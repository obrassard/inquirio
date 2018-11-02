package ca.obrassard;

import ca.obrassard.exception.APIErrorCodes;
import ca.obrassard.exception.APIRequestException;
import ca.obrassard.inquirioCommons.*;
import ca.obrassard.model.LostItem;
import ca.obrassard.model.Notification;
import ca.obrassard.model.User;
import com.google.common.hash.Hashing;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.ws.rs.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ca.obrassard.jooqentities.tables.records.LostitemsRecord;
import ca.obrassard.jooqentities.tables.records.NotificationRecord;
import ca.obrassard.jooqentities.tables.records.UsersRecord;
import static ca.obrassard.jooqentities.tables.Lostitems.LOSTITEMS;
import static ca.obrassard.jooqentities.tables.Notification.NOTIFICATION;
import static ca.obrassard.jooqentities.tables.Users.USERS;

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
        Validator.validateEmail(request.email);
        boolean exists = context.fetchExists(context.selectOne().from(USERS).where(USERS.EMAIL.eq(request.email)));
        System.out.println("Subscription checked on " + DateTime.now().toString());
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

        Validator.validateEmail(loginRequest.email);

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

            System.out.println("User ("+user.Email+") signed in on " + DateTime.now().toString());

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

        Validator.validateEmail(userInfos.email);
        Validator.emailIsUnique(userInfos.email, context);
        Validator.validateNewPassword(userInfos.password, userInfos.passwdConfirmation);
        Validator.isRequired("fullname",userInfos.fullName);
        Validator.validatePhone(userInfos.cellNumber);

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
        System.out.println("User ("+user.Id+") signed up on " + DateTime.now().toString());
        return response;
    }

    /**
     * Effectue une déconnexion de l'utlisateur spécifié
     *
     * @param userID Id de l'utilisateur à déconnecter
     * @return LogoutResponse
     */
    @GET
    @Path("logout")
    public LogoutResponse logout(@HeaderParam("token") int token) throws APIRequestException {

         //TODO : Validate token
         int authUserId = AuthValidator.validateToken(token,context);

        //TODO : Détruire le cookie (le token)

        LogoutResponse response = new LogoutResponse();
        response.message = "Success";
        response.success = true;
        System.out.println("User ("+authUserId+") logged out on " + DateTime.now().toString());
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
    public List<LostItemSummary> getNearLostItems(LocationRequest currentLocation, @HeaderParam("token") int token) throws APIRequestException {

        //TODO : Validate token
        int authUserId = AuthValidator.validateToken(token,context);
        
        Validator.isAValidLocation(currentLocation);
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

        //TODO : Get user id with token
        System.out.println("User ("+authUserId+") requested near items on " + DateTime.now().toString());
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
    public User getUserDetail(@PathParam("id") int userID, @HeaderParam("token") int token) throws APIRequestException {
        //TODO : Validate token 
        int authUserId = AuthValidator.validateToken(token,context);
        
        if (authUserId != userID){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }

        UsersRecord record = context.selectFrom(USERS).where(USERS.ID.eq(userID)).fetchOne();
        if (record == null){
           throw new APIRequestException(APIErrorCodes.UnknownUserId);
        }
        
        //TODO : Get user id with token
        System.out.println("User ("+authUserId+") requested user #"+userID+"'s detail on " + DateTime.now().toString());
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
    public int addNewItem(LostItemCreationRequest item, @HeaderParam("token") int token) throws APIRequestException {
        //TODO : Validate token
        int authUserId = AuthValidator.validateToken(token,context);
        
        Validator.isRequired("locationName", item.locationName);
        Validator.isRequired("title",item.title);
        Validator.isRequired("description",item.description);
        Validator.respectMaxLength("title",item.title,150);
        Validator.respectMaxLength("description",item.description,250);
        Validator.isAPositiveNumber("reward",item.reward);
        Validator.isAnExistantUserID(authUserId,context);
        Validator.isAValidLocation(item.latitude, item.longitude);

        context.insertInto(LOSTITEMS, LOSTITEMS.TITLE, LOSTITEMS.DESCRIPTION, LOSTITEMS.REWARD,
                LOSTITEMS.OWNERID, LOSTITEMS.LONGITUDE,LOSTITEMS.LATTITUDE, LOSTITEMS.LOCATIONNAME)
                .values(item.title, item.description, item.reward, authUserId, item.longitude,
                        item.latitude, item.locationName).execute();

        Record result =  context.select(LOSTITEMS.ID).from(LOSTITEMS)
                .where(LOSTITEMS.OWNERID.eq(authUserId))
                .orderBy(LOSTITEMS.ID.desc()).fetchAny();
        int id = (int) result.get("Id");
        System.out.println("User ("+authUserId+") declared a new lost item on " + DateTime.now().toString());
        return id;
    }

    /**
     * Obtiens les details d'un item
     *
     * @param itemID Identifiant de l'item
     * @return Les details de l'item
     */
    @GET
    @Path("items/{id}")
    public LostItem getItemDetail(@PathParam("id") int itemID, @HeaderParam("token") int token) throws APIRequestException {
        //TODO : Validate token
        int authUserId = AuthValidator.validateToken(token,context);
        
        LostitemsRecord record = context.selectFrom(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).fetchOne();

        if (record == null) {
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }
        System.out.println("User ("+authUserId+") requested item #"+ itemID + "'s details on " + DateTime.now().toString());
        return new LostItem(record);
    }

    /**
     * Obtiens la location (lat + lng) d'un item
     * @param itemID Identifiant de l'item
     * @return L'emplacement de l'item
     */
    @GET
    @Path("items/{id}/location")
    public Location getItemLocation(@PathParam("id") int itemID, @HeaderParam("token") int token) throws APIRequestException {
        //TODO : Validate token
        int authUserId = AuthValidator.validateToken(token,context);
        
        LostitemsRecord record = context.selectFrom(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).fetchOne();

        if (record == null) {
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }

        LostItem li = new LostItem(record);
        System.out.println("User ("+authUserId+") requested item #"+ itemID + "'s location on " + DateTime.now().toString());
        return li.getLocation();
        
    }

    /**
     * Supprime un item
     * @param itemID identifiant de l'id
     * @return True si la suppression s'est bien déroulée
     */
    @DELETE
    @Path("items/{id}")
    public RequestResult deleteItem(@PathParam("id") int itemID, @HeaderParam("token") int token) throws APIRequestException {
        
        //TODO : Validate Token
        int authUserId = AuthValidator.validateToken(token,context);

        if (!context.fetchExists(context.selectOne().from(LOSTITEMS)
                .where(LOSTITEMS.ID.eq(itemID).and(LOSTITEMS.OWNERID.eq(authUserId))))){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }
        
        int result = context.deleteFrom(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).execute();
        if (result != 1){
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }
        System.out.println("User ("+authUserId+") deleted item #"+ itemID + " on " + DateTime.now().toString());
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
    public String getItemName(@PathParam("id") int itemID, @HeaderParam("token") int token) throws APIRequestException {

        //TODO : Validate Token
        int authUserId = AuthValidator.validateToken(token,context);
        
        Record record = context.select(LOSTITEMS.TITLE).from(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).fetchOne();

        if (record == null) {
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }
        System.out.println("User ("+authUserId+") requested item #"+ itemID + "'s title on " + DateTime.now().toString());
        return record.get("Title").toString();
    }

    /**
     * Permet d'envoyer une requete pour signifier
     * qu'un objet à potentiellement été trouvé
     *
     * @param request
     * @return True si tout s'est déroulé correctement
     */

    @POST
    @Path("notifications")
    public RequestResult sendFoundRequest(FoundRequest request, @HeaderParam("token") int token) throws APIRequestException {
        //TODO : Validate Token
        int authUserId = AuthValidator.validateToken(token,context);
        
        if (request.senderID != authUserId){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }
        
        Validator.isRequired("message",request.message);
        Validator.isAnExistantItemID(request.itemID, context);

        context.insertInto(NOTIFICATION, NOTIFICATION.ITEMID, NOTIFICATION.SENDERID, NOTIFICATION.MESSAGE,NOTIFICATION.PHOTO)
                .values(request.itemID,request.senderID,request.message,request.image).execute();

        System.out.println("User ("+authUserId+") send new notification for item #"+ request.itemID + " on " + DateTime.now().toString());
        return new RequestResult(true);
    }

    /**
     * Obtien une liste sommarisée des items
     * actuellement perdus d'un utilisateur
     *
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */

    @GET
    @Path("users/{id}/lostitems")
    public List<LostItemSummary> getLostItemsByOwner(@PathParam("id") int userID, @HeaderParam("token") int token) throws APIRequestException {

        //TODO : Validate Token
        int authUserId = AuthValidator.validateToken(token,context);
        if (userID != authUserId){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }
        
        Result<LostitemsRecord> result = context.selectFrom(LOSTITEMS)
                    .where(LOSTITEMS.OWNERID.eq(userID).and(LOSTITEMS.ITEMHASBEENFOUND.eq((byte)0)))
                    .fetch();

        List<LostItemSummary> lostItemSummaries = new ArrayList<>();
        for (LostitemsRecord item : result){
            LostItemSummary lis = new LostItemSummary();
            lis.distance = -1;
            lis.found = true;
            lis.locationName = item.getLocationname();
            lis.itemName = item.getTitle();
            lis.itemID = item.getId();
            lostItemSummaries.add(lis);
        }
        System.out.println("User ("+authUserId+") requested his lost items list on " + DateTime.now().toString());
        return lostItemSummaries;
    }

    /**
     * Obtien une liste sommarisée des items
     * retrouvés d'un utilisateur
     *
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */
    @GET
    @Path("users/{id}/founditems")
    public List<FoundItemSummary> getFoundItemsByOwner(@PathParam("id") int userID, @HeaderParam("token") int token) throws APIRequestException {
        
        //TODO : Validate Real Token
        int authUserId = AuthValidator.validateToken(token,context);
        if (userID != authUserId){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }

        Result<Record> result = context.select().from(LOSTITEMS.innerJoin(USERS).on(LOSTITEMS.FINDERID.eq(USERS.ID)))
                .where(LOSTITEMS.OWNERID.eq(userID).and(LOSTITEMS.ITEMHASBEENFOUND.eq((byte)1)))
                .fetch();

        List<FoundItemSummary> foundItemSummaries = new ArrayList<>();
        for (Record item : result){
            FoundItemSummary fis = new FoundItemSummary();
            fis.found = true;
            fis.finderName = item.get("Name").toString();
            fis.itemName = item.get("Title").toString();
            fis.itemID = (int)item.get("Id");
            foundItemSummaries.add(fis);
        }
        System.out.println("User ("+authUserId+") requested his found items list on " + DateTime.now().toString());
        return foundItemSummaries;
    }

    /**
     * Obtiens une liste sommarisée de notifications
     * de nouveaux items potentiellements retrouvés
     *
     * @param userID Utilisateur a qui les notifs sont adressées
     * @return Une liste de NotificationSummary
     */
    @GET
    @Path("users/{id}/notifications")
    public List<NotificationSummary> getPotentiallyFoundItems(@PathParam("id") int userID, @HeaderParam("token") int token) throws APIRequestException {

        //TODO : Validate Real Token
        int authUserId = AuthValidator.validateToken(token,context);
        if (userID != authUserId){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }

        Result<Record> result = context.select().from(NOTIFICATION.innerJoin(LOSTITEMS)
                .on(LOSTITEMS.ID.eq(NOTIFICATION.ITEMID))
                .innerJoin(USERS).on(USERS.ID.eq(NOTIFICATION.SENDERID)))
                .where(LOSTITEMS.OWNERID.eq(userID)
                .and(NOTIFICATION.VISIBLE.eq((byte)1)))
                .fetch();

        List<NotificationSummary> notifs = new ArrayList<>();

        for (Record item : result){
            NotificationSummary ns = new NotificationSummary();
            ns.itemName = item.get("Title").toString();
            ns.notificationID = (int)item.get(0);
            ns.senderName = item.get("Name").toString();
            notifs.add(ns);
        }

        System.out.println("User ("+authUserId+") requested his notification list on " + DateTime.now().toString());
        return notifs;
    }

    /**
     * Obtiens les details d'une notification
     * d'un objet potentiellement trouvé
     *
     * @param notificationID identifiant de la notif de candidat
     * @return Details de la Notification
     */
    @GET
    @Path("notifications/{id}")
    public ca.obrassard.model.Notification getNotificationDetail(@PathParam("id") int notificationID, @HeaderParam("token") int token) throws APIRequestException {


        //TODO : Validate Real Token
        int authUserId = AuthValidator.validateToken(token,context);

        NotificationRecord record = context.selectFrom(NOTIFICATION).where(NOTIFICATION.ID.eq(notificationID)).fetchOne();
        if (record == null){
            throw new APIRequestException(APIErrorCodes.UnknownNotificationID);
        }

        LostitemsRecord itrecord = context.selectFrom(LOSTITEMS).where(LOSTITEMS.ID.eq(record.getItemid())).fetchOne();
        if (itrecord.getOwnerid() != authUserId){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }

        System.out.println("User ("+authUserId+") requested notification #"+notificationID+"'s details on " + DateTime.now().toString());
        return new ca.obrassard.model.Notification(record);
    }

    /**
     * Défini une notification d'objet potentillement trouvé
     * comme erronée (l'item proposé n'est pas l'item recherché)
     *
     * @param notificationID identifiant de la notif de candidat
     * @return True si la requête s'est bien déroulée
     */

    //TODO : Le type de retour a été changé ici!
    @GET
    @Path("notifications/{id}/deny")
    public RequestResult denyCandidateNotification( @PathParam("id") int notificationID, @HeaderParam("token") int token) throws APIRequestException {
        //TODO : Validate Real Token
        int authUserId = AuthValidator.validateToken(token,context);

        Record ownerid = context.select(LOSTITEMS.OWNERID)
            .from(NOTIFICATION.innerJoin(LOSTITEMS).on(LOSTITEMS.ID.eq(NOTIFICATION.ITEMID)))
            .where(NOTIFICATION.ID.eq(notificationID)).fetchOne();
        if (ownerid == null){
            throw new APIRequestException(APIErrorCodes.UnknownNotificationID);
        } else if ((int)ownerid.get(0) != authUserId){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }

         context.update(NOTIFICATION)
            .set(NOTIFICATION.VISIBLE, (byte) 0)
            .where(NOTIFICATION.ID.eq(notificationID)).execute();

        System.out.println("User ("+authUserId+") denied notification #"+notificationID+"'s proposal on " + DateTime.now().toString());
        return new RequestResult(true);
    }
    /**
     * Défini l'objet candidat comme accepté
     * (l'objet proposé est l'item recherché) et
     * retourne les informations de contact de la
     * personne qui a retrouvé l'objet;
     *
     * @param notificationID identifiant de la notif de candidat
     * @return Les informations de contact du 'sender'
     */

    @GET
    @Path("notifications/{id}/accept")
    public FinderContactDetail acceptCandidateNotification( @PathParam("id") int notificationID, @HeaderParam("token") int token) throws APIRequestException {
        //TODO : Validate Real Token
        int authUserId = AuthValidator.validateToken(token,context);

        try {
            Notification notification = new Notification(
                    context.selectFrom(NOTIFICATION)
                    .where(NOTIFICATION.ID.eq(notificationID))
                    .fetchOne()
            );

             Integer itemsowner = (Integer)context.selectFrom(LOSTITEMS)
                    .where(LOSTITEMS.ID.eq(notification.itemId))
                    .fetchOne().get("OwnerId");

             if (itemsowner != authUserId){
                 throw new APIRequestException(APIErrorCodes.Forbidden);
             }

            //Marquer la notif comme vue
            context.update(NOTIFICATION)
                    .set(NOTIFICATION.VISIBLE, (byte) 0)
                    .where(NOTIFICATION.ID.eq(notificationID)).execute();

            //Enregistrer l'item comme retrouvé
            context.update(LOSTITEMS)
                    .set(LOSTITEMS.ITEMHASBEENFOUND, (byte) 1)
                    .set(LOSTITEMS.FINDERID, notification.senderId)
                    .where(LOSTITEMS.ID.eq(notification.itemId)).execute();

            String telephone = context.selectFrom(USERS)
                    .where(USERS.ID.eq(notification.senderId))
                    .fetchOne().get("Telephone").toString();

            System.out.println("User ("+authUserId+") accepted notification #"+notificationID+"'s proposal on " + DateTime.now().toString());
            return new FinderContactDetail(telephone);

        } catch (NullPointerException e){
            throw new APIRequestException(APIErrorCodes.UnknownNotificationID);
        }
    }

    /**
     * Obtiens les details de contact de l'utlisateur
     * ayant retrouvé un item
     *
     * @param notificationID identifiant de la notif candidate
     * @return FinderContactDetail
     */

    @GET
    @Path("notifications/{id}/contact")
    public FinderContactDetail getFinderContactDetail(@PathParam("id") int notificationID, @HeaderParam("token") int token) throws APIRequestException {

        //TODO : Validate Real Token
        int authUserId = AuthValidator.validateToken(token,context);

        Record ownerid = context.select(LOSTITEMS.OWNERID)
                .from(NOTIFICATION.innerJoin(LOSTITEMS).on(LOSTITEMS.ID.eq(NOTIFICATION.ITEMID)))
                .where(NOTIFICATION.ID.eq(notificationID)).fetchOne();
        if (ownerid == null){
            throw new APIRequestException(APIErrorCodes.UnknownNotificationID);
        } else if ((int)ownerid.get(0) != authUserId){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }

        Record result = context.select().from(NOTIFICATION.innerJoin(USERS)
                .on(USERS.ID.eq(NOTIFICATION.SENDERID))).fetchOne();

        System.out.println("User ("+authUserId+") requested contact details for notification #"+notificationID+" on " + DateTime.now().toString());
        return new FinderContactDetail(result.get("Telephone").toString());
    }

    /**
     * Assigne une note de fiabilité(de 0 à 5) à un utilisateur
     *
     * @param userId id de l'utilisateur à noter
     * @param rating note de 0 à 5 reporésentant la fiabilité
     * @return True si la requête s'est bien déroulée;
     */

    //TODO : Est-ce vraiment utile ?
    public RequestResult rateUser(long userId, int rating) {
        throw new NotImplementedException();
    }
}
