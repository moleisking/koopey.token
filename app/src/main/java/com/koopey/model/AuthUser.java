package com.koopey.model;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by Scott on 23/09/2016.
 */
public class AuthUser extends User implements Serializable {
    private static final String LOG_HEADER = "AUTH:USER:";
    public static final String AUTH_USER_FILE_NAME = "authUser.dat";
    // private transient Context context;
    public String ip = "";
    public String device = "";
    public String type = "";
    public String token = "";
    public String secret = "";
    public String password = "";
    public String oldPassword = "";
    public String newPassword = "";
    public boolean notify = false;
    public boolean terms = false;
    public boolean cookies = false;
    public Transactions transactions = new Transactions();
    public Messages messages = new Messages();

    public AuthUser() {
        this.token = "";
        //this.context = context;
    }

    @Override
    public String toString() {
        //JSONObject adds backslash in front of forward slashes causing corrupt images
        return this.toJSONObject().toString().replaceAll("\\/", "/");
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
            //this.parseJSON(new JSONObject(json));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void parseJSON(JSONObject jsonObject) {

        try {
            //Boolean
            if (jsonObject.has("cookies")) {
                this.cookies = jsonObject.getBoolean("cookies");
            }
            if (jsonObject.has("terms")) {
                this.terms = jsonObject.getBoolean("terms");
            }
            if (jsonObject.has("notify")) {
                this.notify = jsonObject.getBoolean("notify");
            }
            //Strings
            if (jsonObject.has("device")) {
                this.token = jsonObject.getString("device");
            }
            if (jsonObject.has("ip")) {
                this.token = jsonObject.getString("ip");
            }
            if (jsonObject.has("token")) {
                this.token = jsonObject.getString("token");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            //Arrays
            if (jsonObject.has("transactions")) {
                this.transactions.parseJSON(jsonObject.getJSONArray("transactions"));
            }
            if (jsonObject.has("messages")) {
                this.messages.parseJSON(jsonObject.getJSONArray("messages"));
            }
            super.parseJSON(jsonObject);
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void print() {
        Log.d("AuthUser", "Object");
        try {
            Log.d("IP", this.ip);
            Log.d("Device", this.device);
            Log.d("Token", this.getToken());
            Log.d("Type", this.type);
        } catch (Exception e) {
        }
        this.transactions.print();
        this.messages.print();
        super.print();
    }

    /* * *  PROPERTIES * * */

    public Image getAvatarImage() {
        Image img = new Image();
        img.uri = this.avatar;
        return img;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    /* * *  FUNCTIONS * * */

    public boolean hasToken() {
        return token != null && !token.equals("") ? true : false;
    }

    public boolean isEmpty() {
        if (this.token.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    //Note* Only applies to my user create
    public boolean isCreate() {
        if (!this.alias.equals("")
                && !this.name.equals("")
                && !this.email.equals("")
                && !this.password.equals("")
                && !this.mobile.equals("")
                && !this.location.isEmpty()
                && this.avatar.length() > 0
                && this.birthday != 0) {
            return true;
        } else {
            return false;
        }
    }

    public User getUser() {
        return (User) this;
    }

    public void syncronize(User user) {
        this.avatar = user.avatar;
        this.description = user.description;
        this.mobile = user.mobile;
        this.education = user.education;
        this.currency = user.currency;
        this.location = user.location;
    }

    public JSONObject toJSONObject() {
        JSONObject json = super.toJSONObject(); //new JSONObject();
        try {
            //Strings
            if (!this.token.equals("")) {
                json.put("oldPassword", this.oldPassword);
            }
            if (!this.token.equals("")) {
                json.put("newPassword", this.newPassword);
            }
            if (!this.password.equals("")) {
                json.put("password", this.password);
            }
            if (!this.token.equals("")) {
                json.put("token", this.token);
            }
            if (!this.ip.equals("")) {
                json.put("ip", this.token);
            }
            //Booleans
            json.put("notify", this.notify);
            json.put("terms", this.terms);
            json.put("cookies", this.cookies);
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
        return json;
    }

}
