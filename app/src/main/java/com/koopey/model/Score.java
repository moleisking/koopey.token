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
public class Score implements Serializable, Comparator<Score>, Comparable<Score> {

    public static final String SCORE_FILE_NAME = "score.dat";
    private static final String LOG_HEADER = "SCORE";

    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String userId;
    public String type = "";
    public int elo = 0;
    public int draws = 0;
    public int losses = 0;
    public int wins = 0;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0;
    public long updateTimeStamp = 0;
    public long deleteTimeStamp = 0;

    public Score() {
    }

    @Override
    public int compare(Score o1, Score o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Score o) {
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
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("score")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("score"));//{score:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("score")) {
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
            if (jsonObject.has("hash")) {
                this.hash = jsonObject.getString("hash");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            if (jsonObject.has("userId")) {
                this.userId = jsonObject.getString("userId");
            }
            //Integers
            if (jsonObject.has("draws")) {
                this.draws = jsonObject.getInt("draws");
            }
            if (jsonObject.has("elo")) {
                this.elo = jsonObject.getInt("elo");
            }
            if (jsonObject.has("losses")) {
                this.losses = jsonObject.getInt("losses");
            }
            if (jsonObject.has("wins")) {
                this.wins = jsonObject.getInt("wins");
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
            } catch (Exception e) {
            }
        }
        try {
            if (!this.hash.equals("")) {
                jsonObject.put("hash", this.hash);
            }
        } catch (Exception e) {
        }
        if (!this.type.equals("")) {
            try {
                jsonObject.put("type", this.type);
            } catch (Exception e) {
            }
        }
        if (!this.userId.equals("")) {
            try {
                jsonObject.put("userId", this.userId);
            } catch (Exception e) {
            }
        }
        try {
            jsonObject.put("draws", this.draws);
        } catch (Exception e) {
        }
        try {
            jsonObject.put("elo", this.elo);
        } catch (Exception e) {
        }
        try {
            jsonObject.put("losses", this.losses);
        } catch (Exception e) {
        }
        try {
            jsonObject.put("wins", this.wins);
        } catch (Exception e) {
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

        return jsonObject;
    }

    //*********  Print  *********

    public void print() {
        Log.d("Score", "Object");
        try {
            Log.d("id", this.id);
            Log.d("hash", this.hash);
            Log.d("type", String.valueOf(type));
            Log.d("draws", String.valueOf(draws));
            Log.d("elo", String.valueOf(elo));
            Log.d("losses", String.valueOf(losses));
            Log.d("wins", String.valueOf(wins));
            Log.d("userId", this.userId);
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
