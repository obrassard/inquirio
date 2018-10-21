package ca.obrassard.model;

import java.util.Date;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : Notification.java
 * Date: 20-10-18
 */
public class Notification {
    public long id;
    public long senderId;
    public long itemId;
    public byte[] photo;
    public String message;
    public Date date;
}