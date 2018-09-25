package ca.obrassard.inquirio.transfer;

public class LoginResponse {

    public boolean result;
    public long userID;
    public String userFullName;
    public String userPhoneNumber;
    public boolean isFirstLogin;

    public LoginResponse() {
        this.result = true;
        this.userID = 1;
        this.userFullName = "TEST";
        this.userPhoneNumber = "TEST";
        this.isFirstLogin = false;
    }
}
