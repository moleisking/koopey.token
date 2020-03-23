/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.koopey.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Scott on 23/06/2018.
 */

public class Article implements Serializable, Comparator<Article>, Comparable<Article> {

    //Objects
    public Advert advert = new Advert();
    public User user = new User();
    //Arrays
    public Images images = new Images();
    public Location location = new Location();
    public Reviews reviews = new Reviews();
    public Tags tags = new Tags();
    //Strings
    public static final String ARTICLE_FILE_NAME = "Article.dat";
    private static final String LOG_HEADER = "ARTICLE:";
    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String title = "";
    public String content = "";
    //Longs
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0; //read only
    public long updateTimeStamp = 0; //read only
    public long deleteTimeStamp = 0; //read only
    //Ints
    public int distance = 0;
    public int quantity = 0;
    //Booleans
    public boolean available = true;

    public Article() {
    }

    @Override
    public int compare(Article o1, Article o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Article o) {
        return compare(this, o);
    }

    @Override
    public String toString() {
        //JSONObject adds backslash in front of forward slashes causing corrupt images
        return this.toJSONObject().toString().replaceAll("\\/", "/");
    }

    /*********
     * Checks
     *********/

    public boolean isValid() {
        boolean hasImage = false;

        //Check if at least one images was uploaded
        for (int i = 0; i < 4; i++) {
            if (!this.images.get(i).uri.equals("")) {
                hasImage = true;
            }
        }
        //Note* userid is also passed in token so userid check is not necessary
        if (hasImage && !this.title.equals("") && !this.content.equals("") ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean equals(Article article) {
        if (article.id.equals(this.id)) {
            return true;
        } else {
            return false;
        }
    }

    /*********
     * JSON
     *********/

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            //Booleans
            jsonObject.put("available", this.available);
            //Doubles
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
            //Strings
            if (!this.id.equals("")) {
                jsonObject.put("id", this.id);
            }
            if (!this.hash.equals("")) {
                jsonObject.put("hash", this.hash);
            }
            if (!this.hash.equals("")) {
                jsonObject.put("hash", this.hash);
            }
            if (!this.title.equals("")) {
                jsonObject.put("title", this.title);
            }
            if (!this.content.equals("")) {
                jsonObject.put("content", this.content);
            }
            //Objects
            try {
                jsonObject.put("advert", this.advert.toJSONObject());
            } catch (Exception e) {

            }
            try {
                jsonObject.put("location", this.location.toJSONObject());
            } catch (Exception e) {

            }
            try {
                jsonObject.put("user", this.user.toJSONObject());
            } catch (Exception e) {

            }
            //Arrays
            if (this.images.size() > 0) {
                jsonObject.put("images", this.images.toJSONArray());
            }
            if (this.reviews.size() > 0) {
                jsonObject.put("reviews", this.reviews.toJSONArray());
            }
            if (this.tags.size() > 0) {
                jsonObject.put("tags", this.tags.toJSONArray());
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }

        return jsonObject;
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("article")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("article"));//{user:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("article")) {
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
            //Integers
            if (jsonObject.has("distance")) {
                this.distance = jsonObject.getInt("distance");
            }
            if (jsonObject.has("quantity")) {
                this.quantity = jsonObject.getInt("quantity");
            }
            //Strings
            if (jsonObject.has("id")) {
                this.id = jsonObject.getString("id");
            }
            if (jsonObject.has("hash")) {
                this.hash = jsonObject.getString("hash");
            }
            if (jsonObject.has("title")) {
                this.title = jsonObject.getString("title");
            }
            if (jsonObject.has("content")) {
                this.content = jsonObject.getString("content");
            }
            //Objects
            if (jsonObject.has("advert")) {
                this.advert.parseJSON(jsonObject.getJSONObject("advert"));
            }
            if (jsonObject.has("location")) {
                this.location.parseJSON(jsonObject.getJSONObject("location"));
            }
            if (jsonObject.has("user")) {
                this.user.parseJSON(jsonObject.getJSONObject("user"));
            }
            //Arrays
            if (jsonObject.has("images")) {
                this.images.parseJSON(jsonObject.getJSONArray("images"));
            }
            if (jsonObject.has("reviews")) {
                this.reviews.parseJSON(jsonObject.getJSONArray("reviews"));
            }
            if (jsonObject.has("tags")) {
                this.tags.parseJSON(jsonObject.getString("tags"));
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    /*********
     * Print
     *********/

    public void print() {
        Log.d("Article", "Object");
        try {
            Log.d("id", String.valueOf(id));
            Log.d("hash", this.hash);
            Log.d("Title", this.title);
            Log.d("content", this.content);
            Log.d("available", String.valueOf(this.available));
            this.advert.print();
            this.location.print();
            this.reviews.print();
            this.images.print();
            this.user.print();
            this.tags.print();
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
