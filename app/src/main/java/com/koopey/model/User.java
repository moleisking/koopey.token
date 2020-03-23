package com.koopey.model;
/*
* NOTE: 1) Bitmap class is non serializable amd therefore not used.*
* */

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

public class User implements Serializable, Comparator<User>, Comparable<User> {
    //Constants
    public static final String USER_FILE_NAME = "user.dat";
    private static final String LOG_HEADER = "USER";
    //Booleans
    public boolean available = false;
    public boolean authenticated = false;
    public boolean track = false;
    //Integers
    public int distance = 0;
    public int score = 0;
    //Longs
    public long birthday = 0;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0; //read only
    public long updateTimeStamp = 0; //read only
    public long deleteTimeStamp = 0; //read only
    //Strings
    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String alias = "";
    public String avatar = "";
    public String education = "";
    public String name = "";
    public String mobile = "";
    public String email = "";
    public String description = "";
    public String type = "complete";
    public String currency = "eur";
    public String language = "en";
    public String measure = "metric";
    public String player = "grey";
    //Objects
    public Location location = new Location();
    public Reviews reviews = new Reviews();
    public Scores scores = new Scores();
    public Wallets wallets = new Wallets();
    //private transient Context context;

    public User() {
    }

    @Override
    public int compare(User o1, User o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(User o) {
        return compare(this, o);
    }


    public String getBirthdayString() {
        Date date = new Date(birthday);
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(date);
    }

    public User getUserBasicWithAvatar() {
        User userBasic = new User();
        userBasic.id = this.id;
        userBasic.type = "basic";
        userBasic.alias = this.alias;
        userBasic.name = this.name;
        userBasic.score = this.score;
        userBasic.player = this.player;
        userBasic.avatar = this.avatar;
        userBasic.currency = this.currency;
        userBasic.language = this.language;
        userBasic.reviews = this.reviews;
        userBasic.wallets = this.wallets;
        // userBasic.locations = this.locations;
        return userBasic;
    }

    public boolean equals(User user) {
        if (user != null && user.id.equals(this.id) && user.alias.equals(this.alias)) {
            return true;
        } else {
            return false;
        }
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("user")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("user"));//{user:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("user")) {
                    this.parseJSON(new JSONObject(json));//{id:1}
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(JSONObject jsonObject) {
        try {
            //Booleans
            if (jsonObject.has("available")) {
                this.available = jsonObject.getBoolean("available");
            }
            if (jsonObject.has("authenticated")) {
                this.authenticated = jsonObject.getBoolean("authenticated");
            }
            if (jsonObject.has("track")) {
                this.track = jsonObject.getBoolean("track");
            }
            //Integers
            if (jsonObject.has("score")) {
                this.score = Integer.parseInt(jsonObject.getString("score"));
            }
            //Longs
            if (jsonObject.has("birthday")) {
                this.birthday = Long.parseLong(jsonObject.getString("birthday"));
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
            //Doubles
            if (jsonObject.has("distance")) {
                this.distance = (int) Math.round(Double.parseDouble(jsonObject.getString("distance")));
            }
            //Strings
            if (jsonObject.has("alias")) {
                this.alias = jsonObject.getString("alias");
            }
            if (jsonObject.has("avatar")) {
                this.avatar = jsonObject.getString("avatar");
            }
            if (jsonObject.has("currency")) {
                this.currency = jsonObject.getString("currency");
            }
            if (jsonObject.has("description")) {
                this.description = jsonObject.getString("description");
            }
            if (jsonObject.has("education")) {
                this.education = jsonObject.getString("education");
            }
            if (jsonObject.has("email")) {
                this.email = jsonObject.getString("email");
            }
            if (jsonObject.has("hash")) {
                this.hash = jsonObject.getString("hash");
            }
            if (jsonObject.has("id")) {
                this.id = jsonObject.getString("id");
            }
            if (jsonObject.has("language")) {
                this.language = jsonObject.getString("language");
            }
            if (jsonObject.has("measure")) {
                this.measure = jsonObject.getString("measure");
            }
            if (jsonObject.has("mobile")) {
                this.mobile = jsonObject.getString("mobile");
            }
            if (jsonObject.has("name")) {
                this.name = jsonObject.getString("name");
            }
            if (jsonObject.has("player")) {
                this.player = jsonObject.getString("player");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            //Objects
            if (jsonObject.has("location")) {
                this.location.parseJSON(jsonObject.getJSONObject("location"));
            }
            //Arrays
            if (jsonObject.has("scores")) {
                this.scores.parseJSON(jsonObject.getJSONArray("scores"));
            }
            if (jsonObject.has("reviews")) {
                this.reviews.parseJSON(jsonObject.getJSONArray("reviews"));
            }
            if (jsonObject.has("wallets")) {
                this.wallets.parseJSON(jsonObject.getJSONArray("wallets"));
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    //Note: Used in Asset and Game Object
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            //Booleans
            jsonObject.put("available", this.available);
            jsonObject.put("track", this.track);
            jsonObject.put("authenticated", this.authenticated);
            //Integers
            if (this.birthday != 0) {
                jsonObject.put("score", this.score);
            }
            //Longs
            if (this.birthday != 0) {
                jsonObject.put("birthday", this.birthday);
            }
            //Strings
            if (!this.id.equals("")) {
                jsonObject.put("avatar", this.avatar);
            }
            if (!this.id.equals("")) {
                jsonObject.put("id", this.id);
            }
            if (!this.education.equals("")) {
                jsonObject.put("education", this.education);
            }
            if (!this.hash.equals("")) {
                jsonObject.put("hash", this.hash);
            }
            if (!this.type.equals("")) {
                jsonObject.put("type", this.type);
            }
            if (!this.alias.equals("")) {
                jsonObject.put("alias", this.alias);
            }
            if (!this.name.equals("")) {
                jsonObject.put("name", this.name);
            }
            if (!this.player.equals("")) {
                jsonObject.put("player", this.player);
            }
            if (!this.description.equals("")) {
                jsonObject.put("description", this.description);
            }
            if (!this.email.equals("")) {
                jsonObject.put("email", this.email);
            }
            if (!this.mobile.equals("")) {
                jsonObject.put("mobile", this.mobile);
            }
            if (!this.language.equals("")) {
                jsonObject.put("language", this.language);
            }
            if (!this.currency.equals("")) {
                jsonObject.put("currency", this.currency);
            }
            if (!this.measure.equals("")) {
                jsonObject.put("measure", this.measure);
            }
            //Objects
            try {
                jsonObject.put("location", this.location.toJSONObject());
            } catch (Exception e) {
            }
            //Arrays
            if (this.reviews.size() > 0) {
                jsonObject.put("reviews", this.reviews.toJSONArray());
            }
            if (this.scores.size() > 0) {
                jsonObject.put("scores", this.scores.toJSONArray());
            }
            if (this.wallets.size() > 0) {
                jsonObject.put("wallets", wallets.toJSONArray());
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
        return jsonObject;
    }

    public void print() {
        Log.d("User", "Object");
        try {
            Log.d("alias", this.alias);
            Log.d("authenticated", String.valueOf(this.authenticated));
            Log.d("available", String.valueOf(this.available));
            Log.d("avatar", this.avatar);
            Log.d("birthday", String.valueOf(this.birthday));
            Log.d("currency", this.currency);
            Log.d("description", this.description);
            Log.d("distance", Integer.toString(this.distance));
            Log.d("education", this.education);
            Log.d("hash", this.hash);
            Log.d("language", this.language);
            Log.d("id", this.id);
            Log.d("measure", this.measure);
            Log.d("mobile", this.mobile);
            Log.d("name", this.name);
            Log.d("type", this.type);
            Log.d("score", Integer.toString(this.score));
            Log.d("player", this.player);
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
        this.location.print();
        this.reviews.print();
        this.wallets.print();
    }
}
