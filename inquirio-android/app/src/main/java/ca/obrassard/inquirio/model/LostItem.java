package ca.obrassard.inquirio.model;

import ca.obrassard.inquirio.transfer.Location;

import java.util.Date;

/**
 * Created by Olivier Brassard.
 * Project : inquirioServer
 * Filename : LostItemCreationRequest.java
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


    public LostItem() { }

    public Location getLocation(){
        Location location = new Location();
        location.Lattitude = latitude;
        location.Longittude = longitude;
        location.Name = locationName;
        return location;
    }
}