package ca.obrassard;

import ca.obrassard.exception.APIErrorCodes;
import ca.obrassard.exception.APIRequestException;
import ca.obrassard.inquirioCommons.LocationRequest;
import org.apache.commons.validator.routines.EmailValidator;
import org.jooq.DSLContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.obrassard.jooqentities.tables.Users.USERS;

public class ValidationUtil {
    public static void validateEmail(String email) throws APIRequestException {
        if (!EmailValidator.getInstance().isValid(email)){
            throw new APIRequestException(APIErrorCodes.InvalidEmail);
        }
    }

    public static void emailIsUnique(String email, DSLContext context) throws APIRequestException{
        if (context.fetchExists(context.selectOne().from(USERS).where(USERS.EMAIL.eq(email)))){
            throw new APIRequestException(APIErrorCodes.EmailAlreadyExists);
        }
    }

    public static void userIdShouldExist(int userId, DSLContext context) throws APIRequestException{
        if (!context.fetchExists(context.selectOne().from(USERS).where(USERS.ID.eq(userId)))){
            throw new APIRequestException(APIErrorCodes.UnknownUserId);
        }
    }

    public static void validateNewPassword(String password, String confirmation) throws APIRequestException {
        if (!password.equals(confirmation)){
            throw new APIRequestException(APIErrorCodes.PasswordDoesntMatch);
        }
        if (password.length()==0){
            throw new APIRequestException(APIErrorCodes.PasswordIsRequired);
        }
    }

    public static void nameisRequired(String string) throws APIRequestException {
        if (string.trim().length()==0){
            throw new APIRequestException(APIErrorCodes.NameIsRequired);
        }
    }

    public static void validatePhone(String phoneNum) throws APIRequestException {

        if (!Pattern.compile("[0-9]{10,11}").matcher(phoneNum).matches()){
            throw new APIRequestException(APIErrorCodes.InvalidPhoneNumber);
        }
    }

    public static void isAValidLocation(LocationRequest location) throws APIRequestException {

        if (location.longitude > 180 || location.longitude < -180 ||
                location.latitude > 90 || location.latitude < -90){
            throw new APIRequestException(APIErrorCodes.InvalidLocation);
        }
    }
}
