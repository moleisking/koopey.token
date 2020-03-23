package com.koopey.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by Scott on 09/11/2017.
 */

public class File implements Serializable, Comparator<File>, Comparable<File> {

    private static final String LOG_HEADER = "FILE";
    public static final String FILE_NAME = "file.dat";
    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String name = "";
    public String description = "";
    public String data = "";
    public String path = "";
    public String type = "";
    public String timeZone = "Etc/UTC";
    public double size = 0.0d;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0; //read only
    public long updateTimeStamp = 0; //read only
    public long deleteTimeStamp = 0; //read only

    public File() {
    }

    @Override
    public int compare(File o1, File o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(File o) {
        return compare(this, o);
    }

    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }

    public boolean isEmpty() {
        if (this.path.equals("") || !this.data.equals("") && this.size >= 0) {
            return true;
        } else {
            return false;
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

    //*********  JSON  *********

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            //Longs
            if (this.createTimeStamp != 0) {
                jsonObject.put("createTimeStamp", this.createTimeStamp);
            }
            if (this.readTimeStamp != 0) {
                jsonObject.put("readTimeStamp", this.readTimeStamp);
            }
            if (this.updateTimeStamp != 0) {
                jsonObject.put("updateTimeStamp", this.updateTimeStamp);
            }
            if (this.deleteTimeStamp != 0) {
                jsonObject.put("deleteTimeStamp", this.deleteTimeStamp);
            }
            //Doubles
            if (this.size != 0.0d) {
                jsonObject.put("size", this.size);
            }
            //Strings
            try {
                jsonObject.put("id", this.id);
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
                if (!this.timeZone.equals("")) {
                    jsonObject.put("timeZone", this.timeZone);
                }
            } catch (Exception e) {
            }
            try {
                if (!this.name.equals("")) {
                    jsonObject.put("name", this.name);
                }
            } catch (Exception e) {
            }
            try {
                if (!this.path.equals("")) {
                    jsonObject.put("path", this.path);
                }
            } catch (Exception e) {
            }
            try {
                if (!this.data.equals("")) {
                    jsonObject.put("data", this.data);
                }
            } catch (Exception e) {
            }
            try {
                if (!this.description.equals("")) {
                    jsonObject.put("description", this.description);
                }
            } catch (Exception e) {
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
        return jsonObject;
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("fee")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("fee"));//{file:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("fee")) {
                    this.parseJSON(new JSONObject(json));//{file:1}
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(JSONObject jsonObject) {
        //Longs
        try {
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
            //Double
            if (jsonObject.has("size")) {
                this.size = jsonObject.getDouble("size");
            }
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
            if (jsonObject.has("timeZone")) {
                this.timeZone = jsonObject.getString("timeZone");
            }
            if (jsonObject.has("name")) {
                this.name = jsonObject.getString("name");
            }
            if (jsonObject.has("description")) {
                this.description = jsonObject.getString("description");
            }
            if (jsonObject.has("path")) {
                this.path = jsonObject.getString("path");
            }
            if (jsonObject.has("data")) {
                this.data = jsonObject.getString("data");
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print() {
        Log.d("File", "Object");
        try {
            Log.d("id", id);
            Log.d("hash", hash);
            Log.d("type", type);
            Log.d("name", this.name);
            Log.d("description", this.description);
            Log.d("path", this.path);
            Log.d("size", Double.toString(this.size));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

}
