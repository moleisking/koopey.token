package com.koopey.common;

import com.google.android.gms.maps.model.LatLng;

public class MapHelper {

    private final static String LOG_HEADER = "MAP:HELPER";

    public static int calculateDistanceMeters(LatLng currentLocation, LatLng searchLocation) {
        //default is to set distance in meters
        //This function takes in latitude and longitude of two location and returns the distance between them in m
        double EARTH_RADIUS = 6371; // Radius in Kilometers
        double dLat = Math.toRadians(currentLocation.latitude - searchLocation.latitude);
        double dLon = Math.toRadians(currentLocation.longitude - searchLocation.longitude);
        double lat1 = Math.toRadians(searchLocation.latitude);
        double lat2 = Math.toRadians(currentLocation.latitude);

        double a = Math.sin(dLat / 2)
                * Math.sin(dLat / 2)
                + Math.sin(dLon / 2)
                * Math.sin(dLon / 2)
                * Math.cos(lat1)
                * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        //returns meters
        return ((Double) (EARTH_RADIUS * c * 1000)).intValue();
    }

    public static int calculateDistanceFeet(LatLng currentLocation, LatLng searchLocation) {
        //This function takes in latitude and longitude of two location and returns the distance between them in feet
        double EARTH_RADIUS = 3960;// Radius in miles
        double dLat = Math.toRadians(currentLocation.latitude - searchLocation.latitude);
        double dLon = Math.toRadians(currentLocation.longitude - searchLocation.longitude);
        double lat1 = Math.toRadians(searchLocation.latitude);
        double lat2 = Math.toRadians(currentLocation.latitude);
        double a = Math.sin(dLat / 2)
                * Math.sin(dLat / 2)
                + Math.sin(dLon / 2)
                * Math.sin(dLon / 2)
                * Math.cos(lat1)
                * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        //returns feet
        return ((Double) (EARTH_RADIUS * c * 5280)).intValue();
    }

    public static int convertMetersToFeet(int meters) {
        //1m = 3.28084ft
        return Math.round((long) ((double) meters * 3.28084));
    }
}
