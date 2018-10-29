package ca.obrassard;

import ca.obrassard.exception.APIErrorCodes;
import ca.obrassard.exception.APIRequestException;
import ca.obrassard.inquirioCommons.*;
import ca.obrassard.jooqentities.tables.Lostitems;
import ca.obrassard.jooqentities.tables.records.LostitemsRecord;
import ca.obrassard.jooqentities.tables.records.UsersRecord;
import ca.obrassard.model.LostItem;
import ca.obrassard.model.User;
import com.google.common.hash.Hashing;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;

import javax.ws.rs.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ca.obrassard.jooqentities.tables.Lostitems.LOSTITEMS;
import static ca.obrassard.jooqentities.tables.Users.*;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : InquirioWebService.java
 * Date: 19-10-18
 */

@Path("/")
public class InquirioWebService {

    private DSLContext context;

    public InquirioWebService() throws SQLException {
        this.context = DatasourceConfig.getContext();
    }

    @GET
    public String getVersion(){
        return "Inquirio Web API v1.0 - All systems are operational";
    }

    /**
     * Permet de savoir si une adresse courriel
     * est associé à un compte d'utlisateur
     * @param request adresse à vérifier
     * @return True si un compte correspond à l'adresse
     */
    @POST
    @Path("checksubscription")
    public RequestResult isSubscribed(SubscriptionCheckRequest request) throws APIRequestException {
        ValidationUtil.validateEmail(request.email);
        boolean exists = context.fetchExists(context.selectOne().from(USERS).where(USERS.EMAIL.eq(request.email)));
        return new RequestResult(exists);
    }

    /**
     * Tente une authentfication au service d'un utilisateur existant
     * @param loginRequest adresse couriel de l'utlilisateur mot de passe de l'utilisateur
     * @return LoginResponse
     */

    @POST
    @Path("login")
    public LoginResponse login(LoginRequest loginRequest) throws APIRequestException {

        ValidationUtil.validateEmail(loginRequest.email);

        String hashedPasswd = Hashing.sha256().hashString(loginRequest.password, StandardCharsets.UTF_8).toString();

        UsersRecord usersRecord = context.selectFrom(USERS).where(USERS.EMAIL.eq(loginRequest.email).and(USERS.PASSWORDHASH.eq(hashedPasswd))).fetchAny();

        try{
            User user = new User(usersRecord);
            LoginResponse response = new LoginResponse();
            response.userPhoneNumber = user.Telephone;
            response.userID = user.Id;
            response.userFullName = user.Name;
            response.result = true;
            response.isFirstLogin = false;
            return response;
        } catch (NullPointerException e){
            throw new APIRequestException(APIErrorCodes.BadCredentials);
        }
    }

    /**
     * Inscrit un nouvel utilisateur au service
     * @param userInfos Données d'utlisateurs pour l'inscription
     * @return LoginResponse
     */

    @POST
    @Path("signup")
    public LoginResponse signup(SignupRequest userInfos) throws APIRequestException {

        ValidationUtil.validateEmail(userInfos.email);
        ValidationUtil.emailIsUnique(userInfos.email, context);
        ValidationUtil.validateNewPassword(userInfos.password, userInfos.passwdConfirmation);
        ValidationUtil.isRequired("email",userInfos.fullName);
        ValidationUtil.validatePhone(userInfos.cellNumber);

        String hashedPasswd = Hashing.sha256().hashString(userInfos.password, StandardCharsets.UTF_8).toString();

        context.insertInto(USERS, USERS.NAME, USERS.EMAIL, USERS.TELEPHONE, USERS.PASSWORDHASH)
                .values(userInfos.fullName, userInfos.email, userInfos.cellNumber, hashedPasswd)
                .execute();

        UsersRecord usersRecord = context.selectFrom(USERS).where(USERS.EMAIL.eq(userInfos.email).and(USERS.PASSWORDHASH.eq(hashedPasswd))).fetchOne();
        User user = new User(usersRecord);

        LoginResponse response = new LoginResponse();
        response.isFirstLogin = true;
        response.result = true;
        response.userFullName = user.Name;
        response.userID = user.Id ;
        response.userPhoneNumber = user.Telephone;

        return response;
    }

