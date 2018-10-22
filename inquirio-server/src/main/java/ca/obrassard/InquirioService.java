package ca.obrassard;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : InquirioService.java
 * Date: 20-10-18
 */

import java.util.List;
import ca.obrassard.inquirioCommons.*;
import ca.obrassard.model.LostItem;
import ca.obrassard.model.User;


public interface InquirioService {

    //region [  Login/Signup/Logout  ]

    /**
     * Permet de savoir si une adresse courriel
     * est associé à un compte d'utlisateur
     * @param request adresse à vérifier
     * @return True si un compte correspond à l'adresse
     */
    RequestResult isSubscribed(SubscriptionCheckRequest request);

    /**
     * Tente une authentfication au service d'un utilisateur
     * existant
     * @param loginRequest adresse couriel de l'utlilisateur et mot de passe de l'utilisateur
     * @return LoginResponse
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * Inscrit un nouvel utilisateur au service
     * @param userInfos Données d'utlisateurs pour l'inscription
     * @return LoginResponse
     */
    LoginResponse signup(SignupRequest userInfos);

    /**
     * Effectue une déconnexion de l'utlisateur spécifié
     * @param userID Id de l'utilisateur à déconnecter
     * @return LogoutResponse
     */
    LogoutResponse logout(long userID);
    //endregion

    //region [  MainActivity  ]

    /**
     * Obtiens une liste des items perdus à proximité de l'emplacement
     * actuel de l'utlisateur
     * @param currentLocation Emplacement de l'appareil
     * @return Une liste sommaire des items perdus à proximité
     */
    List<LostItemSummary> getNearLostItems(LocationRequest currentLocation);
    //endregion

    //region[  AccountActivity  ]

    /**
     * Obtiens les details d'un utilisateur
     * @param userID identifiant de l'utilsateur
     * @return un objet User
     */
    User getUserDetail(long userID);
    //endregion

    //region [  AddItemActivity  ]

    /**
     * Ajoute un nouvel item perdu
     * @param item Detail de l'item à ajouter
     * @return L'ID de l'item ajouté ou -1
     */
    Long addNewItem(LostItem item);


    //endregion

    //region[  ItemsDetailActivity  ]

    /**
     * Obtiens les details d'un item
     * @param itemID Identifiant de l'item
     * @return Les details de l'item
     */
    LostItem getItemDetail(long itemID);

    /**
     * Obtiens la location (lat + lng) d'un item
     * @param itemID Identifiant de l'item
     * @return L'emplacement de l'item
     */
    Location getItemLocation(long itemID);

    /**
     * Supprime un item
     * @param itemID identifiant de l'id
     * @return True si la suppression s'est bien déroulée
     */
    RequestResult deleteItem(long itemID);
    //endregion

    // region [ ItemFoundActivity ]

    /**
     * Obtiens le nom d'un item
     * @param itemID id
     * @return nom de l'item
     */
    String getItemName(long itemID);

    /**
     * Permet d'envoyer une requete pour signifier
     * qu'un objet à potentiellement été trouvé
     * @param resquest
     * @return True si tout s'est déroulé correctement
     */
    RequestResult sendFoundRequest(FoundRequest resquest);

    //endregion

    //region [  MyItemsActivity ]

    /**
     * Obtien une liste sommarisée des items
     * actuellement perdus d'un utilisateur
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */
    List<LostItemSummary> getLostItemsByOwner(long userID);

    /**
     * Obtien une liste sommarisée des items
     * retrouvés d'un utilisateur
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */
    List<FoundItemSummary> getFoundItemsByOwner(long userID);
    //endregion

    //region [  Notifications ]

    /**
     * Obtiens une liste sommarisée de notifications
     * de nouveaux items potentiellements retrouvés
     * @param userID Utilisateur a qui les notifs sont adressées
     * @return Une liste de NotificationSummary
     */
    List<NotificationSummary> getPotentiallyFoundItems(long userID);

    /**
     * Obtiens les details d'une notification
     * d'un objet potentiellement trouvé
     * @param notificationID identifiant de la notif de candidat
     * @return Details de la Notification
     */
    Notification getNotificationDetail(long notificationID);

    /**
     * Défini une notification d'objet potentillement trouvé
     * comme erronée (l'item proposé n'est pas l'item recherché)
     * @param notificationID identifiant de la notif de candidat
     * @return True si la requête s'est bien déroulée
     */
    Boolean denyCandidateNotification(long notificationID);

    /**
     * Défini l'objet candidat comme accepté
     * (l'objet proposé est l'item recherché) et
     * retourne les informations de contact de la
     * personne qui a retrouvé l'objet;
     * @param notificationID identifiant de la notif de candidat
     * @return Les informations de contact du 'Finder'
     */
    FinderContactDetail acceptCandidateNotification(long notificationID);

    /**
     * Obtiens les details de contact de l'utlisateur
     * ayant retrouvé un item
     * @param notificationID identifiant de la notif candidate
     * @return FinderContactDetail
     */
    FinderContactDetail getFinderContactDetail(long notificationID);
    //endregion

    // region [ Noter un utilisateur ]

    /**
     * Assigne une note de fiabilité(de 0 à 5) à un utilisateur
     * @param userId id de l'utilisateur à noter
     * @param rating note de 0 à 5 reporésentant la fiabilité
     * @return True si la requête s'est bien déroulée;
     */
    RequestResult rateUser(long userId, int rating);


    //endregion
}
