package ca.obrassard.inquirio.model;

import java.util.Date;

public class Notification {
    private long id;
    private User sender;
    private LostItem item;
    private EnuNotificationType type;
    private Date date;

    public Notification(long id, User sender, LostItem item, EnuNotificationType type, Date date) {
        this.id = id;
        this.sender = sender;
        this.item = item;
        this.type = type;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public LostItem getItem() {
        return item;
    }

    public EnuNotificationType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }
}
