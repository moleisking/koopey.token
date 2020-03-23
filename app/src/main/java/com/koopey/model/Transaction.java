package com.koopey.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Scott on 18/01/2017.
 */
public class Transaction implements Serializable {

    private static final String LOG_HEADER = "TRANSACTION";
    public static final String TRANSACTION_FILE_NAME = "transaction.dat";

    public Location currentLocation;
    public Location startLocation;
    public Location endLocation;
    public String id = UUID.randomUUID().toString();
    public String guid = UUID.randomUUID().toString();
    public String secret = "";
    public String hash = "";
    public Users users = new Users();
    public String name = "";
    public String reference = "";
    public String type = "";
    public String state = "quote";
    public String currency = "eur";
    public String timeZone = "Etc/UTC";
    public String period = "once";
    public Double itemValue = 0.0d;
    public Double totalValue = 0.0d;
    public Integer quantity = 0;
    public long startTimeStamp;
    public long endTimeStamp;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0; //read only
    public long updateTimeStamp = 0; //read only
    public long deleteTimeStamp = 0; //read only

    public Transaction() {
    }

    @Override
    public String toString() {
        //JSONObject adds backslash in front of forward slashes causing corrupt images
        return this.toJSONObject().toString().replaceAll("\\/", "/");
    }

    public int countBuyers() {
        int counter = 0;
        for (int i = 0; i <= this.users.size(); i++) {
            User user = this.users.get(i);
            if (user.type == "buyer") {
                counter++;
            }
        }
        return counter;
    }

    public int countSellers() {
        int counter = 0;
        for (int i = 0; i <= users.size(); i++) {
            User user = this.users.get(i);
            if (user.type == "seller") {
                counter++;
            }
        }
        return counter;
    }

    public boolean isEmpty() {
        //Note* userid is also passed in token so userId check is not necessary
        if (this.name.equals("")
                && this.type.equals("")
                && this.currency.equals("")
                && this.quantity == 0
                && this.itemValue == 0
                && this.totalValue == 0
                && (!isQuote() || !isInvoice() || !isReceipt())) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isQuote() {
        return this.state.equals("quote");
    }

    public boolean isInvoice() {
        return this.state.equals("invoice");
    }

    public boolean isReceipt() {
        return this.state.equals("receipt");
    }

    public boolean isBitcoin() {
        return this.currency.equals("btc");
    }

    public boolean isEthereum() {
        return this.currency.equals("eth");
    }

    public boolean isFiat() {
        if (this.currency.equals("eur") || this.currency.equals("gbp") || this.currency.equals("usd") || this.currency.equals("zar")) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isSeller(User authUser) {
        boolean result = false;
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.id.equals(authUser.id) && user.type.equals("seller")) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean isBuyer(User authUser) {
        boolean result = false;
        for (int i = 0; i < this.users.size(); i++) {
            User user = users.get(i);
            if (user.id.equals(authUser.id) && user.type.equals("buyer")) {
                result = true;
                break;
            }
        }
        return result;
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

    public void setCurrentPosition(LatLng latLng, String address) {
        this.currentLocation.longitude = latLng.longitude;
        this.currentLocation.latitude = latLng.latitude;
        this.currentLocation.address = address;
    }

    public void setStartLocation(LatLng latLng, String address) {
        this.startLocation.longitude = latLng.longitude;
        this.startLocation.latitude = latLng.latitude;
        this.startLocation.address = address;
    }

    public void setEndLocation(LatLng latLng, String address) {
        this.endLocation.longitude = latLng.longitude;
        this.endLocation.latitude = latLng.latitude;
        this.endLocation.address = address;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            //Longs
            if (this.startTimeStamp != 0) {
                jsonObject.put("startTimeStamp", this.startTimeStamp);
            }
            if (this.endTimeStamp != 0) {
                jsonObject.put("endTimeStamp", this.endTimeStamp);
            }
            //Strings
            try {
                jsonObject.put("id", this.id);
            } catch (Exception e) {
            }
            if (!this.hash.equals("")) {
                jsonObject.put("hash", this.hash);
            }
            try {
                jsonObject.put("type", this.type);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("period", this.period);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("state", this.state);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("name", this.name);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("secret", this.secret);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("reference", this.reference);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("currency", this.currency);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("quantity", this.quantity);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("itemValue", this.itemValue);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("totalValue", this.totalValue);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("startLocation", this.startLocation.toJSONObject());
            } catch (Exception e) {
            }
            try {
                jsonObject.put("endLocation", this.endLocation.toJSONObject());
            } catch (Exception e) {
            }
            try {
                jsonObject.put("currentLocation", this.currentLocation.toJSONObject());
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
                jsonObject.put("updateTimeStamp", this.updateTimeStamp);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("deleteTimeStamp", this.deleteTimeStamp);
            } catch (Exception e) {
            }
            //Arrays
            if (this.users.size() > 0) {
                try {
                    jsonObject.put("users", this.users.toJSONArray());
                } catch (Exception e) {
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
        return jsonObject;
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("transaction")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("transaction"));//{transaction:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("transaction")) {
                    this.parseJSON(new JSONObject(json));//{id:1}
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(JSONObject jsonObject) {
        try {
            //Longs
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
            if (jsonObject.has("name")) {
                this.name = jsonObject.getString("name");
            }
            if (jsonObject.has("reference")) {
                this.reference = jsonObject.getString("reference");
            }
            if (jsonObject.has("secret")) {
                this.secret = jsonObject.getString("secret");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            if (jsonObject.has("period")) {
                this.period = jsonObject.getString("period");
            }
            if (jsonObject.has("state")) {
                this.state = jsonObject.getString("state");
            }
            if (jsonObject.has("currency")) {
                this.currency = jsonObject.getString("currency");
            }
            if (jsonObject.has("timeZone")) {
                this.timeZone = jsonObject.getString("timeZone");
            }
            if (jsonObject.has("quantity")) {
                this.quantity = jsonObject.getInt("quantity");
            }
            if (jsonObject.has("itemValue")) {
                this.itemValue = jsonObject.getDouble("itemValue");
            }
            if (jsonObject.has("totalValue")) {
                this.totalValue = jsonObject.getDouble("totalValue");
            }
            if (jsonObject.has("startLocation")) {
                this.startLocation.parseJSON(jsonObject.getString("startLocation"));
            }
            if (jsonObject.has("endLocation")) {
                this.endLocation.parseJSON(jsonObject.getString("endLocation"));
            }
            if (jsonObject.has("currentLocation")) {
                this.currentLocation.parseJSON(jsonObject.getString("currentLocation"));
            }
            //Arrays
            if (jsonObject.has("users")) {
                this.users.parseJSON(jsonObject.getJSONArray("users"));
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print() {
        Log.d("Transaction", "Object");
        try {
            Log.d("id", String.valueOf(id));
            Log.d("hash", this.hash);
            Log.d("name", this.name);
            Log.d("type", this.type);
            Log.d("quantity", String.valueOf(this.quantity));
            Log.d("itemValue", String.valueOf(this.itemValue));
            Log.d("totalValue", String.valueOf(this.totalValue));
            Log.d("currency", this.currency);
            Log.d("period", this.period);
            Log.d("startTimeStamp", this.getStartTimeStampAsString());
            Log.d("endTimeStamp", this.getEndTimeStampAsString());
            this.users.print();
            this.startLocation.print();
            this.endLocation.print();
            this.currentLocation.print();
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
