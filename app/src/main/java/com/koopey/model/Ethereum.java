package com.koopey.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Scott on 18/08/2017.
 * https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_getbalance
 */

public class Ethereum implements Serializable {

    private static final String LOG_HEADER = "ETHEREUM";
    public static final String ETHEREUM_FILE_NAME = "ethereum.dat";

    public int id = 0;
    public Double balance = 0d;
    public String account = "";
    public String address = "";
    public String jsonrpc = "";
    public String result = "";
    public String from = "";
    public String to = "";
    public String data = "";
    public String value = "";
    public String gas = "";
    public String gasPrice = "";
    public String nonce = "";
    public String type = "";

    public Ethereum() {
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
        return this.account.isEmpty();
    }

    public void parseJSON(String json) {
        try {
            if (json.length() >= 1) {
                if (json.substring(0, 10).replaceAll("\\s+", "").contains("ethereum")) {
                    this.parseJSON(new JSONObject(json).getJSONObject("ethereum"));//{user:{id:1}}
                } else if (!json.substring(0, 10).replaceAll("\\s+", "").contains("ethereum")) {
                    this.parseJSON(new JSONObject(json));//{id:1}
                }
            }
            //this.parseJSON(new JSONObject(json));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(JSONObject jsonObject) {
        try {
            //Integers
            if (jsonObject.has("id")) {
                this.id = Integer.parseInt(jsonObject.getString("id"));
            }
            //Double
            if (jsonObject.has("balance")) {
                this.balance = Double.parseDouble(jsonObject.getString("balance"));
            }
            //Strings
            if (jsonObject.has("account")) {
                this.account = jsonObject.getString("account");
            }
            if (jsonObject.has("address")) {
                this.address = jsonObject.getString("address");
            }
            if (jsonObject.has("jsonrpc")) {
                this.jsonrpc = jsonObject.getString("jsonrpc");
            }
            if (jsonObject.has("result")) {
                this.result = jsonObject.getString("result");
            }
            if (jsonObject.has("type")) {
                this.type = jsonObject.getString("type");
            }
            if (jsonObject.has("from")) {
                this.from = jsonObject.getString("from");
            }
            if (jsonObject.has("to")) {
                this.to = jsonObject.getString("to");
            }
            if (jsonObject.has("data")) {
                this.data = jsonObject.getString("data");
            }
            if (jsonObject.has("value")) {
                this.value = jsonObject.getString("value");
            }
            if (jsonObject.has("gas")) {
                this.gas = jsonObject.getString("gas");
            }
            if (jsonObject.has("gasPrice")) {
                this.gasPrice = jsonObject.getString("gasPrice");
            }
            if (jsonObject.has("nonce")) {
                this.nonce = jsonObject.getString("nonce");
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
    }

    public void parseWallet(Wallet wallet) {
        this.account = wallet.name;
        // this.address = wallet.address;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            //Integers
            json.put("id", this.id);
            //Doubles
            json.put("balance", this.balance);
            //Strings
            if (!this.account.equals("")) {
                json.put("account", this.account);
            }
            if (!this.address.equals("")) {
                json.put("address", this.address);
            }
            if (!this.jsonrpc.equals("")) {
                json.put("jsonrpc", this.jsonrpc);
            }
            if (!this.result.equals("")) {
                json.put("result", this.result);
            }
            if (!this.type.equals("")) {
                json.put("type", this.type);
            }
            if (!this.from.equals("")) {
                json.put("from", this.from);
            }
            if (!this.to.equals("")) {
                json.put("to", this.to);
            }
            if (!this.data.equals("")) {
                json.put("data", this.data);
            }
            if (!this.value.equals("")) {
                json.put("value", this.value);
            }
            if (!this.gas.equals("")) {
                json.put("gas", this.gas);
            }
            if (!this.gasPrice.equals("")) {
                json.put("gasPrice", this.gasPrice);
            }
            if (!this.nonce.equals("")) {
                json.put("nonce", this.nonce);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
        return json;
    }

    public void print() {
        Log.d("Ethereum", "Object");
        try {
            Log.d("id", String.valueOf(this.id));
            Log.d("balance", Double.toString(this.balance));
            Log.d("account", this.account);
            Log.d("address", this.address);
            Log.d("type", this.type);
            Log.d("jsonrpc", this.jsonrpc);
            Log.d("result", this.result);
            Log.d("from", this.from);
            Log.d("to", this.to);
            Log.d("data", this.data);
            Log.d("value", this.value);
            Log.d("gas", this.gas);
            Log.d("gasPrice", this.gasPrice);
            Log.d("nonce", this.nonce);
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
    }
}
