package ca.obrassard.inquirio.model;

import com.google.android.gms.location.places.Place;

import java.util.Date;

public class LostItem {
    private long id;
    private String title;
    private String description;
    private Place location;
    private double reward;
    private Date date;
    private User owner;

    public LostItem(long id, String title, String description, Place location, double reward, Date date, User owner) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.reward = reward;
        this.date = date;
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public Date getDate() {
        return date;
    }

    public User getOwner() {
        return owner;
    }

    public Place getLocation() {
        return location;
    }

    public void setLocation(Place location) {
        this.location = location;
    }
}

