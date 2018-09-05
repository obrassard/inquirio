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
    private long ownerId;
    private boolean itemHasBeenFound;

    public LostItem(long id, String title, String description, Place location, double reward,
                    Date date, long ownerId, boolean itemHasBeenFound) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.reward = reward;
        this.date = date;
        this.ownerId = ownerId;
        this.itemHasBeenFound = itemHasBeenFound;
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

    public long getOwner() {
        return ownerId;
    }

    public Place getLocation() {
        return location;
    }

    public void setLocation(Place location) {
        this.location = location;
    }
}

