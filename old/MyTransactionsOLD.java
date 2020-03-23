package me.minitrabajo.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Scott on 07/04/2017.
 */
public class MyTransactions implements Serializable {

    private static final String LOG_HEADER = "MY:TRANS:";
    public static final String MY_TRANSACTIONS_FILE_NAME = "mytransactions.dat";
    public static final String TRANSACTIONS_FILE_NAME = "transactions.dat";
    protected List<MyTransaction> myTransactions;

    public MyTransactions() {
        myTransactions = new ArrayList<MyTransaction>(0);
    }

    public MyTransaction getMyTransaction(int index) {
        return myTransactions.get(index);
    }

    public MyTransaction getMyTransaction(String id) {
        MyTransaction result = null;
        for (int i = 0; i < myTransactions.size(); i++) {
            if (myTransactions.get(i).id.equals(id)) {
                result = myTransactions.get(i);
                break;
            }
        }
        return result;
    }

    public void add(MyTransaction t) {
        try {
            myTransactions.add(t);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void remove(MyTransaction t) {
        myTransactions.remove(t);
    }

    public int size() {
        return myTransactions.size();
    }

    public List<MyTransaction> getList() {
        return this.myTransactions;
    }

    public List<MyTransaction> getList(Date date) {
        List<MyTransaction> todaysTransactions = new ArrayList<MyTransaction>(0);
        for (int i = 0 ; i < this.myTransactions.size(); i++ ){
            Date transactionDate = this.myTransactions.get(i).getStartTimeStampAsDate();
            if (transactionDate.getYear() == date.getYear() && transactionDate.getMonth() == date.getMonth() && transactionDate.getDay() == date.getDay() ){
                todaysTransactions.add(this.myTransactions.get(i));
            }
        }
        return todaysTransactions;
    }

    protected void setList(List<MyTransaction> myTransactions) {
        this.myTransactions = myTransactions;
    }

    public boolean isEmpty() {
        return this.size() == 0 ? true : false;
    }

    public void parseJSON(String json) {
        final String LOG_FUNCTION = LOG_HEADER + ":PASS:JSON:STR";
        Log.v(LOG_FUNCTION, json);
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
                    jsonArray = jsonObject.getJSONArray("transactions");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.v(LOG_FUNCTION + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(JSONArray jsonArray) {
        final String LOG_FUNCTION = LOG_HEADER + ":PASS:JSON:OBJ";
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MyTransaction myTransaction = new MyTransaction();
                if (!jsonObject.toString().equals("{}")) {
                    myTransaction.parseJSON(jsonObject);
                    myTransaction.print();
                    this.add(myTransaction);
                }
            }
        } catch (Exception ex) {
            Log.v(LOG_FUNCTION + ":ER", ex.getMessage());
        }
    }

    public void print() {
        //No super.print() because MyTransactions does not extend class
        try {
            Log.v("MyTransactions", "Object");
            Log.v("MyTransactions Size", String.valueOf(this.size()));
            for (int i = 0; i < myTransactions.size(); i++) {
                myTransactions.get(i).print();
                if (i == 3) {
                    break;
                }
            }
        } catch (Exception ex) {
            Log.v(LOG_HEADER + ":PT:ER", ex.getMessage());
        }
    }

}
