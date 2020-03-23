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
public class Pieces implements Serializable , Comparator<Pieces> , Comparable<Pieces>
{
    private static final String LOG_HEADER = "PIECES";
    public static final String REVIEWS_FILE_NAME = "pieces.dat";
    private List<Piece> pieces;

    public Pieces()
    {
        pieces = new ArrayList();
    }

    public Piece get(int i)
    {
        return this.pieces.get(i);
    }

    public int size()
    {
        return this.pieces.size();
    }

    public void add(Piece t)
    {
        this.pieces.add(t);
    }

    public List<Piece> getList()
    {
        return this.pieces;
    }

    public ArrayList<Piece> getArrayList()
    {
        return (ArrayList)this.pieces;//new ArrayList<Tag>( this.tags.toArray());
    }

    public void setList(List<Piece> arr)
    {
        this.pieces = arr;
    }


    public boolean isEmpty()
    {
        return this.size() == 0 ? true : false;
    }

    @Override
    public String toString()
    {
        /*pieces: [{
            id: 'a90780ab-8e5a-4b1d-ae54-c4a592ae5dbf',
            userId: 'a90780ab-8e5a-4b1d-ae54-c4a592ae5dbf',
            judgeId: 'c8ced750-ecc5-11e6-9003-852484db3118',
            value: 3,
            comment: 'my comment',
            createTimeStamp: 12345
         }]*/
        String str = "[";
        for (int i = 0; i < pieces.size();i++ )
        {
            pieces.get(i).toString();
            if(i != pieces.size() -1){
                str += ",";
            }
        }
        return str + "]";
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < this.pieces.size(); i++) {
                jsonArray.put(this.pieces.get(i).toJSONObject());

            }
        }catch (Exception ex){

        }
        return jsonArray;
    }

    public void sort(){
         Collections.sort(pieces);
    }

    public int compareTo(Pieces o){
       return this.compare(this,o);
    }

    @Override
    public int compare(Pieces o1, Pieces o2) {
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

    public boolean contains(Piece item){
        Piece result = this.find(item);
        if(!result.id.equals(""))
        {
            return true;
        } else {
            return false;
        }
    }

    public Piece find(Piece piece)
    {
        Piece result = null;
        for (int i =0; i < this.pieces.size();i++)
        {
            if ( this.pieces.get(i).id.equals(piece.id))
            {
                result = this.pieces.get(i);
                break;
            }
        }
        return result;
    }



    public void parseJSON(JSONArray jsonArray)    {
               try
        {
           // JSONArray jsonArray = new JSONObject(json).getJSONArray("pieces");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Piece piece = new Piece();
                piece.parseJSON(jsonObject.toString());
                this.add(piece);
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
                    //{pieces:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("pieces");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print()    {
              try{
            Log.d("Pieces", "Object");
            Log.d("Pieces Size", String.valueOf(this.size()));
            Log.d("Pieces ",  this.toString() );
        } catch (Exception ex){Log.d( LOG_HEADER + ":ER", ex.getMessage());}
    }
}
