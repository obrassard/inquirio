package ca.obrassard.inquirio;

import com.google.android.gms.location.places.Place;

import java.util.List;

import ca.obrassard.inquirio.model.LostItem;
import ca.obrassard.inquirio.model.Notification;
import ca.obrassard.inquirio.model.User;
import ca.obrassard.inquirio.transfer.*;

public interface RESTMethods {

    //==  Login/Signup/Logout  ============================================

    /**
     * Permet de savoir si une adresse courriel
     * est associé à un compte d'utlisateur
     * @param email adresse à vérifier
     * @return True si un compte correspond à l'adresse
     */
    boolean isSubscribed(String email);

    /**
     * Tente une authentfication au service d'un utilisateur
     * existant
     * @param email adresse couriel de l'utlilisateur
     * @param password mot de passe de l'utilisateur
     * @return LoginResponse
     */
    LoginResponse login(String email, String password);

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


    //==  MainActivity  ============================================

    /**
     * Obtiens une liste des items perdus à proximité de l'emplacement
     * actuel de l'utlisateur
     * @param currentLocation Emplacement de l'appareil
     * @return Une liste sommaire des items perdus à proximité
     */
    List<LostItemSummary> getNearLostItems(LocationRequest currentLocation);

    //==  AccountActivity  =========================================

    /**
     * Obtiens les details d'un utilisateur
     * @param userID identifiant de l'utilsateur
     * @return un objet User
     */
    User getUserDetail(long userID);

    //==  AddItemActivity  =========================================

    /**
     * Ajoute un nouvel item perdu
     * @param item Detail de l'item à ajouter
     * @return L'ID de l'item ajouté ou -1
     */
    long addNewItem(NewItemRequest item);

    /**
     * Modifie les details d'un item
     * @param item Item a modifier, avec ces nouvelles informations
     * @return True si la modification s'est effectuée avec succès
     */
    boolean updateItem(UpdateItemRequest item);


    //==  ItemsDetailActivity  =====================================

    /**
     * Obtiens les details d'un item
     * @param itemID Identifiant de l'item
     * @return Les details de l'item
     */
    LostItem getItemDetail(long itemID);

    /**
     * Supprime un item
     * @param itemID identifiant de l'id
     * @return True si la suppression s'est bien déroulée
     */
    boolean deleteItem(long itemID);

    //== Item trouvé par un utilisateur

    /**
     * Permet d'ajouter un candidat à l'objet retrouvé
     * (Dire que l'item a été retrouvé, en attente d'aprobabtion par le propriétaire)
     * @param candidate Information relative a l'objet retrouvé
     * @return True si la requête s'est bien déroulée
     */
    boolean addFoundCandidate(FoundCandidate candidate);


    //==  MyItemsActivity ==========================================

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
    List<LostItemSummary> getFoundItemsByOwner(long userID);

    //==  NotificationActivity =====================================

    /**
     * Obtiens une liste sommarisée de notifications
     * de nouveaux candidats retrouvés
     * @param userID Utilisateur a qui les notifs sont adressées
     * @return Une liste de NotificationSummary
     */
    List<NotificationSummary> getCandidateNotifications(long userID);

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
    boolean denyCandidateNotification(long notificationID);

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

    //== À voir ... =====================================

    /**
     * Signale un utilisateur / sa notification comme innaproprié ou
     * problématique
     * @param report
     * @return
     */
    ReportResponse reportUser(ReportUserRequest report);

    /**
     * Assigne une note de fiabilité(de 0 à 5) à un utilisateur
     * @param userId id de l'utilisateur à noter
     * @param rating note de 0 à 5 reporésentant la fiabilité
     * @return True si la requête s'est bien déroulée;
     */
    boolean rateUser(long userId, int rating);

    /*
     *  TODO : Trouver une manière de faire noter les gens plus tard sur la fiabilité du Finder...
     */


}