    /**
     * Effectue une déconnexion de l'utlisateur spécifié
     *
     * @param userID Id de l'utilisateur à déconnecter
     * @return LogoutResponse
     */
    @GET
    @Path("logout/{id}")
    public LogoutResponse logout(@PathParam("id") int userID) throws APIRequestException {

        ValidationUtil.isAnExistantUserID(userID, context);

        //TODO : Détruire le cookie (le token)

        LogoutResponse response = new LogoutResponse();
        response.message = "Success";
        response.success = true;
        return response;
    }

    /**
     * Obtiens une liste des items perdus à proximité de l'emplacement
     * actuel de l'utlisateur
     *
     * @param currentLocation Emplacement de l'appareil
     * @return Une liste sommaire des items perdus à proximité
     */

    @POST
    @Path("items/near")
    public List<LostItemSummary> getNearLostItems(LocationRequest currentLocation) throws APIRequestException {

        ValidationUtil.isAValidLocation(currentLocation);

        String query = String.format("CALL getNearItems(%s, %s);",currentLocation.latitude,currentLocation.longitude);
        Result<Record> results = context.fetch(query);

        List<LostItemSummary> lostItemSummaries = new ArrayList<>();
        for (Record record : results){
            LostItemSummary lis = new LostItemSummary();
            lis.itemID = (Integer)record.get("Id");
            lis.itemName = (String)record.get("Title");
            lis.locationName = (String)record.get("LocationName");
            lis.found = (Boolean) record.get("ItemHasBeenFound");
            lis.distance = (Double) record.get("distance");
            lostItemSummaries.add(lis);
        }
        return lostItemSummaries;
    }

    /**
     * Obtiens les details d'un utilisateur
     *
     * @param userID identifiant de l'utilsateur
     * @return un objet User
     */
    @GET
    @Path("users/{id}")
    public User getUserDetail(@PathParam("id") int userID) throws APIRequestException {
       UsersRecord record = context.selectFrom(USERS).where(USERS.ID.eq(userID)).fetchOne();
       if (record == null){
           throw new APIRequestException(APIErrorCodes.UnknownUserId);
       }
       return new User(record);
    }

    /**
     * Ajoute un nouvel item perdu
     *
     * @param item Detail de l'item à ajouter
     * @return L'ID de l'item ajouté
     */
    @POST
    @Path("items")
    public Long addNewItem(LostItemCreationRequest item) throws APIRequestException {
        ValidationUtil.isRequired("locationName", item.locationName);
        ValidationUtil.isRequired("title",item.title);
        ValidationUtil.isRequired("description",item.description);
        ValidationUtil.respectMaxLength("title",item.title,150);
        ValidationUtil.respectMaxLength("description",item.description,250);
        ValidationUtil.isAPositiveNumber("reward",item.reward);
        ValidationUtil.isAnExistantUserID(item.ownerId,context);
        ValidationUtil.isAValidLocation(item.latitude, item.longitude);

        context.insertInto(LOSTITEMS, LOSTITEMS.TITLE, LOSTITEMS.DESCRIPTION, LOSTITEMS.REWARD,
                LOSTITEMS.OWNERID, LOSTITEMS.LONGITUDE,LOSTITEMS.LATTITUDE, LOSTITEMS.LOCATIONNAME)
                .values(item.title, item.description, item.reward, item.ownerId, item.longitude,
                        item.latitude, item.locationName).execute();

        Record result =  context.select(LOSTITEMS.ID).from(LOSTITEMS)
                .where(LOSTITEMS.OWNERID.eq(item.ownerId))
                .orderBy(LOSTITEMS.ID.desc()).fetchAny();
        int id = (int) result.get("Id");
        return new Long(id);
    }

    /**
     * Obtiens les details d'un item
     *
     * @param itemID Identifiant de l'item
     * @return Les details de l'item
     */
    @GET
    @Path("items/{id}")
    public LostItem getItemDetail(@PathParam("id") int itemID) throws APIRequestException {
        LostitemsRecord record = context.selectFrom(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).fetchOne();

        if (record == null) {
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }
        return new LostItem(record);
    }

