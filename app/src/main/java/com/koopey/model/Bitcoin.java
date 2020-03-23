package com.koopey.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Scott on 18/08/2017.
 */

public class Bitcoin implements Serializable {

    public static final String BITCOIN_FILE_NAME = "bitcoin.dat";
    private static final String LOG_HEADER = "BITCOIN";
    public String account = "";
    public String address = "";
    public String asm = "";
    public String fromaccount = "";
    public String fromaddress = "";
    public String hash = "";
    public String hex = "";
    public String pubkey = "";
    public String scriptPubKey = "";
    public String toaddress = "";
    public String txid = "";
    public String type = "";
    public Double amount = 0d;
    public int confirmations = 0;
    public int timestamp = 0;
    public int vout = 0;
    public int version = 0;
    public boolean complete = true;
    public boolean iscompressed = false;
    public boolean isvalid = false;
    public boolean ismine = false;
    public boolean iswatchonly = false;
    public boolean isscript = false;
    public boolean spendable = true;
    public boolean solvable = true;

    public Bitcoin() {
    }

    @Override
    public String toString() {
        return this.toJSONObject().toString();
    }


    public boolean isBalance() {
        return this.type.equals("balance");
    }

    public boolean isTransaction() {
        return this.type.equals("transaction");
    }


    public boolean isEmpty() {
        return (this.account.isEmpty() && this.address.isEmpty()) ? true : false;
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("bitcoin")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("bitcoin"));//{user:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("bitcoin")) {
                    this.parseJSON(new JSONObject(json));//{id:1}
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(JSONObject jsonObject) {
        try {
            //Booleans
            if (jsonObject.has("complete")) {
                this.complete = jsonObject.getBoolean("complete");
            }
            if (jsonObject.has("iscompressed")) {
                this.iscompressed = jsonObject.getBoolean("iscompressed");
            }
            if (jsonObject.has("ismine")) {
                this.ismine = jsonObject.getBoolean("ismine");
            }
            if (jsonObject.has("isscript")) {
                this.isscript = jsonObject.getBoolean("isscript");
            }
            if (jsonObject.has("isvalid")) {
                this.isvalid = jsonObject.getBoolean("isvalid");
            }
            if (jsonObject.has("iswatchonly")) {
                this.iswatchonly = jsonObject.getBoolean("iswatchonly");
            }
            if (jsonObject.has("spendable")) {
                this.spendable = jsonObject.getBoolean("spendable");
            }
            if (jsonObject.has("solvable")) {
                this.solvable = jsonObject.getBoolean("solvable");
            }
            //Integers
            if (jsonObject.has("confirmations")) {
                this.confirmations = Integer.parseInt(jsonObject.getString("confirmations"));
            }
            if (jsonObject.has("timestamp")) {
                this.timestamp = Integer.parseInt(jsonObject.getString("timestamp"));
            }
            if (jsonObject.has("version")) {
                this.version = Integer.parseInt(jsonObject.getString("version"));
            }
            if (jsonObject.has("vout")) {
                this.vout = Integer.parseInt(jsonObject.getString("vout"));
            }
            //Doubles
            if (jsonObject.has("amount")) {
                this.amount = Double.parseDouble(jsonObject.getString("amount"));
            }
            //Strings
            if (jsonObject.has("asm")) {
                this.asm = jsonObject.getString("asm");
            }
            if (jsonObject.has("account")) {
                this.account = jsonObject.getString("account");
            }
            if (jsonObject.has("address")) {
                this.address = jsonObject.getString("address");
            }
            if (jsonObject.has("fromaccount")) {
                this.fromaccount = jsonObject.getString("fromaccount");
            }
            if (jsonObject.has("fromaddress")) {
                this.fromaddress = jsonObject.getString("fromaddress");
            }
            if (jsonObject.has("hash")) {
                this.hash = jsonObject.getString("hash");
            }
            if (jsonObject.has("hex")) {
                this.hex = jsonObject.getString("hex");
            }
            if (jsonObject.has("scriptPubKey")) {
                this.scriptPubKey = jsonObject.getString("scriptPubKey");
            }
            if (jsonObject.has("toaddress")) {
                this.toaddress = jsonObject.getString("toaddress");
            }
            if (jsonObject.has("txid")) {
                this.txid = jsonObject.getString("txid");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
    }

    public void parseWallet(Wallet wallet) {
        this.address = wallet.name;
    }

    //Note: Used in MyProduct Object
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            //Booleans
            json.put("complete", this.complete);
            json.put("fromaccount", this.fromaccount);
            json.put("fromaddress", this.fromaddress);
            json.put("iscompressed", this.iscompressed);
            json.put("isvalid", this.isvalid);
            json.put("ismine", this.ismine);
            json.put("iswatchonly", this.iswatchonly);
            json.put("isscript", this.isscript);
            json.put("solvable", this.solvable);
            json.put("spendable", this.spendable);
            json.put("toaddress", this.toaddress);
            //Integers
            json.put("vout", this.vout);
            json.put("version", this.version);
            json.put("confirmations", this.confirmations);
            //Doubles
            json.put("amount", this.amount);
            //Strings
            if (!this.asm.equals("")) {
                json.put("asm", this.asm);
            }
            if (!this.account.equals("")) {
                json.put("account", this.account);
            }
            if (!this.address.equals("")) {
                json.put("address", this.address);
            }
            if (!this.hash.equals("")) {
                json.put("hash", this.hash);
            }
            if (!this.hex.equals("")) {
                json.put("hex", this.hex);
            }
            if (!this.hex.equals("")) {
                json.put("pubkey", this.pubkey);
            }
            if (!this.hex.equals("")) {
                json.put("scriptPubKey", this.scriptPubKey);
            }
            if (!this.txid.equals("")) {
                json.put("txid", this.txid);
            }
            if (!this.type.equals("")) {
                json.put("type", this.type);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
        return json;
    }

    public void print() {
        Log.d("Bitcoin", "Object");
        try {

            Log.d("txid", this.txid);
            Log.d("hash", this.hash);
            Log.d("hex", this.hex);
            Log.d("type", this.type);
            Log.d("address", this.address);
            Log.d("account", this.account);
            Log.d("fromaddress", this.fromaddress);
            Log.d("fromaccount", this.fromaccount);
            Log.d("toaddress", this.toaddress);
            Log.d("asm", this.asm);
            Log.d("scriptPubKey", this.scriptPubKey);
            Log.d("amount", String.valueOf(this.amount));
            Log.d("confirmations", String.valueOf(this.confirmations));
            Log.d("vout", String.valueOf(this.vout));
            Log.d("version", String.valueOf(this.version));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
