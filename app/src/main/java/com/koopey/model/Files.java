package com.koopey.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Scott on 09/11/2017.
 */

public class Files implements Serializable, Comparator<Files>, Comparable<Files> {
    private static final String LOG_HEADER = "FILES";
    public static final String FILES_NAME = "files.dat";
    private List<File> files;
    public String hash = "";

    public Files() {
        files = new ArrayList();
    }

    @Override
    public int compare(Files o1, Files o2) {
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
        //fees: [{}]
        String str = "[";
        for (int i = 0; i < files.size(); i++) {
            files.get(i).toString();
            if (i != files.size() - 1) {
                str += ",";
            }
        }
        return str + "]";
    }

    public int compareTo(Files o) {
        return this.compare(this, o);
    }

    public boolean contains(File file) {
        boolean found = false;
        for (int i = 0; i < this.files.size(); i++) {
            File currentFile = this.files.get(i);
            if (currentFile.id.equals(file.id)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public boolean containsExcept(File file) {
        boolean found = false;
        for (int i = 0; i < this.files.size(); i++) {
            File currentFile = this.files.get(i);
            if (!currentFile.id.equals(file.id) && currentFile.hash.equals(file.hash)) {
                found = true;
                break;
            }
        }
        return found;
    }

    /*********  Create *********/

    public void add(File file) {
        if(!this.contains(file)) {
            this.files.add(file);
        } else {
            this.set(file);
        }
    }

    /*********  Read *********/

    public List<File> get() {
        return this.files;
    }

    public File get(int i) {
        return this.files.get(i);
    }

    public File get(String id) {
        File result = null;
        for (int i = 0; i < this.files.size(); i++) {
            if (this.files.get(i).id.equals(id)) {
                result = this.files.get(i);
                break;
            }
        }
        return result;
    }

    public File get(File file) {
        File result = null;
        for (int i = 0; i < this.files.size(); i++) {
            if (this.files.get(i).id.equals(file.id) ) {
                result = this.files.get(i);
                break;
            }
        }
        return result;
    }

    public ArrayList<File> getArrayList() {
        return (ArrayList) this.files;//new ArrayList<Tag>( this.tags.toArray());
    }

    public boolean isEmpty() {
        return this.size() == 0 ? true : false;
    }

    public int size() {
        return this.files.size();
    }

    /*********  Update *********/

    public void set(File file){
        if (!this.containsExcept(file)){
            for(int i = 0; i < this.files.size(); i++ ){
                File currentFile = this.files.get(i);
                if (currentFile.id.equals(file.id) ){
                    this.files.set(i,file);
                }
            }
        }
    }

    public void setList(List<File> files) {
        this.files = files;
    }

    public void sort() {
        Collections.sort(files);
    }

    /*********  Delete *********/

    public void remove(File file){
        for(int i = 0; i < this.files.size(); i++ ){
            File currentFile = this.files.get(i);
            if (currentFile.id.equals(file.id) ){
                this.files.remove(i);
            }
        }
    }

    /*********  JSON *********/

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < this.files.size(); i++) {
                jsonArray.put(this.files.get(i).toJSONObject());
            }
        } catch (Exception ex) {
        }
        return jsonArray;
    }

    public void parseJSON(JSONArray jsonArray) {
        try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                File file = new File();
                file.parseJSON(jsonObject.toString());
                this.add(file);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(String json) {
        JSONArray jsonArray;
        try {
            //Check JSON format, which could be [ or {
            if (json.length() >= 1) {
                if (json.substring(0, 1).equals("[")) {
                    //[] array format
                    jsonArray = new JSONArray(json);
                    this.parseJSON(jsonArray);
                } else if (json.substring(0, 1).equals("{")) {
                    //{files:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("files");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    /*********  Print *********/

    public void print() {
                try {
            Log.d("Files", "Object");
            Log.d("Files Size", String.valueOf(this.size()));
            Log.d("Files ", this.toString());
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
