package ca.obrassard.model;

import ca.obrassard.jooqentities.tables.records.NotificationRecord;

import java.util.Date;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : Notification.java
 * Date: 20-10-18
 */
public class Notification {
    public int id;
    public int senderId;
    public int itemId;
    public byte[] photo;
    public String message;
    public Date date;

    public Notification() { }

    public Notification(NotificationRecord record) {
        this.id = record.getId();
        this.senderId = record.getSenderid();
        this.itemId = record.getItemid();
        this.photo = record.getPhoto();
        this.message = record.getMessage();
        this.date = record.getDate();
    }
}