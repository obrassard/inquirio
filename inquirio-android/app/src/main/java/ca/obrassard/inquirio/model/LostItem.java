package ca.obrassard.inquirio.model;

import com.google.android.gms.location.places.Place;

import java.util.Date;

import ca.obrassard.inquirio.transfer.Location;

public class LostItem {
    public long id;
    public String title;
    public String description;
    public double reward;
    public Date date;
    public long ownerId;
    public boolean itemHasBeenFound;
}

