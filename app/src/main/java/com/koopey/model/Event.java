package com.koopey.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Scott on 19/06/2018.
 */

public class Event implements Serializable {
    private static final String LOG_HEADER = "EVENT";
    public static final String EVENT_FILE_NAME = "event.dat";

    public Location location;
    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public Users users = new Users();
    public String name = "";
    public String description = "";
    public String type = "";
    public String timeZone = "Etc/UTC";
    public long startTimeStamp;
    public long endTimeStamp;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0; //read only
    public long updateTimeStamp = 0; //read only
    public long deleteTimeStamp = 0; //read only

    public Event() {
    }

    @Override
    public String toString() {
        //JSONObject adds backslash in front of forward slashes causing corrupt images
        return this.toJSONObject().toString().replaceAll("\\/", "/");
    }

    public boolean isEmpty() {
        if (this.name.equals("")
                && this.type.equals("")
                && this.users.size() > 0
                && this.startTimeStamp == 0
                && this.endTimeStamp == 0
               ) {
            return true;
        } else {
            return false;
        }
    }

    public String getStartTimeStampAsString() {
        Date date = new Date(this.startTimeStamp);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(this.timeZone));
        return format.format(date);
    }

    public Date getStartTimeStampAsDate() {
        return new Date(this.startTimeStamp);
    }

    public String getEndTimeStampAsString() {
        Date date = new Date(this.endTimeStamp);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(this.timeZone));
        return format.format(date);
    }

    public Date getEndTimeStampAsDate() {
        return new Date(this.endTimeStamp);
    }

    public long getRemainingHours() {
        long now = System.currentTimeMillis();
        if (now < this.endTimeStamp) {
            return TimeUnit.MICROSECONDS.toHours(now - this.endTimeStamp);
        } else {
            return 0;
        }
    }

    public long getRemainingDays() {
        long now = System.currentTimeMillis();
        if (now < this.endTimeStamp) {
            return TimeUnit.MICROSECONDS.toDays(this.endTimeStamp - this.startTimeStamp);
        } else {
            return 0;
        }
    }

    public String getCreateTimeStampAsString() {
        Date date = new Date(this.createTimeStamp);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(this.timeZone));
        return format.format(date);
    }

    public Date getCreateTimeStampAsDate() {
        return new Date(this.createTimeStamp);
    }

    public String getReadTimeStampAsString() {
        Date date = new Date(this.readTimeStamp);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(this.timeZone));
        return format.format(date);
    }

    public Date getReadTimeStampAsDate() {
        return new Date(this.readTimeStamp);
    }

    public String getUpdateTimeStampAsString() {
        Date date = new Date(this.updateTimeStamp);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(this.timeZone));
        return format.format(date);
    }

    public Date getUpdateTimeStampAsDate() {
        return new Date(this.updateTimeStamp);
    }

    public String getDeleteTimeStampAsString() {
        Date date = new Date(this.deleteTimeStamp);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(this.timeZone));
        return format.format(date);
    }

    public Date getDeleteTimeStampAsDate() {
        return new Date(this.deleteTimeStamp);
    }

    public void setLocation(LatLng latLng, String address) {
        this.location = new Location();
        this.location.longitude = latLng.longitude;
        this.location.latitude = latLng.latitude;
        this.location.address = address;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            //Longs
            try {
                jsonObject.put("createTimeStamp", this.createTimeStamp);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("readTimeStamp", this.readTimeStamp);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("updateTimeStamp", this.updateTimeStamp);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("deleteTimeStamp", this.deleteTimeStamp);
            } catch (Exception e) {
            }
            if (this.startTimeStamp != 0) {
                jsonObject.put("startTimeStamp", this.startTimeStamp);
            }
            if (this.endTimeStamp != 0) {
                jsonObject.put("endTimeStamp", this.endTimeStamp);
            }
            //Strings
            try {
                jsonObject.put("id", this.id);
            } catch (Exception e) {
            }
            if (!this.hash.equals("")) {
                jsonObject.put("hash", this.hash);
            }
            try {
                jsonObject.put("type", this.type);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("name", this.name);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("description", this.description);
            } catch (Exception e) {
            }
            //Location
            try {
                jsonObject.put("location", this.location.toJSONObject());
            } catch (Exception e) {
            }
            //Arrays
            if (this.users.size() > 0) {
                try {
                    jsonObject.put("users", this.users.toJSONArray());
                } catch (Exception e) {
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
        return jsonObject;
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("event")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("event"));//{transaction:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("event")) {
                    this.parseJSON(new JSONObject(json));//{id:1}
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(JSONObject jsonObject) {
        try {
            //Longs
            if (jsonObject.has("startTimeStamp")) {
                this.startTimeStamp = jsonObject.getLong("startTimeStamp");
            }
            if (jsonObject.has("endTimeStamp")) {
                this.endTimeStamp = jsonObject.getLong("endTimeStamp");
            }
            if (jsonObject.has("createTimeStamp")) {
                this.createTimeStamp = jsonObject.getLong("createTimeStamp");
            }
            if (jsonObject.has("readTimeStamp")) {
                this.readTimeStamp = jsonObject.getLong("readTimeStamp");
            }
            if (jsonObject.has("updateTimeStamp")) {
                this.updateTimeStamp = jsonObject.getLong("updateTimeStamp");
            }
            if (jsonObject.has("deleteTimeStamp")) {
                this.deleteTimeStamp = jsonObject.getLong("deleteTimeStamp");
            }
            //Strings
            if (jsonObject.has("id")) {
                this.id = jsonObject.getString("id");
            }
            if (jsonObject.has("hash")) {
                this.hash = jsonObject.getString("hash");
            }
            if (jsonObject.has("name")) {
                this.name = jsonObject.getString("name");
            }
            if (jsonObject.has("description")) {
                this.description = jsonObject.getString("description");
            }
            if (jsonObject.has("timeZone")) {
                this.timeZone = jsonObject.getString("timeZone");
            }
           //Objects
            if (jsonObject.has("location")) {
                this.location.parseJSON(jsonObject.getString("location"));
            }
            //Arrays
            if (jsonObject.has("users")) {
                this.users.parseJSON(jsonObject.getJSONArray("users"));
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print() {
        Log.d("Event", "Object");
        try {
            Log.d("id", String.valueOf(id));
            Log.d("hash", this.hash);
            Log.d("name", this.name);
            Log.d("type", this.type);
            Log.d("description", this.description);
            Log.d("startTimeStamp", this.getStartTimeStampAsString());
            Log.d("endTimeStamp", this.getEndTimeStampAsString());
            this.users.print();
            this.location.print();
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
