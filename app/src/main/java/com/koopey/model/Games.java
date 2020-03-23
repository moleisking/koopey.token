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
public class Games implements Serializable , Comparator<Games> , Comparable<Games>
{
    private static final String LOG_HEADER = "GAMES";
    public static final String GAMES_FILE_NAME = "games.dat";
    private List<Game> games;

    public Games()
    {
        games = new ArrayList();
    }

    public Game get(int i)
    {
        return this.games.get(i);
    }

    public int size()
    {
        return this.games.size();
    }

    public void add(Game t)
    {
        this.games.add(t);
    }

    public List<Game> getList()
    {
        return this.games;
    }

    public ArrayList<Game> getArrayList()
    {
        return (ArrayList)this.games;//new ArrayList<Tag>( this.tags.toArray());
    }

    public void setList(List<Game> arr)
    {
        this.games = arr;
    }


    public boolean isEmpty()
    {
        return this.size() == 0 ? true : false;
    }

    @Override
    public String toString()
    {
        /*games: [{
            id: 'a90780ab-8e5a-4b1d-ae54-c4a592ae5dbf',
            userId: 'a90780ab-8e5a-4b1d-ae54-c4a592ae5dbf',
            judgeId: 'c8ced750-ecc5-11e6-9003-852484db3118',
            value: 3,
            comment: 'my comment',
            createTimeStamp: 12345
         }]*/
        String str = "[";
        for (int i = 0; i < games.size();i++ )
        {
            games.get(i).toString();
            if(i != games.size() -1){
                str += ",";
            }
        }
        return str + "]";
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < this.games.size(); i++) {
                jsonArray.put(this.games.get(i).toJSONObject());

            }
        }catch (Exception ex){

        }
        return jsonArray;
    }

    public void sort(){
         Collections.sort(games);
    }

    public int compareTo(Games o){
       return this.compare(this,o);
    }

    @Override
    public int compare(Games o1, Games o2) {
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

    public boolean contains(Game item){
        Game result = this.find(item);
        if(!result.id.equals(""))
        {
            return true;
        } else {
            return false;
        }
    }

    public Game find(Game game)
    {
        Game result = null;
        for (int i =0; i < this.games.size();i++)
        {
            if ( this.games.get(i).id.equals(game.id))
            {
                result = this.games.get(i);
                break;
            }
        }
        return result;
    }

    public void parseJSON(JSONArray jsonArray)    {
               try
        {
           // JSONArray jsonArray = new JSONObject(json).getJSONArray("games");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Game game = new Game();
                game.parseJSON(jsonObject.toString());
                this.add(game);
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
                    //{games:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("games");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print()    {
              try{
            Log.d("Games", "Object");
            Log.d("Games Size", String.valueOf(this.size()));
            Log.d("Games ",  this.toString() );
        } catch (Exception ex){Log.d( LOG_HEADER + ":ER", ex.getMessage());}
    }
}
