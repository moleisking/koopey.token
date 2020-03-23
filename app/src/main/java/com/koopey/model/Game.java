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
public class Game implements Serializable, Comparator<Game>, Comparable<Game> {

    public static final String GAME_FILE_NAME = "game.dat";
    private static final String LOG_HEADER = "GAME";
    public Pieces pieces = new Pieces();
    public Users users = new Users();
    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String[] moves;
    public String type;
    public String token = "";
    public int counter = 0;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0;
    public long updateTimeStamp = 0;
    public long deleteTimeStamp = 0;
    public boolean[] defeats;

    public Game() {
    }

    @Override
    public int compare(Game o1, Game o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Game o) {
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
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("game")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("game"));//{game:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("game")) {
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
            //Integers
            if (jsonObject.has("counter")) {
                this.counter = jsonObject.getInt("counter");
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
            //Strings
            if (jsonObject.has("id")) {
                this.id = jsonObject.getString("id");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            if (jsonObject.has("hash")) {
                this.hash = jsonObject.getString("hash");
            }
            if (jsonObject.has("token")) {
                this.token = jsonObject.getString("token");
            }
            //Arrays
            if (jsonObject.has("pieces")) {
                this.pieces.parseJSON(jsonObject.getJSONArray("pieces"));
            }
            if (jsonObject.has("users")) {
                this.users.parseJSON(jsonObject.getJSONArray("users"));
            }
            if (jsonObject.has("moves")) {
                JSONArray arr = jsonObject.getJSONArray("moves");
                this.moves = new String[arr.length()];
                for (int i = 0; i < arr.length(); i++) {
                    this.moves[i] = arr.getString(i);
                }
            }
            if (jsonObject.has("defeats")) {
                JSONArray arr = jsonObject.getJSONArray("defeats");
                this.defeats = new boolean[arr.length()];
                for (int i = 0; i < arr.length(); i++) {
                    this.defeats[i] = arr.getBoolean(i);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        //Integers
        try {
            jsonObject.put("counter", this.counter);
        } catch (Exception e) {
        }
        try {
            jsonObject.put("createTimeStamp", this.createTimeStamp);
        } catch (Exception e) {
        }
        try {
            jsonObject.put("readTimeStamp", this.readTimeStamp);
        } catch (Exception e) {
        }
        try {
            jsonObject.put("createTimeStamp", this.updateTimeStamp);
        } catch (Exception e) {
        }
        try {
            jsonObject.put("deleteTimeStamp", this.deleteTimeStamp);
        } catch (Exception e) {
        }
        //Strings
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
        try {
            jsonObject.put("token", this.token);
        } catch (Exception e) {
        }
        try {
            jsonObject.put("type", this.type);
        } catch (Exception e) {
        }
        //Arrays
        if (this.users.size() > 0) {
            try {
                jsonObject.put("users", this.users.toJSONArray());
            } catch (Exception e) {
            }
        }
        if (this.pieces.size() > 0) {
            try {
                jsonObject.put("pieces", this.pieces.toJSONArray());
            } catch (Exception e) {
            }
        }
        if (this.moves.length > 0) {
            try {
                JSONArray arr = new JSONArray(moves);
                for (int i = 0; i < arr.length(); i++) {
                    arr.put(this.moves[i]);
                }
                jsonObject.put("moves", arr);
            } catch (Exception e) {
            }
        }
        if (this.defeats.length > 0) {
            try {
                JSONArray arr = new JSONArray(defeats);
                for (int i = 0; i < arr.length(); i++) {
                    arr.put(this.defeats[i]);
                }
                jsonObject.put("defeats", arr);
            } catch (Exception e) {
            }
        }

        return jsonObject;
    }

    //*********  Print  *********

    public void print() {
        try {
            Log.d("Game", "Object");
            Log.d("id", this.id);
            Log.d("hash", this.hash);
            Log.d("type", this.type);
            Log.d("counter", String.valueOf(counter));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
