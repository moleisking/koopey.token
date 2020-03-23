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
 * Created by Scott on 10/08/2017.
 */

public class Wallets implements Serializable , Comparator<Wallets>, Comparable<Wallets> {
    private static final String LOG_HEADER = "WALS";
    public static final String WALLETS_FILE_NAME = "wallets.dat";
    private List<Wallet> wallets;

    public Wallets()
    {
        wallets = new ArrayList();
    }

    //*********  Override *********

    @Override
    public int compare(Wallets o1, Wallets o2) {
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

    @Override
    public String toString()
    {
        /*wallets: [{
            id: 'a90780ab-8e5a-4b1d-ae54-c4a592ae5dbf',
            value: 3,
            createTimeStamp: 12345
         }]*/
        String str = "[";
        for (int i = 0; i < wallets.size();i++ )
        {
            wallets.get(i).toString();
            if(i != wallets.size() -1){
                str += ",";
            }
        }
        return str + "]";
    }

    public int compareTo(Wallets o){
        return this.compare(this,o);
    }


    public boolean contains(Wallet wallet){
        boolean result = false;
        for (int i = 0; i < this.wallets.size(); i++) {
            Wallet cursor = this.wallets.get(i);
            if (wallet.equals(cursor) ) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean contains(String currency) {
        if(this.get(currency) != null){
            return true;
        }else {
            return false;
        }
    }

    /*********  Create *********/

    public void add(Wallet wallet)
    {
        if (!this.wallets.contains(wallet)) {
            this.wallets.add(wallet);
        }
    }

    public void add(Wallets wallets) {
        for (int i = 0; i < wallets.size(); i++) {
            Wallet cursor = wallets.get(i);
            this.add(cursor); //Checks for duplicates
        }
    }

    //*********  Read *********

    public List<Wallet> get()
    {
        return this.wallets;
    }

    public Wallet get(int i)
    {
        return this.wallets.get(i);
    }

    public Wallet get(String currency)
    {
        for (int i =0; i < this.wallets.size();i++)
        {
            if ( this.wallets.get(i).currency.equals(currency) && this.wallets.get(i).type.equals("primary"))
            {
                return this.wallets.get(i);
            }
        }
        return null;
    }

    public Wallet get(Wallet wallet)
    {
        Wallet result = null;
        for (int i =0; i < this.wallets.size();i++)
        {
            if ( this.wallets.get(i).id.equals(wallet.id))
            {
                result = this.wallets.get(i);
                break;
            }
        }
        return result;
    }

    public Wallet getTokoWallet()
    {
        return this.get("tok");
    }

    public Wallet getBitcoinWallet()
    {
        return this.get("btc");
    }

    public Wallet getEthereumWallet()
    {
        return this.get("eth");
    }

    public Wallet getIBANWallet()
    {
        for(int x = 0; x < this.wallets.size(); x++ ){
            if ((this.wallets.get(x).currency.equals("usd")
                    || this.wallets.get(x).currency.equals("gbp")
                    || this.wallets.get(x).currency.equals("eur")
                    || this.wallets.get(x).currency.equals("rsa"))
                    && this.wallets.get(x).type.equals("primary")){
                return this.wallets.get(x);
            }
        }
        return null;
    }
    public Wallets getWalletsExceptLocal() {
        Wallets w = new Wallets();
        for(int x = 0; x < this.wallets.size(); x++ ){
            if(!this.wallets.get(x).currency.equals("tok")) {
                w.add(this.wallets.get(x));
            }
        }
        return w;
    }
    public ArrayList<Review> getArrayList()    {
        return (ArrayList)this.wallets;//new ArrayList<Tag>( this.tags.toArray());
    }

    public int size()
    {
        return this.wallets.size();
    }

    /*********  Update *********/

    public void set(Wallet wallet){
        if (!this.contains(wallet)){
            for(int i = 0; i < this.wallets.size(); i++ ){
                Wallet currentWallet = this.wallets.get(i);
                if (currentWallet.currency.equals(wallet.currency)){
                    this.wallets.set(i,wallet);
                }
            }
        }
    }

    public void setList(List<Wallet> arr)
    {
        this.wallets = arr;
    }


    public boolean isEmpty()
    {
        return this.size() == 0 ? true : false;
    }



    public void sort(){
        Collections.sort(wallets);
    }

    /*********  Delete *********/

    public void remove(Wallet wallet){
        for(int i = 0; i < this.wallets.size(); i++ ){
            Wallet currentWallet = this.wallets.get(i);
            if (currentWallet.id.equals(wallet.id) ){
                this.wallets.remove(i);
            }
        }
    }

    public void removeCurrency(String currency){
        for(int i = 0; i < this.wallets.size(); i++ ){
            Wallet currentWallet = this.wallets.get(i);
            if (currentWallet.currency.equals(currency) ){
                this.wallets.remove(i);
            }
        }
    }

    public void removeBitcoin(){
        this.removeCurrency("btc");
    }

    public void removeEthereum(){
        this.removeCurrency("eth");
    }

    public void removeLocal(){
        this.removeCurrency("tok");
    }

    /*********  JSON *********/

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < this.wallets.size(); i++) {
                jsonArray.put(this.wallets.get(i).toJSONObject());

            }
        }catch (Exception ex){}
        return jsonArray;
    }

    public void parseJSON(JSONArray jsonArray)    {
        try        {
            // JSONArray jsonArray = new JSONObject(json).getJSONArray("reviews");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Wallet wallet = new Wallet();
                wallet.parseJSON(jsonObject.toString());
                this.add(wallet);
            }
        }        catch (Exception ex)        {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(String json) {
        JSONArray jsonArray ;
        try {
            //Check JSON format, which could be [ or {
            if(json.length() >= 1){
                if(json.substring(0,1).equals("[")){
                    //[] array format
                    jsonArray = new JSONArray(json);
                    this.parseJSON(jsonArray);
                }
                else if(json.substring(0,1).equals("{")){
                    //{wallets:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("wallets");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    //*********  Print *********

    public void print()    {
        try{
            Log.d("Wallets", "Object");
            Log.d("Wallets Size", String.valueOf(this.size()));
            Log.d("Wallets ",  this.toString() );
        } catch (Exception ex){}
    }
}
