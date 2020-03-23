package me.minitrabajo.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Scott on 07/04/2017.
 */
public class MyTransactionOLD extends Transaction implements Serializable {

    public static final String MY_TRANSACTION_FILE_NAME = "mytransaction.dat";
    private static final String LOG_HEADER = "MY:TN";

    public boolean isValid()
    {
        //Note* userid is also passed in token so userId check is not necessary
        if (!this.name.equals("")
                && !this.type.equals("")
                && !this.currency.equals("")
                && this.quantity >= 0
                && this.itemValue >= 0
                && this.totalValue >= 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
 /*
    public JSONObject toJSONObject() {
      final String LOG_FUNCTION = LOG_HEADER + ":JSON:ER";
        JSONObject jsonObject = new JSONObject();
        try{
            //Strings
            if (!this.id.equals("")) {
                jsonObject.put("id", this.id);
            }
            if (!this.name.equals("")) {
                jsonObject.put("name", this.name);
            }
            if (!this.type.equals("")) {
                jsonObject.put("type", this.type);
            }
            if (!this.userId.equals("")) {
                jsonObject.put("currency", this.currency);
            }
            if (!this.userId.equals("")) {
                jsonObject.put("userId", this.userId);
            }
            //Doubles
            if (this.value != 0.0d) {
                jsonObject.put("value", this.value);
            }
            //Longs
            if (this.createTimeStamp != 0) {
                jsonObject.put("createTimeStamp", this.createTimeStamp);
            }
        }
        catch (Exception ex)
        {
            Log.v(LOG_FUNCTION, ex.getMessage());
        }
        ;
    }*/

    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }

    @Override
    public void print()
    {
        try{
            Log.v("MyTransaction", "Object");
            super.print();
        } catch (Exception ex){Log.v(LOG_HEADER + ":PT:ER", ex.getMessage());}
    }
}
