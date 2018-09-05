package ca.obrassard.inquirio.transfer;

public class LoginResponse {

    public boolean result;
    public long userID;
    public String userFullName;
    public String userPhoneNumber;

    public LoginResponse(boolean result, long userID, String userFullName, String userPhoneNumber) {
        this.result = result;
        this.userID = userID;
        this.userFullName = userFullName;
        this.userPhoneNumber = userPhoneNumber;
    }
}
