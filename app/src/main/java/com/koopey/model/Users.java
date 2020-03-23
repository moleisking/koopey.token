package com.koopey.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class Users implements Serializable, Comparator<Users>, Comparable<Users> {

    private static final String LOG_HEADER = "USERS";
    public static final String USERS_FILE_NAME = "users.dat";
    public static final String SEARCH_RESULTS_FILE_NAME = "user_search_results.dat";
    private List<User> users;
    //private transient Context context;
    public String hash = "";

    public Users() {
        //this.context= context;
        users = new ArrayList<User>(0);
    }

    public Users(User[] user) {
        this.users = new ArrayList<User>(2);
        for (int i = 0; i < user.length; i++) {
            this.users.add(user[i]);
        }
    }

    public Users(Users users) {
        this.users = new ArrayList<User>();
        for (int i = 0; i < users.size(); i++) {
            this.users.add(users.get(i));
        }
    }

    @Override
    public int compare(Users o1, Users o2) {
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

    @Override
    public int compareTo(Users o) {
        return this.compare(this, o);
    }

    public void add(User user) {
        if (!this.contains(user)) {
            users.add(user);
        }
    }

    public void addBuyer(User user) {
        if (!this.contains(user)) {
            user.type = "buyer";
            users.add(user);
        }
    }

    public void addSeller(User user) {
        if (!this.contains(user)) {
            user.type = "seller";
            users.add(user);
        }
    }

    public void add(Users users) {
        for (int i = 0; i < users.size(); i++) {
            this.add(users.get(i)); //Checks for duplicates
        }
    }

    public int count(List<User> users, String type) {
        if (users != null && (users.size() > 0)) {
            int counter = 0;
            for (int i = 0; i <= users.size(); i++) {
                if (users.get(i) != null && users.get(i).type == type) {
                    counter++;
                }
            }
            return counter;
        } else {
            return 0;
        }
    }

    public int countBuyer(List<User> users) {
        return count( users, "buyer");
    }

    public int countSeller(List<User> users) {
        return count( users, "seller");
    }

    public boolean contains(User user) {
        boolean result = false;
        for (int i = 0; i < this.users.size(); i++) {
            User cursor = this.users.get(i);
            if (user.equals(cursor) ) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean contains(Users users) {
        boolean result = false;
        int counter = 0;
        for (int i = 0; i < this.users.size(); i++) {
            for (int j = 0; j < users.size(); j++) {
                if (this.users.get(i).id.equals(users.get(j).id)
                        || this.users.get(i).name.equals(users.get(j).name)
                        || this.users.get(i).email.equals(users.get(j).email)) {
                    counter++;
                    if (counter == users.size()) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    public static boolean equals(Users usersA, Users usersB) {
        if (usersA == null || usersB == null) {
            return false;
        } else if (usersA.size() != usersB.size()) {
            return false;
        } else if (usersA.size() == usersB.size()) {
            int counter = 0;
            for (int i = 0; i < usersA.size(); i++) {
                User userA = usersA.get(i);
                for (int j = 0; j < usersB.size(); j++) {
                    User userB = usersB.get(j);
                    if (userA.id.equals(userB.id) ) {
                       counter++;
                       break;
                    }
                }
            }
            //Check counter results
            if (counter == usersA.size() && counter == usersB.size()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public User get(int index) {
        return users.get(index);
    }

    public User get(String id) {
        User result = null;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).id.equals(id)) {
                result = users.get(i);
                break;
            }
        }
        return result;
    }

    public User get(User user) {
        User result = null;

        for (int i = 0; i < this.users.size(); i++) {
            if (this.users.get(i).id.equals(user.id)
                    || this.users.get(i).name.equals(user.name)
                    || this.users.get(i).email.equals(user.email)) {
                result = this.users.get(i);
                break;
            }
        }

        return result;
    }

    public User get(String name, String email) {
        User result = null;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).name.equals(name) || users.get(i).email.equals(email)) {
                result = users.get(i);
                break;
            }
        }

        return result;
    }

    public List<User> getList() {
        return users;
    }

    public boolean isEmpty() {
        return this.size() == 0 ? true : false;
    }

    public void parseJSON(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                User user = new User();
                user.parseJSON(jsonObject.toString());
                this.add(user);
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
                    //{users:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("users");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

     /*public void parseJSON(String json) {
        try {
            JSONArray usersJson = new JSONObject(json).getJSONArray("users");
            for (int i = 0; i < usersJson.length(); i++) {
                JSONObject userJSON = usersJson.getJSONObject(i);
                User user = new User();
                user.parseJSON("{user:" + userJSON.toString() + "}");
                this.add(user);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }*/

    public void print() {
        try {
            Log.d("Users", "Object");
            Log.d("Users Size", String.valueOf(this.size()));
            for (int i = 0; i < users.size(); i++) {
                Log.d("User", users.get(i).id + ":" + users.get(i).name);
                if (i == 3) {
                    break;
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void remove(User u) {
        users.remove(u);
    }

    public void set(User user){
        if (!this.contains(user)){
            for(int i = 0; i < this.users.size(); i++ ){
                User currentUser = this.users.get(i);
                if (currentUser.id.equals(currentUser.id)){
                    this.users.set(i,user);
                }
            }
        }
    }

    protected void setList(List<User> users) {
        this.users = users;
    }

    public int size() {
        return users.size();
    }

    public void sort() {
        Collections.sort(users);
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < this.users.size(); i++) {
                jsonArray.put(this.users.get(i).toJSONObject());

            }
        } catch (Exception ex) {

        }
        return jsonArray;
    }
}

