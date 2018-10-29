package ca.obrassard.inquirioCommons;

import ca.obrassard.jooqentities.tables.records.LostitemsRecord;

import java.util.Date;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : LostItemCreationRequest.java
 * Date: 20-10-18
 */
public class LostItemCreationRequest {
    public String title;
    public String description;
    public double reward;
    public int ownerId;
    public double longitude;
    public double latitude;
    public String locationName;

}