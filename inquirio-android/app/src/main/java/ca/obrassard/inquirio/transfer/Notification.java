package ca.obrassard.inquirio.transfer;

import android.media.Image;

import java.util.Date;

import ca.obrassard.inquirio.model.User;

public class Notification {
    public long id;
    public String senderName;
    public double senderRating;
    public String itemName;
    public Image photo;
    public String message;
    public Date date;
}
