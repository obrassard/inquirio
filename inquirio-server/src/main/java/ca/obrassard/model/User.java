package ca.obrassard.model;

import ca.obrassard.jooqentities.tables.records.UsersRecord;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : User.java
 * Date: 20-10-18
 */
public class User {
    public int Id;
    public String Name;
    public String Email;
    public String Telephone;
    public int ItemsFoundCount ;
    public double Rating;

    public User() { }

    public User(UsersRecord ur) {
        Id = ur.getId();
        Name = ur.getName();
        Email = ur.getEmail();
        Telephone = ur.getTelephone();
        ItemsFoundCount = ur.getItemsfoundcount();
        Rating = ur.getRating();
    }

    public User(int id, String name, String email, String telephone, int itemsFoundCount, double rating) {
        Id = id;
        Name = name;
        Email = email;
        Telephone = telephone;
        ItemsFoundCount = itemsFoundCount;
        Rating = rating;
    }


}
