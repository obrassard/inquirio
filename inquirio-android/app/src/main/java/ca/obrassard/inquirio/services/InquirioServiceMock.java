package ca.obrassard.inquirio.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.obrassard.inquirio.model.LostItem;
import ca.obrassard.inquirio.model.User;
import ca.obrassard.inquirio.transfer.FinderContactDetail;
import ca.obrassard.inquirio.transfer.FoundItemSummary;
import ca.obrassard.inquirio.transfer.FoundRequest;
import ca.obrassard.inquirio.transfer.Location;
import ca.obrassard.inquirio.transfer.LocationRequest;
import ca.obrassard.inquirio.transfer.LoginResponse;
import ca.obrassard.inquirio.transfer.LogoutResponse;
import ca.obrassard.inquirio.transfer.LostItemSummary;
import ca.obrassard.inquirio.transfer.Notification;
import ca.obrassard.inquirio.transfer.NotificationSummary;
import ca.obrassard.inquirio.transfer.RequestResult;
import ca.obrassard.inquirio.transfer.SignupRequest;
import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

public class InquirioServiceMock implements InquirioService {

    BehaviorDelegate<InquirioService> delegate;

    public InquirioServiceMock(BehaviorDelegate<InquirioService> delegate) {
        this.delegate = delegate;
    }

