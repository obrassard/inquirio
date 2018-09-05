package ca.obrassard.inquirio;

import com.google.android.gms.location.places.Place;

import java.util.List;

import ca.obrassard.inquirio.model.LostItem;
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

    //TODO : Edit un item

    //==  ItemsDetailActivity  =====================================

    /**
     * Obtiens les details d'un item
     * @param itemID Identifiant de l'item
     * @return Les details de l'item
     */
    LostItem getItemDetail(long itemID);

    //TODO : DELETE d'un item


    //==  MyItemsActivity ==========================================

    //==  NotificationActivity =====================================


}
