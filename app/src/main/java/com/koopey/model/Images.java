package com.koopey.model;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.koopey.R;

/**
 * Created by Scott on 22/03/2017.
 */
public class Images implements Serializable, Comparator<Images>, Comparable<Images> {
    private static final String LOG_HEADER = "IMAGES";
    public static final String IMAGES_FILE_NAME = "images.dat";
    public static final int IMAGES_COUNT_MAX = 4;
    private List<Image> images;
    public String hash = "";

    public Images()    {               images = new ArrayList<Image>(0);    }

    @Override
    public int compare(Images o1, Images o2) {
        //-1 not the same, 0 is same, 1 > is same but larger
        int result = -1;
        if (o1.size() < o2.size()) {
            result = -1;
        } else if (o1.size() > o2.size()) {
            result = 1;
        } else {
            o1.sort();
            o2.sort();
            for (int i = 0; i < o1.size(); i++) {
                if (!o1.contains(o2.get(i))) {
                    result = -1;
                    break;
                } else if (i == o2.size() - 1) {
                    result = 0;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return this.toJSONArray().toString();
    }

    public int compareTo(Images o) {
        return this.compare(this, o);
    }

    public boolean contains(Image image) {
        boolean found = false;
        for (int i = 0; i < this.images.size(); i++) {
            Image currentImage = this.images.get(i);
            if (currentImage.id.equals(image.id)) {
                found = true;
                break;
            }
        }
        return found;
    }

    /*********  Create *********/

    public void add(Image image) {
        if(!this.contains(image)) {
            this.images.add(image);
        } else {
            this.set(image);
        }
    }

    /*********  Read *********/

    public List<Image> get() {
        return this.images;
    }

    public Image get(int i) {
        return this.images.get(i);
    }

    public Image getFirstImage() {
        if(this.size() > 0) {
            return this.images.get(0);
        }
        else
        {
            //R.string.default_user_image
            return null;
        }
    }

    public boolean isEmpty() {
        return this.size() == 0 ? true : false;
    }

    public int size() {
        return this.images.size();
    }

   /* public void setPrimaryImageUri(Image i) {
        if(this.size() > 0) {
           this.images.set(0, i);
        }
        else
        {
            this.images.add(i);
        }
    }*/

    /*********  Update *********/

    public void set(Image image){
        if (!this.contains(image)){
            for(int i = 0; i < this.images.size(); i++ ){
                Image currentImage = this.images.get(i);
                if (currentImage.id.equals(currentImage.id)){
                    this.images.set(i,image);
                }
            }
        }
    }

    public void set(List<Image> images) {
        this.images = images;
    }

    public void sort() {
        Collections.sort(this.images);
    }

    /*********  Delete *********/

    public void remove(Image image){
        for(int i = 0; i < this.images.size(); i++ ){
            Image currentImage = this.images.get(i);
            if (currentImage.id.equals(image.id) ){
                this.images.remove(i);
            }
        }
    }

    public void setPrimaryImageUri(Bitmap bm) {

        if(this.size() > 0) {
            this.images.get(0).setUri(bm);
        }        else        {
            Image image =  new Image();
            image.setUri(bm);
            this.images.add(image );
        }
    }



    /*public void add(Image i) {
        this.images.add(i);
    }*/

    /*********  JSON *********/

    public void parseJSON(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Image image = new Image();
               // if(!jsonObject.toString().equals("{}")){
                    image.parseJSON(jsonObject);
                    this.add(image);
              //  }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(String json) {
        JSONArray jsonArray ;//= new JSONObject(json).getJSONArray("images");
        try {
            //Check JSON format, which could be [ or {
            if(json.length() >= 1){
                if(json.substring(0,1).equals("[")){
                    //[] array format
                    jsonArray = new JSONArray(json);
                    this.parseJSON(jsonArray);
                }
                else if(json.substring(0,1).equals("{")){
                    //{images:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("images");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public JSONArray toJSONArray() {
        //String reply = "[";
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < this.images.size(); i++) {
                jsonArray.put(this.images.get(i).toJSONObject());
            }
        }        catch (Exception ex)        {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }

        return jsonArray;
    }

    /*********  Print *********/

    public void print() {
        try {
            Log.d("Images", "Object");
            Log.d("Images Size", String.valueOf(this.size()));
            Log.d("Images ", this.toString());
        } catch (Exception ex) {         }
    }
}
