package ca.obrassard;

import ca.obrassard.exception.APIErrorCodes;
import ca.obrassard.exception.APIRequestException;
import ca.obrassard.inquirioCommons.*;
import ca.obrassard.jooqentities.Routines;
import ca.obrassard.jooqentities.tables.records.LostitemsRecord;
import ca.obrassard.jooqentities.tables.records.NotificationRecord;
import ca.obrassard.jooqentities.tables.records.UsersRecord;
import ca.obrassard.model.LostItem;
import ca.obrassard.model.Notification;
import ca.obrassard.model.User;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ca.obrassard.jooqentities.tables.Lostitems.LOSTITEMS;
import static ca.obrassard.jooqentities.tables.Notification.NOTIFICATION;
import static ca.obrassard.jooqentities.tables.Users.USERS;
import static ca.obrassard.jooqentities.tables.Tokens.TOKENS;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : InquirioWebService.java
 * Date: 19-10-18
 */

@Path("/")
public class InquirioWebService {
    private static final String COOKEY = "inquirio.auth";
    private DSLContext context;

    public InquirioWebService() throws SQLException {
        this.context = DatasourceConfig.getContext();
    }

    private void FakeNetworkDelay() throws InterruptedException {
        Thread.sleep(2000);
    }

    //region [Token and authentication]
    private NewCookie CreateToken(int userid) {
        String token = UUID.randomUUID().toString()+"-"+UUID.randomUUID().toString();
        context.insertInto(TOKENS, TOKENS.USERID, TOKENS.TOKEN).values(userid, token).execute();

        return new NewCookie(COOKEY,token,"/","","Inquirio authentication",604800,true);
    }

    private NewCookie DestroyToken(Cookie authCookie){
        if (authCookie != null){
            context.deleteFrom(TOKENS).where(TOKENS.TOKEN.eq(authCookie.getValue())).execute();
        }
        return new NewCookie(COOKEY,null,"/",null,null,0,true);
    }
    //endregion

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

