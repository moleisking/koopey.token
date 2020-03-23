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
public class Scores implements Serializable , Comparator<Scores> , Comparable<Scores>
{
    private static final String LOG_HEADER = "SCORES";
    public static final String SCORES_FILE_NAME = "scores.dat";
    private List<Score> scores;

    public Scores()
    {
        scores = new ArrayList();
    }

    public Score get(int i)
    {
        return this.scores.get(i);
    }

    public int size()
    {
        return this.scores.size();
    }

    public void add(Score t)
    {
        this.scores.add(t);
    }

    public List<Score> getList()
    {
        return this.scores;
    }

    public ArrayList<Score> getArrayList()
    {
        return (ArrayList)this.scores;//new ArrayList<Tag>( this.tags.toArray());
    }

    public void setList(List<Score> arr)
    {
        this.scores = arr;
    }


    public boolean isEmpty()
    {
        return this.size() == 0 ? true : false;
    }

    @Override
    public String toString()
    {
        /*scores: [{
            id: 'a90780ab-8e5a-4b1d-ae54-c4a592ae5dbf',
            userId: 'a90780ab-8e5a-4b1d-ae54-c4a592ae5dbf',
            judgeId: 'c8ced750-ecc5-11e6-9003-852484db3118',
            value: 3,
            comment: 'my comment',
            createTimeStamp: 12345
         }]*/
        String str = "[";
        for (int i = 0; i < scores.size();i++ )
        {
            scores.get(i).toString();
            if(i != scores.size() -1){
                str += ",";
            }
        }
        return str + "]";
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < this.scores.size(); i++) {
                jsonArray.put(this.scores.get(i).toJSONObject());

            }
        }catch (Exception ex){

        }
        return jsonArray;
    }

    public void sort(){
         Collections.sort(scores);
    }

    public int compareTo(Scores o){
       return this.compare(this,o);
    }

    @Override
    public int compare(Scores o1, Scores o2) {
        //-1 not the same, 0 is same, 1 > is same but larger
        int result = -1;
        if (o1.size() < o2.size())
        {
            result =  -1;
        }
        else if (o1.size() > o2.size())
        {
            result = 1;
        }
        else
        {
            //Sort both lists before compare
            o1.sort();
            o2.sort();
            //Check each tag in tags
            for (int i =0; i < o1.size();i++)
            {
                if (!o1.contains(o2.get(i)))
                {
                    result = -1;
                    break;
                }
                else if (i == o2.size() - 1)
                {
                    result = 0;
                    break;
                }
            }
        }
        return result;
    }

    public boolean contains(Score item){
        Score result = this.find(item);
        if(!result.id.equals(""))
        {
            return true;
        } else {
            return false;
        }
    }

    public Score find(Score score)
    {
        Score result = null;
        for (int i =0; i < this.scores.size();i++)
        {
            if ( this.scores.get(i).id.equals(score.id) )
            {
                result = this.scores.get(i);
                break;
            }
        }
        return result;
    }


    public void parseJSON(JSONArray jsonArray)    {
               try
        {
           // JSONArray jsonArray = new JSONObject(json).getJSONArray("scores");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Score score = new Score();
                score.parseJSON(jsonObject.toString());
                this.add(score);
            }
        }
        catch (Exception ex)
        {
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
                    //{scores:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("scores");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print()    {
              try{
            Log.d("Scores", "Object");
            Log.d("Scores Size", String.valueOf(this.size()));
            Log.d("Scores ",  this.toString() );
        } catch (Exception ex){Log.d( LOG_HEADER + ":ER", ex.getMessage());}
    }
}
