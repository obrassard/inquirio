package ca.obrassard;

import com.google.maps.model.LatLng;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Calculate distance between coordinates.
 */
public class DistanceCalculator {
    static double PI_RAD = Math.PI / 180.0;

    /**
     * Use Great Circle distance formula to calculate distance between 2 coordinates in meters.
     */
    public static double getDistanceInMeters(double lat1, double long1, double lat2, double long2) {
        return getDistanceInKilometers(lat1, long1, lat2,
               long2) * 1000;
    }

    /**
     * Use Great Circle distance formula to calculate distance between 2 coordinates in kilometers.
     * https://software.intel.com/en-us/blogs/2012/11/25/calculating-geographic-distances-in-location-aware-apps
     */
    public static double getDistanceInKilometers(double lat1, double long1, double lat2, double long2) {
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;

        return 6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1));
    }
}