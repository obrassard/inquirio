package ca.obrassard.inquirio.model;

import android.media.Image;

import java.util.Date;

public class Notification {
    private long id;
    private User sender;
    private String itemName;
    private Image photo;
    private String message;
    private Date date;

    public Notification(long id, User sender, String item, Date date) {
        this.id = id;
        this.sender = sender;
        this.itemName = item;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public String getItem() {
        return itemName;
    }

    public Date getDate() {
        return date;
    }
}
