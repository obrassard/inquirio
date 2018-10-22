package ca.obrassard;

import ca.obrassard.inquirioCommons.*;
import ca.obrassard.model.LostItem;
import ca.obrassard.model.User;
import com.google.common.hash.Hashing;
import org.jooq.DSLContext;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.ws.http.HTTPException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import static ca.obrassard.jooqentities.tables.Users.*;
import static org.jooq.impl.DSL.field;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : InquirioWebService.java
 * Date: 19-10-18
 */

@Path("/")
public class InquirioWebService implements InquirioService {

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
     *
     * @param request adresse à vérifier
     * @return True si un compte correspond à l'adresse
     */
    @POST
    @Path("checksubscription")
    public RequestResult isSubscribed(SubscriptionCheckRequest request) {
        boolean exists = context.fetchExists(context.selectOne().from(USERS).where(USERS.EMAIL.eq(request.email)));
        return new RequestResult(exists);
    }

    /**
     * Tente une authentfication au service d'un utilisateur
     * existant
     *
     * @param loginRequest adresse couriel de l'utlilisateur mot de passe de l'utilisateur
     * @return LoginResponse
     */
    
    public LoginResponse login(LoginRequest loginRequest) {
        if (!ValidationUtil.isValidEmail(loginRequest.email) || loginRequest.password.trim().equals("")){
            throw new HTTPException(400);
        }

        String hashedPasswd = Hashing.sha256().hashString(loginRequest.password, StandardCharsets.UTF_8).toString();
        //TODO : A compléter
        return null;
    }

    /**
     * Inscrit un nouvel utilisateur au service
     *
     * @param userInfos Données d'utlisateurs pour l'inscription
     * @return LoginResponse
     */
    
    public LoginResponse signup(SignupRequest userInfos) {

        //context.insertInto(USERS, USERS. )
        return null;
    }

    /**
     * Effectue une déconnexion de l'utlisateur spécifié
     *
     * @param userID Id de l'utilisateur à déconnecter
     * @return LogoutResponse
     */
    
    public LogoutResponse logout(long userID) {
        return null;
    }

    /**
     * Obtiens une liste des items perdus à proximité de l'emplacement
     * actuel de l'utlisateur
     *
     * @param currentLocation Emplacement de l'appareil
     * @return Une liste sommaire des items perdus à proximité
     */
    
    public List<LostItemSummary> getNearLostItems(LocationRequest currentLocation) {
        return null;
    }

    /**
     * Obtiens les details d'un utilisateur
     *
     * @param userID identifiant de l'utilsateur
     * @return un objet User
     */
    
    public User getUserDetail(long userID) {
        return null;
    }

    /**
     * Ajoute un nouvel item perdu
     *
     * @param item Detail de l'item à ajouter
     * @return L'ID de l'item ajouté ou -1
     */
    
    public Long addNewItem(LostItem item) {
        return null;
    }

    /**
     * Obtiens les details d'un item
     *
     * @param itemID Identifiant de l'item
     * @return Les details de l'item
     */
    
    public LostItem getItemDetail(long itemID) {
        return null;
    }

    /**
     * Obtiens la location (lat + lng) d'un item
     * @param itemID Identifiant de l'item
     * @return L'emplacement de l'item
     */
    
    public Location getItemLocation(long itemID) {
        return null;
    }

    /**
     * Supprime un item
     * @param itemID identifiant de l'id
     * @return True si la suppression s'est bien déroulée
     */
    
    public RequestResult deleteItem(long itemID) {
        return null;
    }

    /**
     * Obtiens le nom d'un item
     *
     * @param itemID id
     * @return nom de l'item
     */
    
    public String getItemName(long itemID) {
        return null;
    }

    /**
     * Permet d'envoyer une requete pour signifier
     * qu'un objet à potentiellement été trouvé
     *
     * @param resquest
     * @return True si tout s'est déroulé correctement
     */
    
    public RequestResult sendFoundRequest(FoundRequest resquest) {
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
