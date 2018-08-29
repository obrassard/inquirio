package ca.obrassard.inquirio.model;

public class User {
    private long id;
    private String fullname;
    private String email;
    private String telephone;
    private int itemsFound;

    public User(long id, String fullname, String email, String telephone, int itemsFound) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.telephone = telephone;
        this.itemsFound = itemsFound;
    }

    public long getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getItemsFound() {
        return itemsFound;
    }

    public void incrementItemsFound() {
        this.itemsFound+=1;
    }
}
