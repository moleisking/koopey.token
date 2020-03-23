package com.koopey.model;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Scott on 15/10/2016.
 */
public class Messages implements Serializable, Comparator<Messages>, Comparable<Messages> {

    private static final String LOG_HEADER = "MESSAGES";
    public static final String MESSAGES_FILE_NAME = "messages.dat";
    //private transient Context context;
    private List<Message> messages;

    public Messages() {
        this.messages = new ArrayList();
    }

    public Messages(Message[] message) {
        this.messages = new ArrayList<Message>(1);
        for (int i = 0; i < message.length; i++) {
            this.messages.add(message[i]);
        }
    }

    public Messages(Messages messages) {
        this.messages = new ArrayList<Message>();
        for (int i = 0; i < messages.size(); i++) {
            this.messages.add(messages.get(i));
        }
    }

    @Override
    public int compare(Messages o1, Messages o2) {
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
    public int compareTo(Messages o) {
        return this.compare(this, o);
    }

    public void add(Message message) {
        if (!this.contains(message)) {
            this.messages.add(message);
        }
    }

    public void add(Messages messages) {
        for (int i = 0; i < messages.size(); i++) {
            this.add(messages.get(i)); //Checks for duplicates
        }
    }

    protected boolean contains(Message message) {
        boolean result = false;
        for (int i = 0; i < this.messages.size(); i++) {
            Message cursor = this.messages.get(i);
            if (message.equals(cursor) ) {
                result = true;
                break;
            }
        }
        return result;
    }

    public int countUnread() {
        int read = 0;
        for (int i = 0; i < this.messages.size(); i++) {
            if (!this.messages.get(i).read){
                read++;
            }
        }
        return read;
    }

    public int countUnsent() {
        int sent = 0;
        for (int i = 0; i < this.messages.size(); i++) {
            if (!this.messages.get(i).sent){
                sent++;
            }
        }
        return sent;
    }

    public Message get(int i) {
        return this.messages.get(i);
    }

    public List<Message> getMessageList() {
        return messages;
    }

    public void parseJSON(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Message message = new Message();
                message.parseJSON(jsonObject.toString());
                this.add(message);
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
                    //{messages:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("messages");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print() {
        try {
            Log.d("Messages", "Object");
            Log.d("Messages Size", String.valueOf(this.size()));
            for (int i = 0; i < this.messages.size(); i++) {
                this.messages.get(i).print();
                if (i == 3) {
                    break;
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void sort() {
        Collections.sort(messages);
    }



   /* public Message getLastConversationMessage(String otherUserId) {
        //NOTE: Backend Result return LIFO
        Message result = null;
        for (int i = 0; i < this.messages.size(); i++) {
            String fromId = this.messages.get(i).fromId;
            String toId = this.messages.get(i).toId;
            if (toId.equals(otherUserId) || fromId.equals(otherUserId)) {
                result = this.messages.get(i);
                break;
            }
        }
        return result;
    }*/

   /* public Message getFirstConversationMessage(String otherUserId) {
        //NOTE: Backend Result return LIFO
        Message result = null;
        for (int i = 0; i < this.messages.size(); i++) {
            String fromId = this.messages.get(i).fromId;
            String toId = this.messages.get(i).toId;
            if (toId.equals(otherUserId) || fromId.equals(otherUserId)) {
                result = this.messages.get(i);
            }
        }
        return result;
    }*/

    protected void setMessageList(List<Message> messages) {
        this.messages = messages;
    }

    public int size() {
        return messages.size();
    }

    public boolean isEmpty() {
        return this.size() == 0 ? true : false;
    }
}