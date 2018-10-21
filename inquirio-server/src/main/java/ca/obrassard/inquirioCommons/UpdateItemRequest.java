package ca.obrassard.inquirioCommons;

import com.google.android.gms.location.places.Place;

public class UpdateItemRequest {
    public long itemID;
    public String itemTitle;
    public String description;
    public double reward;
    public Place location;
}