    //Allow anonymous
    @POST
    @Path("checksubscription")
    public RequestResult isSubscribed(SubscriptionCheckRequest request) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();
        Validator.validateEmail(request.email);
        boolean exists = context.fetchExists(context.selectOne().from(USERS).where(USERS.EMAIL.eq(request.email)));
        System.out.println("Subscription checked on " + DateTime.now().toString());
        context.close();
        return new RequestResult(exists);
    }

    /**
     * Tente une authentfication au service d'un utilisateur existant
     * @param loginRequest adresse couriel de l'utlilisateur mot de passe de l'utilisateur
     * @return LoginResponse
     */
    //Allow anonymous
    @POST
    @Path("login")
    public Response login(LoginRequest loginRequest) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();
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

            NewCookie cookie = CreateToken(user.Id);

            System.out.println("User ("+user.Email+") signed in on " + DateTime.now().toString());

            return Response.ok(new Gson().toJson(response), MediaType.APPLICATION_JSON).cookie(cookie).build();
        } catch (NullPointerException e){
            throw new APIRequestException(APIErrorCodes.BadCredentials);
        } finally {
            context.close();
        }
    }

    /**
     * Inscrit un nouvel utilisateur au service
     * @param userInfos Données d'utlisateurs pour l'inscription
     * @return LoginResponse
     */
    @POST
    @Path("signup")
    public Response signup(SignupRequest userInfos) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();
        Validator.validateEmail(userInfos.email);
        Validator.emailIsUnique(userInfos.email, context);
        Validator.validatePhone(userInfos.cellNumber);
        Validator.isRequired("fullname",userInfos.fullName);
        Validator.validateNewPassword(userInfos.password, userInfos.passwdConfirmation);

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

        NewCookie cookie = CreateToken(user.Id);

        System.out.println("User ("+user.Id+") signed up on " + DateTime.now().toString());
        context.close();
        return Response.ok(new Gson().toJson(response), MediaType.APPLICATION_JSON).cookie(cookie).build();
    }

    /**
     * Effectue une déconnexion de l'utlisateur spécifié
     *
     * @param token token de l'utilisateur à déconnecter
     * @return LogoutResponse
     */
    
    @GET
    @Path("logout")
    public Response logout(@CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();

        int authUserId = AuthValidator.validateToken(token,context);
        NewCookie deadCookie = DestroyToken(token);

        LogoutResponse response = new LogoutResponse();
        response.message = "Success";
        response.success = true;
        System.out.println("User ("+authUserId+") logged out on " + DateTime.now().toString());
        context.close();
        return Response.ok(new Gson().toJson(response), MediaType.APPLICATION_JSON).cookie(deadCookie).build();
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
    public List<LostItemSummary> getNearLostItems(LocationRequest currentLocation, @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();
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

        System.out.println("User ("+authUserId+") requested near items on " + DateTime.now().toString());
        context.close();
        return lostItemSummaries;
    }

    /**
     * Obtiens les details d'un utilisateur
     * @param userID identifiant de l'utilsateur
     * @return un objet User
     */
    
    @GET
    @Path("users/{id}")
    public User getUserDetail(@PathParam("id") int userID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {

        FakeNetworkDelay();

        int authUserId = AuthValidator.validateToken(token,context);
        
        if (authUserId != userID){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }

        UsersRecord record = context.selectFrom(USERS).where(USERS.ID.eq(authUserId)).fetchOne();
        if (record == null){
           throw new APIRequestException(APIErrorCodes.UnknownUserId);
        }

        System.out.println("User ("+authUserId+") requested his account details on " + DateTime.now().toString());
        User u = new User(record);
        context.close();
        return u;
    }

    /**
     * Ajoute un nouvel item perdu
     *
     * @param item Detail de l'item à ajouter
     * @return L'ID de l'item ajouté
     */
    
    @POST
    @Path("items")
    public int addNewItem(LostItemCreationRequest item,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();

        int authUserId = AuthValidator.validateToken(token,context);
        Validator.isRequired("title",item.title);
        Validator.respectMaxLength("title",item.title,150);
        Validator.isRequired("description",item.description);
        Validator.respectMaxLength("description",item.description,250);
        Validator.isAPositiveNumber("reward",item.reward);
        Validator.isRequired("locationName", item.locationName);
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
        context.close();
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
    public LostItem getItemDetail(@PathParam("id") int itemID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();

        int authUserId = AuthValidator.validateToken(token,context);
        
        LostitemsRecord record = context.selectFrom(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).fetchOne();

        if (record == null) {
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }
        System.out.println("User ("+authUserId+") requested item #"+ itemID + "'s details on " + DateTime.now().toString());
        LostItem li = new LostItem(record);
        context.close();
        return li;
    }

    /**
     * Obtiens la location (lat + lng) d'un item
     * @param itemID Identifiant de l'item
     * @return L'emplacement de l'item
     */
    
    @GET
    @Path("items/{id}/location")
    public Location getItemLocation(@PathParam("id") int itemID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();

        int authUserId = AuthValidator.validateToken(token,context);
        
        LostitemsRecord record = context.selectFrom(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).fetchOne();

        if (record == null) {
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }

        LostItem li = new LostItem(record);
        System.out.println("User ("+authUserId+") requested item #"+ itemID + "'s location on " + DateTime.now().toString());
        context.close();
        return li.getLocation();
        
    }

    /**
     * Supprime un item
     * @param itemID identifiant de l'id
     * @return True si la suppression s'est bien déroulée
     */
    
    @DELETE
    @Path("items/{id}")
    public RequestResult deleteItem(@PathParam("id") int itemID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();

        int authUserId = AuthValidator.validateToken(token,context);

        //Verify that the user is the owner
        if (!context.fetchExists(context.selectOne().from(LOSTITEMS)
                .where(LOSTITEMS.ID.eq(itemID).and(LOSTITEMS.OWNERID.eq(authUserId))))){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }
        
        int result = context.deleteFrom(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).execute();
        if (result != 1){
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }
        System.out.println("User ("+authUserId+") deleted item #"+ itemID + " on " + DateTime.now().toString());
        context.close();
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
    public StringWrapper getItemName(@PathParam("id") int itemID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();
        int authUserId = AuthValidator.validateToken(token,context);
        
        Record record = context.select(LOSTITEMS.TITLE).from(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).fetchOne();

        if (record == null) {
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        }
        System.out.println("User ("+authUserId+") requested item #"+ itemID + "'s title on " + DateTime.now().toString());
        StringWrapper sw = new StringWrapper(record.get("Title").toString());
        context.close();
        return sw;
    }

    /**
     * Permet d'envoyer une requete pour signifier
     * qu'un objet à potentiellement été trouvé
     *
     * @param request request
     * @return True si tout s'est déroulé correctement
     */
    @POST
    @Path("notifications")
    public RequestResult sendFoundRequest(FoundRequest request,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();
        int authUserId = AuthValidator.validateToken(token,context);
        
        if (request.senderID != authUserId){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }
        
        Validator.isRequired("message",request.message);
        Validator.isAnExistantItemID(request.itemID, context);

        context.insertInto(NOTIFICATION, NOTIFICATION.ITEMID, NOTIFICATION.SENDERID, NOTIFICATION.MESSAGE,NOTIFICATION.PHOTO)
                .values(request.itemID,request.senderID,request.message,request.image).execute();

        System.out.println("User ("+authUserId+") send new notification for item #"+ request.itemID + " on " + DateTime.now().toString());
        context.close();
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
    public List<LostItemSummary> getLostItemsByOwner(@PathParam("id") int userID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();

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
        context.close();
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
    public List<FoundItemSummary> getFoundItemsByOwner(@PathParam("id") int userID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();
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
            fis.finderName = item.get(USERS.NAME);
            fis.itemName = item.get(LOSTITEMS.TITLE);
            fis.itemID = item.get(LOSTITEMS.ID);
            foundItemSummaries.add(fis);
        }

        System.out.println("User ("+authUserId+") requested his found items list on " + DateTime.now().toString());
        context.close();
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
    public List<NotificationSummary> getPotentiallyFoundItems(@PathParam("id") int userID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();

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
        context.close();
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
    public ca.obrassard.inquirioCommons.Notification getNotificationDetail(@PathParam("id") int notificationID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();

        int authUserId = AuthValidator.validateToken(token,context);

        NotificationRecord record = context.selectFrom(NOTIFICATION).where(NOTIFICATION.ID.eq(notificationID)).fetchOne();
        if (record == null){
            throw new APIRequestException(APIErrorCodes.UnknownNotificationID);
        }

        LostitemsRecord itrecord = context.selectFrom(LOSTITEMS).where(LOSTITEMS.ID.eq(record.getItemid())).fetchOne();
        if (itrecord.getOwnerid() != authUserId){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }

        UsersRecord senderrecord = context.selectFrom(USERS).where(USERS.ID.eq(record.getSenderid())).fetchOne();

        ca.obrassard.inquirioCommons.Notification notification = new ca.obrassard.inquirioCommons.Notification();
        notification.date = record.getDate();
        notification.id = record.getId();
        notification.itemName = itrecord.getTitle();
        notification.message = record.getMessage();
        notification.senderName = senderrecord.getName();
        notification.senderRating = senderrecord.getRating();
        notification.photo = record.getPhoto();

        System.out.println("User ("+authUserId+") requested notification #"+notificationID+"'s details on " + DateTime.now().toString());
        context.close();
        return notification;
    }

    /**
     * Défini une notification d'objet potentillement trouvé
     * comme erronée (l'item proposé n'est pas l'item recherché)
     *
     * @param notificationID identifiant de la notif
     * @return True si la requête s'est bien déroulée
     */
    
    @GET
    @Path("notifications/{id}/deny")
    public RequestResult denyCandidateNotification(@PathParam("id") int notificationID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();
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
        context.close();
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
    public FinderContactDetail acceptCandidateNotification(@PathParam("id") int notificationID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();

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

            //MAJ le nombre d'item trouvé de la personne
            Routines.updatefounditemcount(context.configuration(),notification.senderId);

            String telephone = context.selectFrom(USERS)
                    .where(USERS.ID.eq(notification.senderId))
                    .fetchOne().get("Telephone").toString();

            System.out.println("User ("+authUserId+") accepted notification #"+notificationID+"'s proposal on " + DateTime.now().toString());
            return new FinderContactDetail(telephone);

        } catch (NullPointerException e){
            throw new APIRequestException(APIErrorCodes.UnknownNotificationID);
        } finally {
            context.close();
        }
    }

    /**
     * Obtiens les details de contact de l'utlisateur
     * ayant retrouvé un item
     *
     * @param itemID identifiant de l'item trouvé
     * @return FinderContactDetail
     */
    @GET
    @Path("notifications/{id}/contact")
    public FinderContactDetail getFinderContactDetail(@PathParam("id") int itemID,  @CookieParam(COOKEY) Cookie token) throws APIRequestException, InterruptedException {
        FakeNetworkDelay();
        int authUserId = AuthValidator.validateToken(token,context);

        Record ownerid = context.select(LOSTITEMS.OWNERID)
                .from(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)).fetchOne();
        if (ownerid == null){
            throw new APIRequestException(APIErrorCodes.UnknownItemId);
        } else if ((int)ownerid.get(0) != authUserId){
            throw new APIRequestException(APIErrorCodes.Forbidden);
        }

        Record result = context.select().from(LOSTITEMS.innerJoin(USERS)
                .on(USERS.ID.eq(LOSTITEMS.FINDERID))).where(LOSTITEMS.ID.eq(itemID)).fetchOne();

        System.out.println("User ("+authUserId+") requested finder contact details for item #"+itemID+" on " + DateTime.now().toString());
        FinderContactDetail fcd = new FinderContactDetail(result.get("Telephone").toString());
        context.close();
        return fcd;
    }

}
