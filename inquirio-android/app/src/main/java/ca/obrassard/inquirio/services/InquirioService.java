package ca.obrassard.inquirio.services;

import java.util.List;

import ca.obrassard.inquirio.model.LostItem;
import ca.obrassard.inquirio.transfer.Notification;
import ca.obrassard.inquirio.model.User;
import ca.obrassard.inquirio.transfer.*;
import retrofit2.Call;
import retrofit2.http.GET;

public interface InquirioService {

    //region [  Login/Signup/Logout  ]

    /**
     * Permet de savoir si une adresse courriel
     * est associé à un compte d'utlisateur
     * @param email adresse à vérifier
     * @return True si un compte correspond à l'adresse
     */
    @GET("/")
    Call<RequestResult> isSubscribed(String email);

    /**
     * Tente une authentfication au service d'un utilisateur
     * existant
     * @param email adresse couriel de l'utlilisateur
     * @param password mot de passe de l'utilisateur
     * @return LoginResponse
     */
    @GET("/")
    Call<LoginResponse> login(String email, String password);

    /**
     * Inscrit un nouvel utilisateur au service
     * @param userInfos Données d'utlisateurs pour l'inscription
     * @return LoginResponse
     */
    @GET("/")
    Call<LoginResponse> signup(SignupRequest userInfos);

    /**
     * Effectue une déconnexion de l'utlisateur spécifié
     * @param userID Id de l'utilisateur à déconnecter
     * @return LogoutResponse
     */
    @GET("/")
    Call<LogoutResponse> logout(long userID);
    //endregion

    //region [  MainActivity  ]

    /**
     * Obtiens une liste des items perdus à proximité de l'emplacement
     * actuel de l'utlisateur
     * @param currentLocation Emplacement de l'appareil
     * @return Une liste sommaire des items perdus à proximité
     */
    Call<List<LostItemSummary>> getNearLostItems(LocationRequest currentLocation);
    //endregion
    
    //region[  AccountActivity  ]

    /**
     * Obtiens les details d'un utilisateur
     * @param userID identifiant de l'utilsateur
     * @return un objet User
     */
    Call<User> getUserDetail(long userID);
    //endregion
    
    //region [  AddItemActivity  ]

    /**
     * Ajoute un nouvel item perdu
     * @param item Detail de l'item à ajouter
     * @return L'ID de l'item ajouté ou -1
     */
    Call<Long> addNewItem(NewItemRequest item);


    //endregion

    //region[  ItemsDetailActivity  ]

    /**
     * Obtiens les details d'un item
     * @param itemID Identifiant de l'item
     * @return Les details de l'item
     */
    Call<LostItem> getItemDetail(long itemID);

    /**
     * Obtiens la location (lat + lng) d'un item
     * @param itemID Identifiant de l'item
     * @return L'emplacement de l'item
     */
    Call<Location> getItemLocation(long itemID);

    /**
     * Supprime un item
     * @param itemID identifiant de l'id
     * @return True si la suppression s'est bien déroulée
     */
    Call<RequestResult> deleteItem(long itemID);

    /**
     * Permet d'ajouter un candidat à l'objet retrouvé
     * (Dire que l'item a été retrouvé, en attente d'aprobabtion par le propriétaire)
     * @param candidate Information relative a l'objet retrouvé
     * @return True si la requête s'est bien déroulée
     */
    Call<RequestResult> addFoundCandidate(FoundCandidate candidate);
    //endregion

    // region [ ItemFoundActivity ]

    /**
     * Obtiens le nom d'un item
     * @param itemID id
     * @return nom de l'item
     */
    Call<String> getItemName(long itemID);

    /**
     * Permet d'envoyer une requete pour signifier
     * qu'un objet à potentiellement été trouvé
     * @param resquest
     * @return True si tout s'est déroulé correctement
     */
    Call<RequestResult> sendFoundRequest(FoundRequest resquest);

    //endregion

    //region [  MyItemsActivity ]

    /**
     * Obtien une liste sommarisée des items
     * actuellement perdus d'un utilisateur
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */
    Call<List<LostItemSummary>> getLostItemsByOwner(long userID);

    /**
     * Obtien une liste sommarisée des items
     * retrouvés d'un utilisateur
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */
    Call<List<LostItemSummary>> getFoundItemsByOwner(long userID);
    //endregion
    
    //region [  Notifications ]

    /**
     * Obtiens une liste sommarisée de notifications
     * de nouveaux candidats retrouvés
     * @param userID Utilisateur a qui les notifs sont adressées
     * @return Une liste de NotificationSummary
     */
    Call<List<NotificationSummary>> getCandidateNotifications(long userID);

    /**
     * Obtiens les details d'une notification
     * d'un objet potentiellement trouvé
     * @param notificationID identifiant de la notif de candidat
     * @return Details de la Notification
     */
    Call<Notification> getNotificationDetail(long notificationID);

    /**
     * Défini une notification d'objet potentillement trouvé
     * comme erronée (l'item proposé n'est pas l'item recherché)
     * @param notificationID identifiant de la notif de candidat
     * @return True si la requête s'est bien déroulée
     */
    Call<Boolean> denyCandidateNotification(long notificationID);

    /**
     * Défini l'objet candidat comme accepté
     * (l'objet proposé est l'item recherché) et
     * retourne les informations de contact de la
     * personne qui a retrouvé l'objet;
     * @param notificationID identifiant de la notif de candidat
     * @return Les informations de contact du 'Finder'
     */
    Call<FinderContactDetail> acceptCandidateNotification(long notificationID);

    /**
     * Obtiens les details de contact de l'utlisateur
     * ayant retrouvé un item
     * @param notificationID identifiant de la notif candidate
     * @return FinderContactDetail
     */
    Call<FinderContactDetail> getFinderContactDetail(long notificationID);
    //endregion
    
    // region [ Noter un utilisateur ]

    /**
     * Assigne une note de fiabilité(de 0 à 5) à un utilisateur
     * @param userId id de l'utilisateur à noter
     * @param rating note de 0 à 5 reporésentant la fiabilité
     * @return True si la requête s'est bien déroulée;
     */
    Call<RequestResult> rateUser(long userId, int rating);

    /*
     *  TODO : Trouver une manière de faire noter les gens plus tard sur la fiabilité du Finder...
     */

    //endregion
}
