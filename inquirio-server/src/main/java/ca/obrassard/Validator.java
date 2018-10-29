package ca.obrassard;

import ca.obrassard.exception.APIErrorCodes;
import ca.obrassard.exception.APIRequestException;
import ca.obrassard.inquirioCommons.LocationRequest;
import ca.obrassard.jooqentities.tables.Lostitems;
import ca.obrassard.jooqentities.tables.Notification;
import org.apache.commons.validator.routines.EmailValidator;
import org.jooq.DSLContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.obrassard.jooqentities.tables.Lostitems.*;
import static ca.obrassard.jooqentities.tables.Notification.NOTIFICATION;
import static ca.obrassard.jooqentities.tables.Users.USERS;

public class Validator {
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

    public static void isAnExistantUserID(int userId, DSLContext context) throws APIRequestException{
        if (!context.fetchExists(context.selectOne().from(USERS).where(USERS.ID.eq(userId)))){
            throw new APIRequestException(APIErrorCodes.UnknownUserId);
        }
    }

    public static void isAnExistantItemID(int itemID, DSLContext context) throws APIRequestException{
        if (!context.fetchExists(context.selectOne().from(LOSTITEMS).where(LOSTITEMS.ID.eq(itemID)))){
            throw new APIRequestException(APIErrorCodes.UnknownUserId);
        }
    }

    public static void isAnExistantNotificationID(int notifId, DSLContext context) throws APIRequestException{
        if (!context.fetchExists(context.selectOne().from(NOTIFICATION).where(NOTIFICATION.ID.eq(notifId)))){
            throw new APIRequestException(APIErrorCodes.UnknownNotificationID);
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

    public static void isRequired(String attribute, String value) throws APIRequestException {
        if (value == null || value.trim().length()==0){
            throw new APIRequestException(APIErrorCodes.RequiredAttribute, attribute);
        }
    }

    public static void validatePhone(String phoneNum) throws APIRequestException {

        if (!Pattern.compile("[0-9]{10,11}").matcher(phoneNum).matches()){
            throw new APIRequestException(APIErrorCodes.InvalidPhoneNumber);
        }
    }

    public static void isAValidLocation(double latitude, double longitude) throws APIRequestException {

        if (longitude > 180 || longitude < -180 ||
                latitude > 90 || latitude < -90){
            throw new APIRequestException(APIErrorCodes.InvalidLocation);
        }
    }

    public static void isAValidLocation(LocationRequest location) throws APIRequestException {
        isAValidLocation(location.latitude,location.longitude);
    }

    public static void isAPositiveNumber(String attribute, double number) throws  APIRequestException{
        if (number < 0){
            throw new APIRequestException(APIErrorCodes.InvalidValue,attribute);
        }
    }

    public static void respectMaxLength(String attribute, String text, int maxlength)throws  APIRequestException{
        if (text.length() > maxlength){
            throw new APIRequestException(APIErrorCodes.TextLengthViolation,attribute);
        }
    }


}
