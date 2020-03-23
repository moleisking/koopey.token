package com.koopey.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;

/**
 * Created by Scott on 10/08/2017.
 */

public class Wallet implements Serializable, Comparator<Wallet>, Comparable<Wallet> {

    private static final String LOG_HEADER = "WALLET";
    public static final String WALLET_FILE_NAME = "wallet.dat";

    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public Double latitude = 0.0d;
    public Double longitude = 0.0d;
    public Double value = 0.0d;
    public String name = "";
    public String type = "";
    public String currency = "";
    public boolean authenticated = false;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0;
    public long updateTimeStamp = 0;
    public long deleteTimeStamp = 0;

    @Override
    public int compare(Wallet o1, Wallet o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Wallet o) {
        return compare(this, o);
    }


    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }

    public boolean isEmpty() {
        return this.currency.equals("");
    }

    public boolean isCryptocurrencyEmpty() {
        if (!this.name.equals("") && !this.currency.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("wallet")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("wallet"));//{wallet:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("wallet")) {
                    this.parseJSON(new JSONObject(json));//{id:1}
                }
            }
            //this.parseJSON(new JSONObject(json));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(JSONObject jsonObject) {
        //Strings
        try {
            if (jsonObject.has("id")) {
                this.id = jsonObject.getString("id");
            }
            if (jsonObject.has("hash")) {
                this.hash = jsonObject.getString("hash");
            }
            if (jsonObject.has("name")) {
                this.name = jsonObject.getString("name");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            if (jsonObject.has("currency")) {
                this.currency = jsonObject.getString("currency");
            }
            if (jsonObject.has("latitude")) {
                this.latitude = jsonObject.getDouble("latitude");
            }
            if (jsonObject.has("longitude")) {
                this.longitude = jsonObject.getDouble("longitude");
            }
            if (jsonObject.has("value")) {
                this.value = jsonObject.getDouble("value");
            }
            if (jsonObject.has("authenticated")) {
                this.authenticated = jsonObject.getBoolean("authenticated");
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
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            //Strings
            if (!this.id.equals("")) {
                json.put("id", this.id);
            }
            if (!this.hash.equals("")) {
                json.put("hash", this.hash);
            }
            if (!this.name.equals("")) {
                json.put("name", this.name);
            }
            if (!this.type.equals("")) {
                json.put("type", this.type);
            }
            if (!this.currency.equals("")) {
                json.put("currency", this.currency);
            }
            //Boolean
            json.put("authenticated", this.authenticated);
            //Doubles
            if (this.latitude != 0.0d) {
                json.put("latitude", this.latitude);
            }
            if (this.longitude != 0.0d) {
                json.put("longitude", this.longitude);
            }
            if (this.value != 0.0d) {
                json.put("value", this.value);
            }
            //Longs
            if (this.createTimeStamp != 0) {
                json.put("createTimeStamp", this.createTimeStamp);
            }
            if (this.readTimeStamp != 0) {
                json.put("readTimeStamp", this.readTimeStamp);
            }
            if (this.updateTimeStamp != 0) {
                json.put("updateTimeStamp", this.updateTimeStamp);
            }
            if (this.deleteTimeStamp != 0) {
                json.put("deleteTimeStamp", this.deleteTimeStamp);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
        return json;
    }

    public void print() {
        Log.d("Wallet", "Object");
        try {
            Log.d("id", this.id);
            Log.d("hash", this.hash);
            Log.d("name", this.name);
            Log.d("type", this.type);
            Log.d("currency", this.currency);
            Log.d("latitude", String.valueOf(this.latitude));
            Log.d("longitude", String.valueOf(this.longitude));
            Log.d("value", String.valueOf(this.value));
            Log.d("currency", this.currency);
            Log.d("authenticated", Boolean.toString(this.authenticated));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}