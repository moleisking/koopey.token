package com.koopey.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Scott on 12/06/2017.
 */
public class Search implements Serializable {

    private static final String LOG_HEADER = "SEARCH";
    public String id = UUID.randomUUID().toString();
    public String userId = "";
    public String productId = "";
    public String transactionId = "";
    public String type = "users";
    public String period = "hour";
    public String currency = "btc";
    public String name = "";
    public String alias = "";
    public String measure = "metric";
    public int min = 0;
    public int max = 5000;
    public int radius = 10;
    public Double latitude;
    public Double longitude;
    public Tags tags = new Tags();
    public long start = 0;
    public long end = 0;
    public long createTimeStamp = System.currentTimeMillis();

    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            //Strings
            if (!this.id.equals("")) {
               json.put("id", this.id);
            }
            if (!this.userId.equals("")) {
                json.put("userId", this.userId);
            }
            if (!this.productId.equals("")) {
                json.put("productId", this.productId);
            }
            if (!this.transactionId.equals("")) {
                json.put("transactionId", this.transactionId);
            }
            if (!this.type.equals("")) {
                json.put("type", this.type);
            }
            if (!this.currency.equals("")) {
                json.put("currency", this.currency);
            }
            if (!this.period.equals("")) {
                json.put("period", this.period);
            }
            if (!this.alias.equals("")) {
                json.put("alias", this.alias);
            }
            if (!this.name.equals("")) {
                json.put("name", this.name);
            }
            if (!this.measure.equals("")) {
                json.put("measure", this.measure);
            }
            //Integers
            json.put("min", this.min);
            json.put("max", this.max);
            json.put("radius", this.radius);
            //Doubles
            if (this.latitude != 0.0d) {
                json.put("latitude", this.latitude);
            }
            if (this.longitude != 0.0d) {
                json.put("longitude", this.longitude);
            }
            //Longs
            if (this.createTimeStamp != 0) {
                json.put("createTimeStamp", this.createTimeStamp);
            }
            //Tags
            if (this.tags.size() > 0) {
                json.put("tags", tags.toJSONArray() );
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
        return json;
    }

    public void print() {
        try {
            Log.d("Search", "Object");
            Log.d("id", this.id);
            Log.d("type", this.type);
            Log.d("period", this.period);
            Log.d("min", Integer.toString(this.min));
            Log.d("max", Integer.toString(this.max));
            Log.d("currency", this.currency);
            Log.d("measure", this.measure);
            Log.d("alias", this.alias);
            Log.d("name", this.name);
            Log.d("radius", Integer.toString(this.radius));
            Log.d("latitude", this.latitude.toString());
            Log.d("longitude", this.longitude.toString());
            this.tags.print();
        } catch (Exception e) {
        }
    }
}
