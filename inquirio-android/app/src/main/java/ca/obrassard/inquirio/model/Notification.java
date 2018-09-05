package ca.obrassard.inquirio.model;

import android.media.Image;

import java.util.Date;

public class Notification {
    private long id;
    private User sender;
    private String itemName;
    private Image photo;
    private String message;
    private EnuNotificationType type;
    private Date date;

    public Notification(long id, User sender, String item, EnuNotificationType type, Date date) {
        this.id = id;
        this.sender = sender;
        this.itemName = item;
        this.type = type;
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

    public EnuNotificationType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }
}
