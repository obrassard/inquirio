package ca.obrassard;

import ca.obrassard.exception.APIErrorCodes;
import ca.obrassard.exception.APIRequestException;
import ca.obrassard.inquirioCommons.LocationRequest;
import ca.obrassard.jooqentities.tables.Tokens;
import ca.obrassard.jooqentities.tables.records.TokensRecord;
import org.apache.commons.validator.routines.EmailValidator;
import org.jooq.DSLContext;

import javax.ws.rs.core.Cookie;
import java.util.regex.Pattern;

import static ca.obrassard.jooqentities.tables.Lostitems.LOSTITEMS;
import static ca.obrassard.jooqentities.tables.Notification.NOTIFICATION;
import static ca.obrassard.jooqentities.tables.Tokens.TOKENS;
import static ca.obrassard.jooqentities.tables.Users.USERS;

public class AuthValidator {

    /**
     * Check if a cookie contains a token and validate that the token is valid
     * @param authCookie authentication cookie
     * @param context databse
     * @return The corresponding userID
     * @throws APIRequestException AccesDenied if there is no token or if the token is invalid
     */
    public static int validateToken(Cookie authCookie, DSLContext context) throws APIRequestException{
        if (authCookie == null){
            throw new APIRequestException(APIErrorCodes.AccesDenied);
        }

        TokensRecord r = context.selectFrom(TOKENS).where(TOKENS.TOKEN.eq(authCookie.getValue())).fetchOne();

        if (r == null){
            throw new APIRequestException(APIErrorCodes.AccesDenied);
        }
        return r.getUserid();
    }


}
