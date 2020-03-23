package com.koopey.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;

/**
 * Created by Scott on 18/01/2017.
 */

public class Review implements Serializable, Comparator<Review>, Comparable<Review> {

    public static final String REVIEW_FILE_NAME = "review.dat";
    private static final String LOG_HEADER = "REVIEW";

    public String id = UUID.randomUUID().toString();
    public String type = "";
    public String hash = "";
    public String assetId;
    public String userId;
    public String judgeId;
    public String productId;
    public String comment = "";
    public int value = 0;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0;
    public long updateTimeStamp = 0;
    public long deleteTimeStamp = 0;

    public Review() {
    }

    @Override
    public int compare(Review o1, Review o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Review o) {
        return compare(this, o);
    }

    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }

    //*********  JSON  *********

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("review")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("review"));//{review:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("review")) {
                    this.parseJSON(new JSONObject(json));//{id:1}
                }
            }
            //this.parseJSON(new JSONObject(json));
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
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            if (jsonObject.has("assetId")) {
                this.assetId = jsonObject.getString("assetId");
            }
            if (jsonObject.has("userId")) {
                this.userId = jsonObject.getString("userId");
            }
            if (jsonObject.has("judgeId")) {
                this.judgeId = jsonObject.getString("judgeId");
            }
            if (jsonObject.has("commet")) {
                this.comment = jsonObject.getString("comment");
            }
            //Integers
            if (jsonObject.has("value")) {
                this.value = jsonObject.getInt("value");
            }
            //Longs
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
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        if (!id.equals("")) {
            try {
                jsonObject.put("id", this.id);
                jsonObject.put("type", this.type);
            } catch (Exception e) {
            }
        }
        try {
            if (!this.hash.equals("")) {
                jsonObject.put("hash", this.hash);
            }
        } catch (Exception e) {
        }
        if (!this.judgeId.equals("")) {
            try {
                jsonObject.put("judgeId", this.judgeId);
            } catch (Exception e) {
            }
        }
        if (!this.assetId.equals("")) {
            try {
                jsonObject.put("assetId", this.assetId);
            } catch (Exception e) {
            }
        }
        if (!this.userId.equals("")) {
            try {
                jsonObject.put("userId", this.userId);
            } catch (Exception e) {
            }
        }
        if (this.createTimeStamp != 0) {
            try {
                jsonObject.put("createTimeStamp", this.createTimeStamp);
            } catch (Exception e) {
            }
        }
        if (this.readTimeStamp != 0) {
            try {
                jsonObject.put("readTimeStamp", this.readTimeStamp);
            } catch (Exception e) {
            }
        }
        if (this.updateTimeStamp != 0) {
            try {
                jsonObject.put("createTimeStamp", this.updateTimeStamp);
            } catch (Exception e) {
            }
        }
        if (this.deleteTimeStamp != 0) {
            try {
                jsonObject.put("deleteTimeStamp", this.deleteTimeStamp);
            } catch (Exception e) {
            }
        }
        try {
            jsonObject.put("value", this.value);
        } catch (Exception e) {
        }
        return jsonObject;
    }

    //*********  Print  *********

    public void print() {
        try {
            Log.d("Review", "Object");
            try {
                Log.d("id", this.id);
            } catch (Exception ex) {
            }
            try {
                Log.d("hash", this.hash);
            } catch (Exception e) {
            }
            try {
                Log.d("value", String.valueOf(value));
            } catch (Exception ex) {
            }
            try {
                Log.d("userId", this.userId);
            } catch (Exception ex) {
            }
            try {
                Log.d("judgeId", this.judgeId);
            } catch (Exception ex) {
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