    /**
     * Permet de savoir si une adresse courriel
     * est associé à un compte d'utlisateur
     *
     * @param email adresse à vérifier
     * @return True si un compte correspond à l'adresse
     */
    @Override
    public Call<RequestResult> isSubscribed(String email) {
        return delegate.returningResponse(new RequestResult(true)).isSubscribed(email);
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
    public Call<LoginResponse> login(String email, String password) {
        LoginResponse lr = new LoginResponse();
        lr.userFullName = "Olivier Brassard";
        lr.result = true;
        lr.userID = 1;
        lr.userPhoneNumber= "51457825404";

        return delegate.returningResponse(lr).login(email,password);
    }


    /**
     * Inscrit un nouvel utilisateur au service
     * @param userInfos Données d'utlisateurs pour l'inscription
     * @return LoginResponse
     */
    @Override
    public Call<LoginResponse> signup(SignupRequest userInfos) {
        LoginResponse lr = new LoginResponse();
        lr.userFullName = "Olivier Brassard";
        lr.result = false;
        lr.userID = 1;
        lr.userPhoneNumber= "51457825404";

        return delegate.returningResponse(lr).signup(userInfos);
    }

    /**
     * Effectue une déconnexion de l'utlisateur spécifié
     *
     * @param userID Id de l'utilisateur à déconnecter
     * @return LogoutResponse
     */
    @Override
    public Call<LogoutResponse> logout(long userID) {
        LogoutResponse lr = new LogoutResponse();
        lr.message="Ok";
        lr.success = true;
        return delegate.returningResponse(lr).logout(userID);
    }

    /**
     * Obtiens une liste des items perdus à proximité de l'emplacement
     * actuel de l'utlisateur
     *
     * @param currentLocation Emplacement de l'appareil
     * @return Une liste sommaire des items perdus à proximité
     */
    @Override
    public Call<List<LostItemSummary>> getNearLostItems(LocationRequest currentLocation) {
        ArrayList<LostItemSummary> Lostitems = new ArrayList<>();
        LostItemSummary lis1 = new LostItemSummary();
        lis1.found = false;
        lis1.itemID = 1;
        lis1.itemName = "iPhone XS [Max]";
        lis1.locationName = "Terminus Longueuil";

        LostItemSummary lis2 = new LostItemSummary();
        lis2.found = false;
        lis2.itemID = 2;
        lis2.itemName = "Caniche Royal de grande valeur";
        lis2.locationName = "Rue Chambly";

        LostItemSummary lis3 = new LostItemSummary();
        lis3.found = false;
        lis3.itemID = 3;
        lis3.itemName = "Disque dur externe SanDisk";
        lis3.locationName = "Cégep Édouard-Montpetit";

        Lostitems.add(lis1);
        Lostitems.add(lis2);
        Lostitems.add(lis3);

        return delegate.returningResponse(Lostitems).getNearLostItems(currentLocation);
    }

    /**
     * Obtiens les details d'un utilisateur
     *
     * @param userID identifiant de l'utilsateur
     * @return un objet User
     */
    @Override
    public Call<User> getUserDetail(long userID) {
        User u = new User();
        u.Email = "tester.roger@obrassard.ca";
        u.Name = "Roger Tester";
        u.Id = 1;
        u.ItemsFoundCount = 5;
        u.Rating = 3;
        u.Telephone = "5145782504";

        return delegate.returningResponse(u).getUserDetail(userID);
    }

    /**
     * Ajoute un nouvel item perdu
     *
     * @param item Detail de l'item à ajouter
     * @return L'ID de l'item ajouté ou -1
     */
    @Override
    public Call<Long> addNewItem(LostItem item) {
        return delegate.returningResponse(2L).addNewItem(item);
    }


    /**
     * Obtiens les details d'un item
     *
     * @param itemID Identifiant de l'item
     * @return Les details de l'item
     */
    @Override
    public Call<LostItem> getItemDetail(long itemID) {
        LostItem li = new LostItem();
        li.date = new Date();
        li.description = "Il a un étuis rouge et coute très très cher...";
        li.id = 1;
        li.itemHasBeenFound = false;
        li.ownerId = 2;
        li.reward = 200;
        li.title = "iPhone XS [Max]";

        return delegate.returningResponse(li).getItemDetail(itemID);
    }

    /**
     * Obtiens la location (lat + lng) d'un item
     * @param itemID Identifiant de l'item
     * @return L'emplacement de l'item
     */
    @Override
    public Call<Location> getItemLocation(long itemID){
        Location location = new Location();
        location.Lattitude = 45.536286;
        location.Longittude = -73.493004;
        location.Name = "Cégep Édouard-Montpetit";
        return  delegate.returningResponse(location).getItemLocation(itemID);
    }

    /**
     * Supprime un item
     *
     * @param itemID identifiant de l'id
     * @return True si la suppression s'est bien déroulée
     */
    @Override
    public Call<RequestResult> deleteItem(long itemID) {
        return delegate.returningResponse(new RequestResult(true)).deleteItem(itemID);
    }

    /**
     * Obtien une liste sommarisée des items
     * actuellement perdus d'un utilisateur
     *
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */
    @Override
    public Call<List<LostItemSummary>> getLostItemsByOwner(long userID) {
        ArrayList<LostItemSummary> Lostitems = new ArrayList<>();
        LostItemSummary lis1 = new LostItemSummary();
        lis1.found = false;
        lis1.itemID = 1;
        lis1.itemName = "iPhone XS [Max]";
        lis1.locationName = "Terminus Longueuil";

        LostItemSummary lis2 = new LostItemSummary();
        lis2.found = false;
        lis2.itemID = 2;
        lis2.itemName = "Caniche Royal de grande valeur";
        lis2.locationName = "Rue Chambly";


        Lostitems.add(lis1);
        Lostitems.add(lis2);

        return delegate.returningResponse(Lostitems).getLostItemsByOwner(userID);
    }

    /**
     * Obtien une liste sommarisée des items
     * retrouvés d'un utilisateur
     *
     * @param userID Identifiant de l'utilisateur
     * @return Une liste de LostItemSummary
     */
    @Override
    public Call<List<FoundItemSummary>> getFoundItemsByOwner(long userID) {
        ArrayList<FoundItemSummary> FoundItem = new ArrayList<>();

        FoundItemSummary lis3 = new FoundItemSummary();
        lis3.found = true;
        lis3.itemID = 3;
        lis3.itemName = "Disque dur externe SanDisk";
        lis3.finderName = "Roger Tester";

        FoundItem.add(lis3);

        return delegate.returningResponse(FoundItem).getFoundItemsByOwner(userID);
    }

    /**
     * Obtiens une liste sommarisée de notifications
     * d'item potentiellement trouvé
     * @param userID Utilisateur a qui les notifs sont adressées
     * @return Une liste de NotificationSummary
     */
    @Override
    public Call<List<NotificationSummary>> getPotentiallyFoundItems(long userID) {
        ArrayList<NotificationSummary> notificationSummaries = new ArrayList<>();
        NotificationSummary n1 = new NotificationSummary();
        NotificationSummary n2 = new NotificationSummary();

        n1.itemName = "Porte feuille";
        n1.senderName = "Christopher St-Pierre";
        n1.notificationID = 1;

        n2.itemName = "Écouteurs";
        n2.notificationID = 2;
        n2.senderName = "Rogert Tester";


        notificationSummaries.add(n1);
        notificationSummaries.add(n2);

        return delegate.returningResponse(notificationSummaries).getPotentiallyFoundItems(userID);
    }

    /**
     * Obtiens les details d'une notification
     * d'un objet potentiellement trouvé
     *
     * @param notificationID identifiant de la notif de candidat
     * @return Details de la Notification
     */
    @Override
    public Call<Notification> getNotificationDetail(long notificationID) {
        Notification notif = new Notification();
        notif.date = new Date();
        notif.id = 1;
        notif.itemName = "Mon chihuahua et son colier de diamants";
        notif.message = "Je l'ai trouvé près de la garre !!! Mais je garde le colier";
        notif.senderName = "Jean Pierre";
        notif.senderRating = 2.5;
        return delegate.returningResponse(notif).getNotificationDetail(notificationID);
    }

    /**
     * Défini une notification d'objet potentillement trouvé
     * comme erronée (l'item proposé n'est pas l'item recherché)
     *
     * @param notificationID identifiant de la notif de candidat
     * @return True si la requête s'est bien déroulée
     */
    @Override
    public Call<Boolean> denyCandidateNotification(long notificationID) {
        return delegate.returningResponse(true).denyCandidateNotification(notificationID);
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
    public Call<FinderContactDetail> acceptCandidateNotification(long notificationID) {
        FinderContactDetail fcd = new FinderContactDetail("5146514567");
        return delegate.returningResponse(fcd).acceptCandidateNotification(notificationID);
    }

    /**
     * Obtiens les details de contact de l'utlisateur
     * ayant retrouvé un item
     *
     * @param notificationID identifiant de la notif candidate
     * @return FinderContactDetail
     */
    @Override
    public Call<FinderContactDetail> getFinderContactDetail(long notificationID) {
        FinderContactDetail fcd = new FinderContactDetail("5146514567");
        return delegate.returningResponse(fcd).acceptCandidateNotification(notificationID);
    }

    /**
     * Assigne une note de fiabilité(de 0 à 5) à un utilisateur
     *
     * @param userId id de l'utilisateur à noter
     * @param rating note de 0 à 5 reporésentant la fiabilité
     * @return True si la requête s'est bien déroulée;
     */
    @Override
    public Call<RequestResult> rateUser(long userId, int rating) {
        return delegate.returningResponse(new RequestResult(true)).rateUser(userId, rating);
    }

    /**
     * Obtiens le nom d'un item
     * @param itemID id
     * @return nom de l'item
     */
    @Override
    public Call<String> getItemName(long itemID){
        return delegate.returningResponse("Iphone XS").getItemName(itemID);
    }

    /**
     * Permet d'envoyer une requete pour signifier
     * qu'un objet à potentiellement été trouvé
     * @param request
     * @return True si tout s'est déroulé correctement
     */
    @Override
    public Call<RequestResult> sendFoundRequest(FoundRequest request){
        return delegate.returningResponse(new RequestResult(true)).sendFoundRequest(request);
    }

}
