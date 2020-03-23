package com.koopey.model;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Scott on 03/10/2016.
 */
public class Tags implements Serializable, Comparator<Tags>, Comparable<Tags> {
    private static final String LOG_HEADER = "TAGS";
    public static final String TAGS_FILE_NAME = "tags.dat";
    //private transient Context mContext;
    private List<Tag> tags;

    public Tags() {
        tags = new ArrayList();
    }

    @Override
    public String toString() {
        return this.toJSONArray().toString();
    }

    @Override
    public int compare(Tags o1, Tags o2) {
        //-1 not the same, 0 is same, 1 > is same but larger
        int result = -1;
        if (o1.size() < o2.size()) {
            result = -1;
        } else if (o1.size() > o2.size()) {
            result = 1;
        } else {
            //Sort both lists before compare
            o1.sort();
            o2.sort();
            //Check each tag in tags
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

    protected Tag get(int i) {
        return this.tags.get(i);
    }

    public int size() {
        return this.tags.size();
    }

    public void add(Tag t) {
        this.tags.add(t);
    }

    public List<Tag> getList() {
        return this.tags;
    }

    public ArrayList<Tag> getArrayList() {
        return (ArrayList) this.tags;//new ArrayList<Tag>( this.tags.toArray());
    }

    public void setTagList(List<Tag> arr) {
        this.tags = arr;
    }

    public boolean isEmpty() {
        return this.size() == 0 ? true : false;
    }

    public JSONArray toJSONArray() {
        //tags : [{id: 1, en:'',de:'',fr:'',it:''}]
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < this.tags.size(); i++) {
                jsonArray.put(this.tags.get(i).toJSONObject());
            }

        } catch (Exception ex) {
        }
        return jsonArray;
    }

    public void sort() {
        Collections.sort(tags);
    }

    public int compareTo(Tags o) {
        return this.compare(this, o);
    }

    public boolean contains(Tag item) {
        Tag result = this.findTag(item.id);
        if (!result.id.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public Tag findTag(String id) {
        Tag result = new Tag();
        for (int i = 0; i < this.tags.size(); i++) {
            if (tags.get(i).id.equals(id)) {
                result = tags.get(i);
                break;
            }
        }
        return result;
    }

    public void parseJSON(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Tag tag = new Tag();
                tag.parseJSON(jsonObject.toString());
                this.add(tag);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(String json) {
        JSONArray jsonArray;//= new JSONObject(json).getJSONArray("images");
        try {
            //Check JSON format, which could be [ or {
            if (json.length() >= 1) {
                if (json.substring(0, 1).equals("[")) {
                    //[] array format
                    jsonArray = new JSONArray(json);
                    this.parseJSON(jsonArray);
                } else if (json.substring(0, 1).equals("{")) {
                    //{tags:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("tags");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print() {
        try {
            Log.d("Tags", "Object");
            Log.d("Tags Size", String.valueOf(this.size()));
            for (int i = 0; i < tags.size(); i++) {
                tags.get(i).print();
                if (i == 3) {
                    break;
                }
            }
        } catch (Exception ex) {
        }
    }
}