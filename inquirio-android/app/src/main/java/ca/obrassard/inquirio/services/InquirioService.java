package ca.obrassard.inquirio.services;

import java.util.List;

import ca.obrassard.inquirio.errorHandling.APIRequestException;
import ca.obrassard.inquirio.model.LostItem;
import ca.obrassard.inquirio.model.User;
import ca.obrassard.inquirio.transfer.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InquirioService {

    /**
     * Permet de savoir si une adresse courriel
     * est associé à un compte d'utlisateur
     * @param request adresse à vérifier
     * @return True si un compte correspond à l'adresse
     */
    @POST
    ("api/checksubscription")
    Call<RequestResult> isSubscribed(@Body SubscriptionCheckRequest request);

    /**
     * Tente une authentfication au service d'un utilisateur existant
     * @param loginRequest adresse couriel de l'utlilisateur mot de passe de l'utilisateur
     * @return LoginResponse
     */

    @POST
    ("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    /**
     * Inscrit un nouvel utilisateur au service
     * @param userInfos Données d'utlisateurs pour l'inscription
     * @return LoginResponse
     */

    @POST
    ("api/signup")
    Call<LoginResponse> signup(@Body SignupRequest userInfos);

    /**
     * Effectue une déconnexion de l'utlisateur spécifié
     *
     * @param token token de l'utilisateur à déconnecter
     * @return LogoutResponse
     */
    @GET
    ("api/logout")
    Call<LogoutResponse> logout(@Header("token") int token);

    /**
     * Obtiens une liste des items perdus à proximité de l'emplacement
     * actuel de l'utlisateur
     *
     * @param currentLocation Emplacement de l'appareil
     * @return Une liste sommaire des items perdus à proximité
     */

    @POST
    ("api/items/near")
    Call<List<LostItemSummary>> getNearLostItems(@Body LocationRequest currentLocation, @Header("token") int token);

    /**
     * Obtiens les details d'un utilisateur
     *
     * @param userID identifiant de l'utilsateur
     * @return un objet User
     */
    @GET
    ("api/users/{id}")
    Call<User> getUserDetail(@Path("id") int userID, @Header("token") int token);

    /**
     * Ajoute un nouvel item perdu
     *
     * @param item Detail de l'item à ajouter
     * @return L'ID de l'item ajouté
     */
    @POST
    ("api/items")
    Call<Integer> addNewItem(@Body LostItemCreationRequest item, @Header("token") int token);

    /**
     * Obtiens les details d'un item
     *
     * @param itemID Identifiant de l'item
     * @return Les details de l'item
     */
    @GET
    ("api/items/{id}")
    Call<LostItem> getItemDetail(@Path("id") int itemID, @Header("token") int token);

    /**
     * Obtiens la location (lat + lng) d'un item
     * @param itemID Identifiant de l'item
     * @return L'emplacement de l'item
     */
    @GET
    ("api/items/{id}/location")
    Call<Location> getItemLocation(@Path("id") int itemID, @Header("token") int token);

    /**
     * Supprime un item
     * @param itemID identifiant de l'id
     * @return True si la suppression s'est bien déroulée
     */
    @DELETE
    ("api/items/{id}")
    Call<RequestResult> deleteItem(@Path("id") int itemID, @Header("token") int token);

    /**
     * Obtiens le nom d'un item
     *
     * @param itemID id
     * @return nom de l'item
     */
    @GET
    ("api/items/{id}/title")
    Call<String> getItemName(@Path("id") int itemID, @Header("token") int token);

    /**
     * Permet d'envoyer une requete pour signifier
     * qu'un objet à potentiellement été trouvé
     *
     * @param request
     * @return True si tout s'est déroulé correctement
     */

    @POST
    ("api/notifications")
    Call<RequestResult> sendFoundRequest(@Body FoundRequest request, @Header("token") int token);

    /**
     * Obtien une liste sommarisée des items
     * actuellement perdus d'un utilisateur
     *
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */

    @GET
    ("api/users/{id}/lostitems")
    Call<List<LostItemSummary>> getLostItemsByOwner(@Path("id") int userID, @Header("token") int token);

    /**
     * Obtien une liste sommarisée des items
     * retrouvés d'un utilisateur
     *
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */
    @GET
    ("api/users/{id}/founditems")
    Call<List<FoundItemSummary>> getFoundItemsByOwner(@Path("id") int userID, @Header("token") int token);

    /**
     * Obtiens une liste sommarisée de notifications
     * de nouveaux items potentiellements retrouvés
     *
     * @param userID Utilisateur a qui les notifs sont adressées
     * @return Une liste de NotificationSummary
     */
    @GET
    ("api/users/{id}/notifications")
    Call<List<NotificationSummary>> getPotentiallyFoundItems(@Path("id") int userID, @Header("token") int token);

    /**
     * Obtiens les details d'une notification
     * d'un objet potentiellement trouvé
     *
     * @param notificationID identifiant de la notif de candidat
     * @return Details de la Notification
     */
    @GET
    ("api/notifications/{id}")
    Call<Notification> getNotificationDetail(@Path("id") int notificationID, @Header("token") int token);

    /**
     * Défini une notification d'objet potentillement trouvé
     * comme erronée (l'item proposé n'est pas l'item recherché)
     *
     * @param notificationID identifiant de la notif de candidat
     * @return True si la requête s'est bien déroulée
     */

    //TODO : Le type de retour a été changé ici!
    @GET
    ("api/notifications/{id}/deny")
    Call<RequestResult> denyCandidateNotification(@Path("id") int notificationID, @Header("token") int token);

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
    ("api/notifications/{id}/accept")
    Call<FinderContactDetail> acceptCandidateNotification(@Path("id") int notificationID, @Header("token") int token);

    /**
     * Obtiens les details de contact de l'utlisateur
     * ayant retrouvé un item
     *
     * @param notificationID identifiant de la notif candidate
     * @return FinderContactDetail
     */

    @GET
    ("api/notifications/{id}/contact")
    Call<FinderContactDetail> getFinderContactDetail(@Path("id") int notificationID, @Header("token") int token);

}