    /**
     * Obtiens la location (lat + lng) d'un item
     * @param itemID Identifiant de l'item
     * @return L'emplacement de l'item
     */
    @GET
    @Path("items/{id}/location")
    public Location getItemLocation(@PathParam("id") int itemID) throws APIRequestException {
        LostitemsRecord record = context.selectFrom(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).fetchOne();

        if (record == null) {
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }

        LostItem li = new LostItem(record);
        return li.getLocation();
    }

    /**
     * Supprime un item
     * @param itemID identifiant de l'id
     * @return True si la suppression s'est bien déroulée
     */
    @DELETE
    @Path("items/{id}")
    public RequestResult deleteItem(@PathParam("id") int itemID) throws APIRequestException {
        //TODO : Vérifier que l'item est bien celui de la personne !
        int result = context.deleteFrom(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).execute();
        if (result != 1){
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }
        return new RequestResult(true);
    }

    /**
     * Obtiens le nom d'un item
     *
     * @param itemID id
     * @return nom de l'item
     */
    @GET
    @Path("items/{id}/title")
    public String getItemName(@PathParam("id") int itemID) throws APIRequestException {

        Record record = context.select(LOSTITEMS.TITLE).from(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).fetchOne();

        if (record == null) {
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }
        return record.get("Title").toString();
    }

    /**
     * Permet d'envoyer une requete pour signifier
     * qu'un objet à potentiellement été trouvé
     *
     * @param request
     * @return True si tout s'est déroulé correctement
     */
    
    public RequestResult sendFoundRequest(FoundRequest request) {
        return null;
    }

    /**
     * Obtien une liste sommarisée des items
     * actuellement perdus d'un utilisateur
     *
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */
    
    public List<LostItemSummary> getLostItemsByOwner(long userID) {
        return null;
    }

    /**
     * Obtien une liste sommarisée des items
     * retrouvés d'un utilisateur
     *
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */
    
    public List<FoundItemSummary> getFoundItemsByOwner(long userID) {
        return null;
    }

    /**
     * Obtiens une liste sommarisée de notifications
     * de nouveaux items potentiellements retrouvés
     *
     * @param userID Utilisateur a qui les notifs sont adressées
     * @return Une liste de NotificationSummary
     */
    
    public List<NotificationSummary> getPotentiallyFoundItems(long userID) {
        return null;
    }

    /**
     * Obtiens les details d'une notification
     * d'un objet potentiellement trouvé
     *
     * @param notificationID identifiant de la notif de candidat
     * @return Details de la Notification
     */
    
    public Notification getNotificationDetail(long notificationID) {
        return null;
    }

    /**
     * Défini une notification d'objet potentillement trouvé
     * comme erronée (l'item proposé n'est pas l'item recherché)
     *
     * @param notificationID identifiant de la notif de candidat
     * @return True si la requête s'est bien déroulée
     */
    
    public Boolean denyCandidateNotification(long notificationID) {
        return null;
    }

    /**
     * Défini l'objet candidat comme accepté
     * (l'objet proposé est l'item recherché) et
     * retourne les informations de contact de la
     * personne qui a retrouvé l'objet;
     *
     * @param notificationID identifiant de la notif de candidat
     * @return Les informations de contact du 'Finder'
     */
    
    public FinderContactDetail acceptCandidateNotification(long notificationID) {
        return null;
    }

    /**
     * Obtiens les details de contact de l'utlisateur
     * ayant retrouvé un item
     *
     * @param notificationID identifiant de la notif candidate
     * @return FinderContactDetail
     */
    
    public FinderContactDetail getFinderContactDetail(long notificationID) {
        return null;
    }

    /**
     * Assigne une note de fiabilité(de 0 à 5) à un utilisateur
     *
     * @param userId id de l'utilisateur à noter
     * @param rating note de 0 à 5 reporésentant la fiabilité
     * @return True si la requête s'est bien déroulée;
     */
    
    public RequestResult rateUser(long userId, int rating) {
        return null;
    }
}
