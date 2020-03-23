package com.koopey.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Scott on 22/09/2017.
 */

public class Locations implements Serializable, Comparator<Locations>, Comparable<Locations> {
    private static final String LOG_HEADER = "LOCS";
    public static final String LOCATIONS_FILE_NAME = "locations.dat";
    private List<Location> locations;

    public Locations() {
        locations = new ArrayList();
    }

    public Location get(int i) {
        return this.locations.get(i);
    }

    public Location get(Location location) {
        Location result = null;
        for (int i = 0; i < this.locations.size(); i++) {
            if (this.locations.get(i).id.equals(location.id) ||
                    this.locations.get(i).type.equals(location.type)) {
                result = this.locations.get(i);
                break;
            }
        }
        return result;
    }

    public Location get(String type) {
        for (int x = 0; x < this.locations.size(); x++) {
            if (this.locations.get(x).type.equals(type)) {
                return this.locations.get(x);
            }
        }
        return null;
    }

    public Location getAbode() {
        return this.get("abode");
    }



    public Location getCurrent() {
        return this.get("current");
    }

    public LatLng getCurrentAsLatLng() {
        Location current = this.get("current");
        return new LatLng(current.latitude, current.longitude);
    }

    public void set(Location location) {
        for (int x = 0; x < this.locations.size(); x++) {
            if (this.locations.get(x).type.equals(location.type)) {
                this.locations.set(x, location);
            }
        }
    }

    public int size() {
        return this.locations.size();
    }

    public void add(double latitude, double longitude, String type) {
        this.add(latitude, longitude, "", type);
    }

    public void add(double latitude, double longitude, String address, String type) {
        Location location = new Location();
        location.type = type;
        location.latitude = latitude;
        location.longitude = longitude;
        location.position = Location.convertLatLngToPosition(latitude, longitude);
        this.add(location);
    }

    public void add(Location location) {
        if (!this.contains(location.type)) {
            this.locations.add(location);
        } else {
            this.set(location);
        }
    }

    public List<Location> getList() {
        return this.locations;
    }

    public ArrayList<Review> getArrayList() {
        return (ArrayList) this.locations;//new ArrayList<Tag>( this.tags.toArray());
    }

    public void setList(List<Location> locations) {
        this.locations = locations;
    }


    public boolean isEmpty() {
        return this.size() == 0 ? true : false;
    }

    @Override
    public String toString() {
        /*locations: [{
            id: 'a90780ab-8e5a-4b1d-ae54-c4a592ae5dbf',
            createTimeStamp: 12345
         }]*/
        String str = "[";
        for (int i = 0; i < locations.size(); i++) {
            locations.get(i).toString();
            if (i != locations.size() - 1) {
                str += ",";
            }
        }
        return str + "]";
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < this.locations.size(); i++) {
                jsonArray.put(this.locations.get(i).toJSONObject());
            }
        } catch (Exception ex) {
        }
        return jsonArray;
    }

    public void sort() {
        Collections.sort(locations);
    }

    public int compareTo(Locations o) {
        return this.compare(this, o);
    }

    @Override
    public int compare(Locations o1, Locations o2) {
        //-1 not the same, 0 is same, 1 > is same but larger
        int result = -1;
        if (o1.size() < o2.size()) {
            result = -1;
        } else if (o1.size() > o2.size()) {
            result = 1;
        } else {
            //Sort both lists before compare
            o1.sort();
            o2.sort();
            //Check each tag in tags
            for (int i = 0; i < o1.size(); i++) {
                if (!o1.contains(o2.get(i))) {
                    result = -1;
                    break;
                } else if (i == o2.size() - 1) {
                    result = 0;
                    break;
                }
            }
        }
        return result;
    }

    public boolean contains(Location item) {
        if (this.get(item) != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean contains(String type) {
        if (this.get(type) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void parseJSON(JSONArray jsonArray) {
        try {
            // JSONArray jsonArray = new JSONObject(json).getJSONArray("reviews");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Location location = new Location();
                location.parseJSON(jsonObject.toString());
                this.add(location);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(String json) {
        JSONArray jsonArray;
        try {
            //Check JSON format, which could be [ or {
            if (json.length() >= 1) {
                if (json.substring(0, 1).equals("[")) {
                    //[] array format
                    jsonArray = new JSONArray(json);
                    this.parseJSON(jsonArray);
                } else if (json.substring(0, 1).equals("{")) {
                    //{locations:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("locations");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print() {
        try {
            Log.d("Locations", "Object");
            Log.d("Locations Size", String.valueOf(this.size()));
            Log.d("Locations ", this.toString());
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
