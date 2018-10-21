package ca.obrassard;

import ca.obrassard.inquirioCommons.*;
import ca.obrassard.model.LostItem;
import ca.obrassard.model.User;
import org.jooq.DSLContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.sql.SQLException;
import java.util.List;

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
     * @param email adresse à vérifier
     * @return True si un compte correspond à l'adresse
     */
    @Override
    public RequestResult isSubscribed(String email) {
        return null;
    }

    /**
     * Tente une authentfication au service d'un utilisateur
     * existant
     *
     * @param email    adresse couriel de l'utlilisateur
     * @param password mot de passe de l'utilisateur
     * @return LoginResponse
     */
    @Override
    public LoginResponse login(String email, String password) {
        return null;
    }

    /**
     * Inscrit un nouvel utilisateur au service
     *
     * @param userInfos Données d'utlisateurs pour l'inscription
     * @return LoginResponse
     */
    @Override
    public LoginResponse signup(SignupRequest userInfos) {
        return null;
    }

    /**
     * Effectue une déconnexion de l'utlisateur spécifié
     *
     * @param userID Id de l'utilisateur à déconnecter
     * @return LogoutResponse
     */
    @Override
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
    @Override
    public List<LostItemSummary> getNearLostItems(LocationRequest currentLocation) {
        return null;
    }

    /**
     * Obtiens les details d'un utilisateur
     *
     * @param userID identifiant de l'utilsateur
     * @return un objet User
     */
    @Override
    public User getUserDetail(long userID) {
        return null;
    }

    /**
     * Ajoute un nouvel item perdu
     *
     * @param item Detail de l'item à ajouter
     * @return L'ID de l'item ajouté ou -1
     */
    @Override
    public Long addNewItem(LostItem item) {
        return null;
    }

    /**
     * Obtiens les details d'un item
     *
     * @param itemID Identifiant de l'item
     * @return Les details de l'item
     */
    @Override
    public LostItem getItemDetail(long itemID) {
        return null;
    }

    /**
     * Obtiens la location (lat + lng) d'un item
     * @param itemID Identifiant de l'item
     * @return L'emplacement de l'item
     */
    @Override
    public Location getItemLocation(long itemID) {
        return null;
    }

    /**
     * Supprime un item
     * @param itemID identifiant de l'id
     * @return True si la suppression s'est bien déroulée
     */
    @Override
    public RequestResult deleteItem(long itemID) {
        return null;
    }

    /**
     * Obtiens le nom d'un item
     *
     * @param itemID id
     * @return nom de l'item
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public RequestResult rateUser(long userId, int rating) {
        return null;
    }
}
