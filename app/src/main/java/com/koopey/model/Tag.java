package com.koopey.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

/**
 * Created by Scott on 03/10/2016.
 */
public class Tag implements Serializable, Comparator<Tag>, Comparable<Tag> {

    private static final String LOG_HEADER = "TAG";
    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String de = "";
    public String en = "";
    public String es = "";
    public String fr = "";
    public String it = "";
    public String pt = "";
    public String zh = "";
    public String type = "";
    // public String text = "";

    public Tag() {
    }

    @Override
    public int compare(Tag o1, Tag o2) {
        return String.CASE_INSENSITIVE_ORDER.compare(o1.id, o2.id);
    }

    @Override
    public int compareTo(Tag o) {
        return compare(this, o);
    }

    //why .replaceAll("\"", "'")
    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }

    public String getText(String language) {
        if (language.equals("de")) {
            return this.de;
        } else if (language.equals("en")) {
            return this.en;
        } else if (language.equals("es")) {
            return this.es;
        } else if (language.equals("fr")) {
            return this.fr;
        } else if (language.equals("it")) {
            return this.it;
        } else if (language.equals("pt")) {
            return this.pt;
        } else if (language.equals("zh")) {
            return this.zh;
        } else {
            return this.en;
        }
    }

    //*********  JSON  *********

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
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
                jsonObject.put("de", this.de);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("en", this.en);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("es", this.es);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("fr", this.fr);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("it", this.it);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("pt", this.pt);
            } catch (Exception e) {
            }
            try {
                jsonObject.put("zh", this.zh);
            } catch (Exception e) {
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
        return jsonObject;
    }

    public void parseJSON(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                this.id = jsonObject.getString("id");
            }
            if (jsonObject.has("hash")) {
                this.hash = jsonObject.getString("hash");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            if (jsonObject.has("de")) {
                this.de = jsonObject.getString("de");
            }
            if (jsonObject.has("en")) {
                this.en = jsonObject.getString("en");
            }
            if (jsonObject.has("es")) {
                this.es = jsonObject.getString("es");
            }
            if (jsonObject.has("fr")) {
                this.fr = jsonObject.getString("fr");
            }
            if (jsonObject.has("it")) {
                this.it = jsonObject.getString("it");
            }
            if (jsonObject.has("pt")) {
                this.pt = jsonObject.getString("pt");
            }
            if (jsonObject.has("zh")) {
                this.zh = jsonObject.getString("zh");
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("tag")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("tag"));//{tag:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("tag")) {
                    this.parseJSON(new JSONObject(json));//{id:1}
                }
            }
            //this.parseJSON(new JSONObject(json));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    //*********  Print  *********

    public void print() {
        Log.d("Tag", "Object");
        try {
            Log.d("id", this.id);
            Log.d("hash", this.hash);
            Log.d("en", this.en);
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
