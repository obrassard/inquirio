package ca.obrassard;

import ca.obrassard.exception.APIErrorCodes;
import ca.obrassard.exception.APIRequestException;
import ca.obrassard.inquirioCommons.LocationRequest;
import org.apache.commons.validator.routines.EmailValidator;
import org.jooq.DSLContext;

import java.util.regex.Pattern;

import static ca.obrassard.jooqentities.tables.Lostitems.LOSTITEMS;
import static ca.obrassard.jooqentities.tables.Notification.NOTIFICATION;
import static ca.obrassard.jooqentities.tables.Users.USERS;

public class AuthValidator {

    public static int validateToken(int token, DSLContext context) throws APIRequestException{
        if (!context.fetchExists(context.selectOne().from(USERS).where(USERS.ID.eq(token)))){
            throw new APIRequestException(APIErrorCodes.AccesDenied);
        }
        return token; //Retourera eventuellement l'ID de l'utilisateur à partir du token (cookie)
        //Pour l'instant le "token" c'est déjà l'ID de l'utilisateur
    }


}
