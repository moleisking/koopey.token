package com.koopey.model;
/*
* Bitmap class is non serializable amd therefore not used
* */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Comparator;
import java.util.UUID;

//import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import com.koopey.R;
import com.koopey.view.RoundImage;

public class Asset implements Serializable, Comparator<Asset>, Comparable<Asset> {

    //Objects
    public Advert advert = new Advert();
    public User user = new User();
    //Arrays
    public Images images = new Images();
    public Location location = new Location();
    public Reviews reviews = new Reviews();
    public Tags tags = new Tags();
    //Strings
    public static final String ASSET_FILE_NAME = "Asset.dat";
    private static final String LOG_HEADER = "ASSET:";
    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String title = "";
    public String description = "";
    public String dimensiontUnit = "";
    public String weightUnit = "";
    public String currency = "eur";
    public String fileData = "";
    public String fileName = "";
    public String fileType = "";
    //Doubles
    public Double width = 0.0d; //cm
    public Double height = 0.0d; //cm
    public Double length = 0.0d; //cm
    public Double weight = 0.0d; //kg
    public Double value = 200d;

    //Longs
    public long fileSize = 0;
    public long manufactureDate = 0;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0; //read only
    public long updateTimeStamp = 0; //read only
    public long deleteTimeStamp = 0; //read only
    //Ints
    public int distance = 0;
    public int quantity = 0;
    //Booleans
    public boolean available = true;

    public Asset() {
    }

    @Override
    public int compare(Asset o1, Asset o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Asset o) {
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
        if (hasImage && !this.title.equals("") && this.value >= 0 && this.tags.size() >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean equals(Asset asset) {
        if (asset.id.equals(this.id)) {
            return true;
        } else {
            return false;
        }
    }

    /*********
     * Helpers
     *********/

    public String getValue() {
        return Double.toString(value);
    }

    /*********
     * JSON
     *********/

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            //Booleans
            jsonObject.put("available", this.available);
            //Integer
            if (this.distance != 0) {
                jsonObject.put("distance", this.distance);
            }
            if (this.quantity != 0) {
                jsonObject.put("quantity", this.quantity);
            }
            //Doubles
            if (this.fileSize != 0) {
                jsonObject.put("fileSize", this.fileSize);
            }
            if (this.manufactureDate != 0) {
                jsonObject.put("manufactureDate", this.manufactureDate);
            }
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
            if (this.value != 0) {
                jsonObject.put("price", this.value);
            }
            if (this.weight != 0) {
                jsonObject.put("weight", this.weight);
            }
            if (this.width != 0) {
                jsonObject.put("width", this.width);
            }
            if (this.length != 0) {
                jsonObject.put("length", this.length);
            }
            if (this.height != 0) {
                jsonObject.put("height", this.height);
            }
            //Strings
            if (!this.id.equals("")) {
                jsonObject.put("id", this.id);
            }
            if (!this.hash.equals("")) {
                jsonObject.put("hash", this.hash);
            }
            if (!this.fileName.equals("")) {
                jsonObject.put("fileName", this.fileName);
            }
            if (!this.fileType.equals("")) {
                jsonObject.put("fileType", this.fileType);
            }
            if (!this.fileData.equals("")) {
                jsonObject.put("fileData", this.fileData);
            }
            if (!this.hash.equals("")) {
                jsonObject.put("hash", this.hash);
            }
            if (!this.title.equals("")) {
                jsonObject.put("title", this.title);
            }
            if (!this.description.equals("")) {
                jsonObject.put("description", this.description);
            }
            if (!this.dimensiontUnit.equals("")) {
                jsonObject.put("dimensiontUnit", this.dimensiontUnit);
            }
            if (!this.weightUnit.equals("")) {
                jsonObject.put("weightUnit", this.weightUnit);
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
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("asset")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("asset"));//{user:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("asset")) {
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
            if (jsonObject.has("manufactureTimeStamp")) {
                this.manufactureDate = jsonObject.getLong("manufactureDate");
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
            //Integers
            if (jsonObject.has("distance")) {
                this.distance = jsonObject.getInt("distance");
            }
            if (jsonObject.has("quantity")) {
                this.quantity = jsonObject.getInt("quantity");
            }
            //Doubles
            if (jsonObject.has("height")) {
                this.height = jsonObject.getDouble("height");
            }
            if (jsonObject.has("length")) {
                this.length = jsonObject.getDouble("length");
            }
            if (jsonObject.has("value")) {
                this.value = jsonObject.getDouble("value");
            }
            if (jsonObject.has("width")) {
                this.width = jsonObject.getDouble("width");
            }
            if (jsonObject.has("weight")) {
                this.weight = jsonObject.getDouble("weight");
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
            if (jsonObject.has("description")) {
                this.description = jsonObject.getString("description");
            }
            if (jsonObject.has("currency")) {
                this.currency = jsonObject.getString("currency");
            }
            if (jsonObject.has("dimensiontUnit")) {
                this.dimensiontUnit = jsonObject.getString("dimensiontUnit");
            }
            if (jsonObject.has("weightUnit")) {
                this.weightUnit = jsonObject.getString("weightUnit");
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
        try {
            Log.d("Asset", "Object");
            Log.d("id", String.valueOf(id));
            Log.d("hash", this.hash);
            Log.d("Title", this.title);
            Log.d("dimensionUnit", this.dimensiontUnit);
            Log.d("weightUnit", this.weightUnit);
            Log.d("description", this.description);
            Log.d("weight", String.valueOf(this.weight));
            Log.d("height", String.valueOf(this.height));
            Log.d("width", String.valueOf(this.width));
            Log.d("length", String.valueOf(this.length));
            Log.d("value", String.valueOf(this.value));
            Log.d("currency", this.currency);
            Log.d("available", String.valueOf(this.available));
            Log.d("distance", String.valueOf(this.distance));
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
