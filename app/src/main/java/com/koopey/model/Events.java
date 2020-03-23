package com.koopey.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Scott on 19/06/2018.
 */

public class Events  implements Serializable {
    private static final String LOG_HEADER = "EVENTS";
    public static final String EVENTS_FILE_NAME = "events.dat";
    private List<Event> events;

    public Events() {
        events = new ArrayList<Event>(0);
    }

    @Override
    public String toString() {
        JSONArray json = new JSONArray();
        if (this.events.size() > 0) {
            for (int i = 0; i < events.size(); i++) {
                json.put(events.get(i).toString());
            }
        }
        return json.toString();
    }

     /* Array */

    public void add(Event event) {
        if (event != null && !this.contains(event)) {
            this.events.add(event);
        }
    }

    public void add(Events events) {
        for (int i = 0; i < events.size(); i++) {
            this.add(events.get(i)); //Checks for duplicates
        }
    }

    protected boolean contains(Event event) {
        boolean result = false;
        for (int i = 0; i < this.events.size(); i++) {
            Event cursor = this.events.get(i);
            if (event.equals(cursor) ) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void set(Event event) {
        try {
            for (int i = 0; i < events.size(); i++) {
                if(events.get(i).id.equals(event.id)){
                    events.set(i,event);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    public Event get(int index) {
        return events.get(index);
    }

    public Events getEventsOfDate(Date start) {
        Events todayTransactions = new Events();
        for (int i = 0; i < this.events.size(); i++) {
            Date transactionDate = this.events.get(i).getStartTimeStampAsDate();
            if (transactionDate.getYear() == start.getYear()
                    && transactionDate.getMonth() == start.getMonth()
                    && transactionDate.getDay() == start.getDay()) {
                todayTransactions.add(this.events.get(i));
            }
        }
        return todayTransactions;
    }

    public Events getTransactionsBetweenDates(Date start, Date end) {
        Events todayTransactions = new Events();
        for (int i = 0; i < this.events.size(); i++) {
            Date transactionDate = this.events.get(i).getStartTimeStampAsDate();
            if (transactionDate.getYear() >= start.getYear()
                    && transactionDate.getMonth() >= start.getMonth()
                    && transactionDate.getDay() >= start.getDay()
                    && transactionDate.getYear() <= end.getYear()
                    && transactionDate.getMonth() <= end.getMonth()
                    && transactionDate.getDay() <= end.getDay()) {
                todayTransactions.add(this.events.get(i));
            }
        }
        return todayTransactions;
    }

    public Events getTransactionsFromDate(Date start) {
        Events todayTransactions = new Events();
        for (int i = 0; i < this.events.size(); i++) {
            Date transactionDate = this.events.get(i).getStartTimeStampAsDate();
            if (transactionDate.getYear() >= start.getYear()
                    && transactionDate.getMonth() >= start.getMonth()
                    && transactionDate.getDay() >= start.getDay()) {
                todayTransactions.add(this.events.get(i));
            }
        }
        return todayTransactions;
    }

    public List<Event> getList() {
        return events;
    }



    public void remove(Event event) {
        events.remove(event);
    }

    protected void setList(List<Event> events) {
        this.events = events;
    }

    public int size() {
        return events.size();
    }

    public boolean isEmpty() {
        if (this.size() == 0) {
            return true;
        } else {
            return false;
        }
    }


    /*********  JSON *********/

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < this.events.size(); i++) {
                jsonArray.put(this.events.get(i).toJSONObject());
            }
        } catch (Exception ex) {

        }
        return jsonArray;
    }

    public void parseJSON(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Event event = new Event();
                event.parseJSON(jsonObject.toString());
                this.add(event);
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
                    //{transactions:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("events");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print() {
        try {
            Log.d("Events", "Object");
            Log.d("Events Size", String.valueOf(this.size()));
            for (int i = 0; i < events.size(); i++) {
                events.get(i).print();
                if (i == 3) {
                    break;
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
