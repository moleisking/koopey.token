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
public class Piece implements Serializable, Comparator<Piece>, Comparable<Piece> {

    public static final String PIECE_FILE_NAME = "piece.dat";
    private static final String LOG_HEADER = "PIECE";

    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String type;
    public String color = "";
    public int file;
    public int moves;
    public int rank;
    public int value = 0;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0;
    public long updateTimeStamp = 0;
    public long deleteTimeStamp = 0;

    public Piece() {
    }

    @Override
    public int compare(Piece o1, Piece o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Piece o) {
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
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("piece")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("piece"));//{piece:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("piece")) {
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
            if (jsonObject.has("color")) {
                this.color = jsonObject.getString("color");
            }
            //Integers
            if (jsonObject.has("value")) {
                this.value = jsonObject.getInt("value");
            }
            if (jsonObject.has("moves")) {
                this.value = jsonObject.getInt("moves");
            }
            if (jsonObject.has("file")) {
                this.value = jsonObject.getInt("file");
            }
            if (jsonObject.has("rank")) {
                this.value = jsonObject.getInt("rank");
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
        if (!this.color.equals("")) {
            try {
                jsonObject.put("color", this.color);
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
                jsonObject.put("updateTimeStamp", this.updateTimeStamp);
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
            jsonObject.put("file", this.file);
            jsonObject.put("moves", this.moves);
            jsonObject.put("rank", this.rank);
            jsonObject.put("value", this.value);
        } catch (Exception e) {
        }
        return jsonObject;
    }

    //*********  Print  *********

    public void print() {
            Log.d("Piece", "Object");
            try {
                Log.d("id", this.id);
                Log.d("type", this.type);
                Log.d("hash", this.hash);
                Log.d("color", this.color);
                Log.d("file", String.valueOf(file));
                Log.d("rank", String.valueOf(rank));
                Log.d("moves", String.valueOf(moves));
                Log.d("value", String.valueOf(value));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
