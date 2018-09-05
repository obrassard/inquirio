package ca.obrassard.inquirio;

import ca.obrassard.inquirio.transfer.LoginResponse;
import ca.obrassard.inquirio.transfer.SignupRequest;

public interface RESTMethods {

    //==  Login/Signup  ============================================

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
}
