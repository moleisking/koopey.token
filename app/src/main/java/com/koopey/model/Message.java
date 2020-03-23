package com.koopey.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Scott on 15/10/2016.
 */
public class Message implements Serializable, Comparator<Message>, Comparable<Message> {

    private static final String LOG_HEADER = "MSG";
    public Users users = new Users();
    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String subject = "";
    public String text = "";
    public String language = "";
    public boolean archived = false;
    public boolean delivered = false;
    public boolean read = false;
    public boolean sent = false;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0; //read only
    public long updateTimeStamp = 0; //read only
    public long deleteTimeStamp = 0; //read only

    public Message() {
    }

    @Override
    public int compare(Message o1, Message o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Message o) {
        return compare(this, o);
    }

    @Override
    public String toString() {
        //Warning: JSONObject adds backslash in front of forward slashes causing corrupt images
        return this.toJSONObject().toString().replaceAll("\\/", "/");
    }

    public boolean equals(Message message) {
        if (this.id.equals(message.id)){
            return true;
        } else {
            return false;
        }
    }

    public User getSender() {
        User user = new User();

        for (int i = 0; i < this.users.size(); i++) {
            user = this.users.get(i);
            if (user.type.equals("sender")) {
                return user;
            }
        }

        return user;
    }

    public Users getReceivers() {
        Users users = new Users();

        for (int i = 0; i < this.users.size(); i++) {
            User user = this.users.get(i);
            if (user.type.equals("receiver")) {
                users.add(user);
            }
        }

        return users;
    }

    public String getTextSummary() {
        if (this.text.length() <= 20) {
            return this.text + "...";
        } else {
            return this.text.substring(0, 20) + "...";
        }
    }

   /* protected boolean contains(String userId) {
        if (this.fromId.equals(userId) || this.toId.equals(userId)) {
            return true;
        } else {
            return false;
        }
    }*/

    //*********  JSON  *********

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("message")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("message"));//{advert:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("message")) {
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
            //Boolean
            if (jsonObject.has("sent")) {
                this.sent = jsonObject.getBoolean("sent");
            }
            if (jsonObject.has("read")) {
                this.read = jsonObject.getBoolean("read");
            }
            if (jsonObject.has("archived")) {
                this.archived = jsonObject.getBoolean("archived");
            }
            //Long
            if (jsonObject.has("createTimeStamp")) {
                this.createTimeStamp = Long.parseLong(jsonObject.getString("createTimeStamp"));
            }
            if (jsonObject.has("deleteTimeStamp")) {
                this.deleteTimeStamp = Long.parseLong(jsonObject.getString("deleteTimeStamp"));
            }
            if (jsonObject.has("readTimeStamp")) {
                this.readTimeStamp = Long.parseLong(jsonObject.getString("readTimeStamp"));
            }
            if (jsonObject.has("updateTimeStamp")) {
                this.updateTimeStamp = Long.parseLong(jsonObject.getString("updateTimeStamp"));
            }
            //String
            if (jsonObject.has("id")) {
                this.id = jsonObject.getString("id");
            }
            if (jsonObject.has("hash")) {
                this.hash = jsonObject.getString("hash");
            }
            if (jsonObject.has("language")) {
                this.language = jsonObject.getString("language");
            }
            if (jsonObject.has("subject")) {
                this.subject = jsonObject.getString("subject");
            }
            if (jsonObject.has("text")) {
                this.text = jsonObject.getString("text");
            }
            //Arrays
            if (jsonObject.has("users")) {
                this.users.parseJSON(jsonObject.getJSONArray("users"));
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            //Boolean
            jsonObject.put("sent", this.sent);
            jsonObject.put("read", this.read);
            jsonObject.put("archived", this.archived);
            //Long
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
            //String
            if (!this.id.equals("")) {
                jsonObject.put("id", this.id);
            }
            if (!this.hash.equals("")) {
                jsonObject.put("hash", this.hash);
            }
            if (!this.subject.equals("")) {
                jsonObject.put("subject", this.subject);
            }
            if (!this.text.equals("")) {
                jsonObject.put("text", this.text);
            }
            if (!this.language.equals("")) {
                jsonObject.put("language", this.language);
            }
            //Arrays
            if (this.users.size() > 0) {
                jsonObject.put("users", this.users.toJSONArray());
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }

        return jsonObject;
    }

    //*********  Print  *********

    public void print() {
        Log.d("MSG", "Object");
        try {
            Log.d("id", this.id);
        } catch (Exception ex) {
        }
        try {
            Log.d("hash", this.hash);
        } catch (Exception e) {
        }
        try {
            Log.d("text", this.text);
        } catch (Exception ex) {
        }
        this.users.print();
    }
}
