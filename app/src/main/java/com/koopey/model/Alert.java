/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.koopey.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Scott on 12/09/2017.
 */

public class Alert implements Serializable {
    public static final String ALERT_FILE_NAME = "alert.dat";
    private static final String LOG_HEADER = "ALERT";

    public String id = "";
    public String type = "";
    public String message = "";
    public String object = "";
    public long createTimeStamp = System.currentTimeMillis();

    public Alert() {
    }

    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }

    public boolean isError(){
        if (this.type.equals("error")){
            return true;
        } else {
            return false;
        }
    }

    public boolean isSuccess(){
        if (this.type.equals("success")){
            return true;
        } else {
            return false;
        }
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("alert")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("alert"));//{alert:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("alert")) {
                    this.parseJSON(new JSONObject(json));//{id:1}
                }
            }
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
            if (jsonObject.has("message")) {
                this.message = jsonObject.getString("message");
            }
            if (jsonObject.has("object")) {
                this.object = jsonObject.getString("object");
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            //Strings
            if (!this.id.equals("")) {
                json.put("id", this.id);
            }
            if (!this.type.equals("")) {
                json.put("type", this.type);
            }
            if (!this.message.equals("")) {
                json.put("message", this.message);
            }
            if (!this.object.equals("")) {
                json.put("object", this.object);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
        return json;
    }

    public void print() {
        Log.d("Alert", "Object");
        Log.d("id", this.id);
        Log.d("type", this.type);
        Log.d("message", this.message);
        Log.d("object", this.object);
    }

}
