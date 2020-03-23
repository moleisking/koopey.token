package com.koopey.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;

/**
 * Created by Scott on 12/06/2017.
 */
public class Location implements Serializable, Comparator<Location>, Comparable<Location> {

    public static final String LOCATION_FILE_NAME = "location.dat";
    private static final String LOG_HEADER = "LOC";
    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String type = ""; //current,abode
    public Double latitude = 0.0d;
    public Double longitude = 0.0d;
    public String address = "";
    public String position = "";
    //public JSONObject position = new JSONObject();
    public long startTimeStamp = 0;
    public long endTimeStamp = 0;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0;
    public long updateTimeStamp = 0;
    public long deleteTimeStamp = 0;

    public Location() {
    }

    public static String convertLatLngToPosition(Double longitude, Double latitude) {
        if (longitude != 0.0d && latitude != 0.0d) {
            return "{ 'type': 'Point', 'coordinates': [" + String.valueOf(longitude) + "," + String.valueOf(latitude) + "]}"; //longitude, latitude
        } else {
            return "{}";
        }
    }

    public boolean isEmpty() {
        if ((this.address != null && this.address.length() > 5) || (this.latitude != 0.0d) || (this.longitude != 0.0d)) {
            return false;
        } else {
            return true;
        }
    }

    /*public static String convertDistanceToMiles(Double distance) {
        //5280ft in a mile
        if (distance <= 5280) {
            return distance + " ft";
        } else {
            return String.valueOf(Math.round(distance / 5280)) + " mi";
        }
    }

    public static String convertDistanceToKilometers(Double distance) {
        if (distance <= 1000) {
            return String.valueOf(Math.round(distance)) + " m";
        } else {
            return String.valueOf(Math.round(distance / 1000)) + " km";
        }
    }*/

    public LatLng getLatLng() {
        ;
        return new LatLng(this.latitude, this.longitude);
    }

    @Override
    public int compare(Location o1, Location o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Location o) {
        return compare(this, o);
    }

    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("location")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("location"));//{user:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("location")) {
                    this.parseJSON(new JSONObject(json));//{id:1}
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(JSONObject jsonObject) {
        try {
            //Strings
            if (jsonObject.has("id")) {
                this.id = jsonObject.getString("id");
            }
            if (jsonObject.has("hash")) {
                this.hash = jsonObject.getString("hash");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            if (jsonObject.has("address")) {
                this.address = jsonObject.getString("address");
            }
            if (jsonObject.has("position")) {
                this.position = jsonObject.getString("position");
                //this.position = jsonObject.getJSONObject("position");
            }
            if (jsonObject.has("latitude")) {
                this.latitude = jsonObject.getDouble("latitude");
            }
            if (jsonObject.has("longitude")) {
                this.longitude = jsonObject.getDouble("longitude");
            }
            if (jsonObject.has("startTimeStamp")) {
                this.startTimeStamp = Long.parseLong(jsonObject.getString("startTimeStamp"));
            }
            if (jsonObject.has("endTimeStamp")) {
                this.endTimeStamp = Long.parseLong(jsonObject.getString("endTimeStamp"));
            }
            if (jsonObject.has("createTimeStamp")) {
                this.createTimeStamp = Long.parseLong(jsonObject.getString("createTimeStamp"));
            }
            if (jsonObject.has("readTimeStamp")) {
                this.readTimeStamp = Long.parseLong(jsonObject.getString("readTimeStamp"));
            }
            if (jsonObject.has("updateTimeStamp")) {
                this.updateTimeStamp = Long.parseLong(jsonObject.getString("updateTimeStamp"));
            }
            if (jsonObject.has("deleteTimeStamp")) {
                this.deleteTimeStamp = Long.parseLong(jsonObject.getString("deleteTimeStamp"));
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        //Longs
        try {
            if (this.startTimeStamp != 0) {
                jsonObject.put("startTimeStamp", this.startTimeStamp);
            }
        } catch (Exception e) {
        }
        try {
            if (this.endTimeStamp != 0) {
                jsonObject.put("endTimeStamp", this.endTimeStamp);
            }
        } catch (Exception e) {
        }
        try {
            if (this.createTimeStamp != 0) {
                jsonObject.put("createTimeStamp", this.createTimeStamp);
            }
        } catch (Exception e) {
        }
        try {
            if (this.readTimeStamp != 0) {
                jsonObject.put("readTimeStamp", this.readTimeStamp);
            }
        } catch (Exception e) {
        }
        try {
            if (this.updateTimeStamp != 0) {
                jsonObject.put("updateTimeStamp", this.updateTimeStamp);
            }
        } catch (Exception e) {
        }
        try {
            if (this.deleteTimeStamp != 0) {
                jsonObject.put("deleteTimeStamp", this.deleteTimeStamp);
            }
        } catch (Exception e) {
        }
        //Doubles
        try {
            if (this.latitude != 0.0d) {
                jsonObject.put("latitude", this.latitude);
            }
        } catch (Exception e) {
        }
        try {
            if (this.longitude != 0.0d) {
                jsonObject.put("longitude", this.longitude);
            }
        } catch (Exception e) {
        }
        //Strings
        try {
            if (!this.id.equals("")) {
                jsonObject.put("id", this.id);
            }
        } catch (Exception e) {
        }
        try {
            if (!this.hash.equals("")) {
                jsonObject.put("hash", this.hash);
            }
        } catch (Exception e) {
        }
        try {
            if (!this.type.equals("")) {
                jsonObject.put("type", this.type);
            }
        } catch (Exception e) {
        }
        try {
            if (!this.address.equals("")) {
                jsonObject.put("address", this.address);
            }
        } catch (Exception e) {
        }
        //Objects
        try {
            jsonObject.put("position", this.convertLatLngToPosition(this.longitude, this.latitude));
        } catch (Exception e) {
        }
        return jsonObject;
    }

    public void print() {
        Log.d("Location", "Object");
        try {
            Log.d("id", this.id);
            Log.d("hash", this.hash);
            Log.d("type", this.type);
            Log.d("address", this.address);
            Log.d("latitude", String.valueOf(this.latitude));
            Log.d("longitude", String.valueOf(this.longitude));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }

    }
}
