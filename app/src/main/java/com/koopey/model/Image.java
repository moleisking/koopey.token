package com.koopey.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.UUID;

import com.koopey.view.RoundImage;

/**
 * Created by Scott on 22/03/2017.
 */
public class Image implements Serializable, Comparator<Image>, Comparable<Image> {

    private static final String LOG_HEADER = "IMAGE";
    public String id = UUID.randomUUID().toString();
    public String hash = "";
    public String uri = "";
    public String type = "png";
    public int width = 512;
    public int height = 512;
    public long createTimeStamp = System.currentTimeMillis();
    public long readTimeStamp = 0;
    public long updateTimeStamp = 0;
    public long deleteTimeStamp = 0;

    public Image() {
    }

    @Override
    public int compare(Image o1, Image o2) {
        if (o1.hashCode() < o2.hashCode()) {
            return -1;
        } else if (o1.hashCode() > o2.hashCode()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Image o) {
        return compare(this, o);
    }

    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }

    public boolean isEmpty() {
        if (this.uri.equals("") || this.id.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public void setUri(Bitmap bitmap) {
        //Resize image to 512 * 512
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        scaleBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        this.uri = "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public Bitmap getBitmap() {
        //gets bitmap from encoded data of Uri string
        byte[] arr = Base64.decode(this.uri.split(",", 2)[1], Base64.DEFAULT);
        // ByteArrayInputStream inputStream = new ByteArrayInputStream (arr);
        return BitmapFactory.decodeByteArray(arr, 0, arr.length);
    }

    public Uri getUri() {
        return Uri.parse(this.uri);
    }

    public String getSmallUri() {
        Bitmap smallImage = Bitmap.createScaledBitmap(this.getBitmap(), 256, 256, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }


    public RoundImage getRoundBitmap() {
        Bitmap temp = Bitmap.createScaledBitmap(this.getBitmap(), 100, 100, true);
        RoundImage roundedImage = new RoundImage(temp);
        return roundedImage;
    }

    public int getBitmapFileSize(Bitmap bitmap) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);
        byte[] ba = bao.toByteArray();
        //returns size in bytes
        Log.d(LOG_HEADER + ":SZ", Integer.toString(ba.length));
        return ba.length;
    }

    public String getUriMD5() {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(this.uri.getBytes());
            result = messageDigest.toString();
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":MD5", ex.getMessage());
        }
        return result;
    }

    public String getPeakUri() {
        return uri.length() > 100 ? uri.substring(0, 99) : uri;
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("image")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("image"));//{user:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("image")) {
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
            if (jsonObject.has("id")) {
                this.id = jsonObject.getString("id");
            }
            if (jsonObject.has("hash")) {
                this.hash = jsonObject.getString("hash");
            }
            if (jsonObject.has("uri")) {
                this.uri = jsonObject.getString("uri");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            if (jsonObject.has("width")) {
                this.width = jsonObject.getInt("width");
            }
            if (jsonObject.has("height")) {
                this.height = jsonObject.getInt("height");
            }
            if (jsonObject.has("createTimeStamp")) {
                this.createTimeStamp = jsonObject.getInt("createTimeStamp");
            }
            if (jsonObject.has("readTimeStamp")) {
                this.readTimeStamp = jsonObject.getInt("readTimeStamp");
            }
            if (jsonObject.has("update")) {
                this.updateTimeStamp = jsonObject.getInt("update");
            }
            if (jsonObject.has("deleteTimeStamp")) {
                this.deleteTimeStamp = jsonObject.getInt("deleteTimeStamp");
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            //Integers
            jsonObject.put("width", this.width);
            jsonObject.put("height", this.height);
            //Longs
            if (this.createTimeStamp != 0) {
                jsonObject.put("createTimeStamp", this.createTimeStamp);
            }
            if (this.readTimeStamp != 0) {
                jsonObject.put("readTimeStamp", this.readTimeStamp);
            }
            if (this.updateTimeStamp != 0) {
                jsonObject.put("createTimeStamp", this.updateTimeStamp);
            }
            if (this.deleteTimeStamp != 0) {
                jsonObject.put("deleteTimeStamp", this.deleteTimeStamp);
            }
            //Strings
            if (!this.id.equals("")) {
                jsonObject.put("id", this.id);
            }
            try {
                if (!this.hash.equals("")) {
                    jsonObject.put("hash", this.hash);
                }
            } catch (Exception e) {
            }
            if (!this.uri.equals("")) {
                jsonObject.put("uri", this.uri);
            }
            if (!this.type.equals("")) {
                jsonObject.put("type", this.type);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }

        return jsonObject;
    }

    public void print() {
        Log.d("Image", "Object");
        try {
            Log.d("id", this.id);
            Log.d("hash", this.hash);
            Log.d("uri", this.getPeakUri());
            Log.d("type", this.type);
            Log.d("height", Integer.toString(this.height));
            Log.d("width", Integer.toString(this.width));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
