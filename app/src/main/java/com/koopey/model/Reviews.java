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
public class Reviews implements Serializable , Comparator<Reviews> , Comparable<Reviews>
{
    private static final String LOG_HEADER = "REVIEWS";
    public static final String REVIEWS_FILE_NAME = "reviews.dat";
    private List<Review> reviews;

    public Reviews()
    {
        reviews = new ArrayList();
    }

    @Override
    public int compare(Reviews o1, Reviews o2) {
        //-1 not the same, 0 is same, 1 > is same but larger
        int result = -1;
        if (o1.size() < o2.size())        {
            result =  -1;
        }        else if (o1.size() > o2.size())        {
            result = 1;
        }        else        {
            //Sort both lists before compare
            o1.sort();
            o2.sort();
            //Check each tag in tags
            for (int i =0; i < o1.size();i++)            {
                if (!o1.contains(o2.get(i)))                {
                    result = -1;
                    break;
                }                else if (i == o2.size() - 1)                {
                    result = 0;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        /*reviews: [{
            id: 'a90780ab-8e5a-4b1d-ae54-c4a592ae5dbf',
            userId: 'a90780ab-8e5a-4b1d-ae54-c4a592ae5dbf',
            judgeId: 'c8ced750-ecc5-11e6-9003-852484db3118',
            value: 3,
            comment: 'my comment',
            createTimeStamp: 12345
         }]*/
        String str = "[";
        for (int i = 0; i < reviews.size();i++ )
        {
            reviews.get(i).toString();
            if(i != reviews.size() -1){
                str += ",";
            }
        }
        return str + "]";
    }

    public void add(Review t)
    {
        this.reviews.add(t);
    }

    public int compareTo(Reviews o){
        return this.compare(this,o);
    }

    public boolean contains(Review item){
        Review result = this.get(item);
        if(!result.id.equals(""))        {
            return true;
        } else {
            return false;
        }
    }

    public Review get(int i)
    {
        return this.reviews.get(i);
    }

    public Review get(Review review)    {
        Review result = null;
        for (int i =0; i < this.reviews.size();i++)        {
            Review cursor = this.reviews.get(i);
            if ( cursor.id.equals(review.id) || cursor.judgeId.equals(review.judgeId))            {
                result = cursor;
                break;
            }
        }
        return result;
    }

    public ArrayList<Review> getArrayList()    {
        return (ArrayList)this.reviews;//new ArrayList<Tag>( this.tags.toArray());
    }

    public int getStarAverage() {
        int denominator = 0;
        int numerator = 0;

        for (int i = 0; i < this.reviews.size(); i++) {
            if (this.reviews.get(i).type.equals("stars")) {
                denominator += this.reviews.get(i).value;
                numerator++;
            }
        }

        return (denominator == 0 ||  numerator == 0) ? 0 :Math.round( denominator / numerator) ;
    }

    public int getThumbAverage() {
        int denominator = 0;
        int numerator = 0;

        for (int i = 0; i < this.reviews.size(); i++) {
            if (this.reviews.get(i).type.equals("thumb")) {
                denominator += this.reviews.get(i).value;
                numerator++;
            }
        }

        return (denominator == 0 ||  numerator == 0) ? 0 :Math.round( denominator / numerator) ;
    }

    public List<Review> getList()
    {
        return this.reviews;
    }

    public int getNegative() {
        int negative = 0;
        for (int i = 0; i < this.reviews.size(); i++) {
            if (this.reviews.get(i).value == 0 && this.reviews.get(i).type.equals("thumbs")){
                negative++;
            }
        }
        return negative;
    }

    public int getPositive() {
        int positive = 0;
        for (int i = 0; i < this.reviews.size(); i++) {
            if (this.reviews.get(i).value == 100 && this.reviews.get(i).type.equals("thumbs") ){
                positive++;
            }
        }
        return positive;
    }

    public boolean isEmpty()
    {
        return this.size() == 0 ? true : false;
    }

    public void setList(List<Review> arr)
    {
        this.reviews = arr;
    }

    public int size()
    {
        return this.reviews.size();
    }

    public void sort(){
        Collections.sort(reviews);
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < this.reviews.size(); i++) {
                jsonArray.put(this.reviews.get(i).toJSONObject());

            }
        }catch (Exception ex){

        }
        return jsonArray;
    }













    public void parseJSON(JSONArray jsonArray)    {
               try
        {
           // JSONArray jsonArray = new JSONObject(json).getJSONArray("reviews");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Review review = new Review();
                review.parseJSON(jsonObject.toString());
                this.add(review);
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
                    //{reviews:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("reviews");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void print()    {
              try{
            Log.d("Reviews", "Object");
            Log.d("Reviews Size", String.valueOf(this.size()));
            Log.d("Reviews ",  this.toString() );
        } catch (Exception ex){Log.d( LOG_HEADER + ":ER", ex.getMessage());}
    }
}
