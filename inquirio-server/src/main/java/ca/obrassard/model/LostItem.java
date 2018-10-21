package ca.obrassard.model;

import ca.obrassard.jooqentities.tables.records.LostitemsRecord;

import java.util.Date;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : LostItem.java
 * Date: 20-10-18
 */
public class LostItem {
    public long id;
    public String title;
    public String description;
    public double reward;
    public Date date;
    public long ownerId;
    public boolean itemHasBeenFound;
    public double longitude;
    public double latitude;
    public String locationName;
    public Integer finderID;

    public LostItem(LostitemsRecord record) {
        this.id = record.getId();
        this.title = record.getTitle();
        this.description = record.getDescription();
        this.reward = record.getReward();
        this.date = record.getDate();
        this.ownerId = record.getOwnerid();
        this.itemHasBeenFound = (record.getItemhasbeenfound()==1);
        this.longitude = record.getLongitude();
        this.latitude = record.getLattitude();
        this.locationName = record.getLocationname();
        this.finderID = record.getFinderid();
    }

    public LostItem() { }
}