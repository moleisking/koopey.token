/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.koopey.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Scott on 20/09/2017.
 */

public class Advert implements Serializable {
    private static final String LOG_HEADER = "ADV";
    public static final String ADVERT_FILE_NAME = "advert.dat";

    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String type = "";
    public String timeZone = "Etc/UTC";
    public long startTimeStamp = 0;
    public long endTimeStamp = 0; //read only
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0; //read only
    public long updateTimeStamp = 0; //read only
    public long deleteTimeStamp = 0; //read only

    public Advert() {
    }

    @Override
    public String toString() {
        return this.toJSONObject().toString();
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
            if (!this.hash.equals("")) {
                jsonObject.put("type", this.type);
            }
        } catch (Exception e) {
        }
        return jsonObject;
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("advert")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("advert"));//{advert:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("advert")) {
                    this.parseJSON(new JSONObject(json));//{id:1}
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(JSONObject jsonObject) {
        //Longs
        try {
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
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print() {
        Log.d("Advert", "Object");
        try {
            Log.d("id", this.id);
        } catch (Exception e) {
        }
        try {
            Log.d("hash", this.hash);
        } catch (Exception e) {
        }
        try {
            Log.d("type", this.type);
        } catch (Exception e) {
        }
        try {
            Log.d("startTimeStamp", this.getStartTimeStampAsString());
        } catch (Exception e) {
        }
        try {
            Log.d("endTimeStamp", this.getEndTimeStampAsString());
        } catch (Exception e) {
        }
    }

}
