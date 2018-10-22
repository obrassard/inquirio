package ca.obrassard;

import org.apache.commons.validator.routines.EmailValidator;

public class ValidationUtil {
    public static boolean isValidEmail(String email){
        return EmailValidator.getInstance().isValid(email);
    }
}
