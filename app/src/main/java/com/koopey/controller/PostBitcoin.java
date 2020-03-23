package com.koopey.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Bitcoin;
import com.koopey.model.Ethereum;

import com.koopey.model.User;
import com.koopey.model.Wallet;

/**
 * Created by Scott on 11/01/2018.
 */

public class PostBitcoin implements  PostJSON.PostResponseListener  {

    private final String LOG_HEADER = "POST:BITCOIN";
    private final int POST_BITCOIN = 1001;
    private Context context ;
    private Bitcoin bitcoin ;
    private String token ="";

    public PostBitcoin( Context context, AuthUser authUser){
        this.context = context;
        this.token = authUser.token;
        this.bitcoin.address = authUser.wallets.getBitcoinWallet().name;
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("bitcoin")) {
                Bitcoin temp = new Bitcoin();
                temp.parseJSON(output);
                if(temp.isBalance()) {
                    this.bitcoin.amount = temp.amount;
                    delegate.readBitcoinBalanceEvent(this.bitcoin);
                }  else  if(temp.isTransaction()) {
                    delegate.readBitcoinTransactionEvent(this.bitcoin);
                }
            } else if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    delegate.writeBitcoinMessageEvent(context.getResources().getString(R.string.error_update));
                } else if (alert.isSuccess()) {
                    delegate.writeBitcoinMessageEvent(context.getResources().getString(R.string.info_update));
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    /**
     * @author Scott Johnston
     * @return current user address bitcoin balance
     * @link https://en.bitcoin.it/wiki/Original_Bitcoin_client/API_calls_list
     * @since 1.0
     */
    public void getBalance() {
        if (this.bitcoin != null && !this.bitcoin.isEmpty()) {
            PostJSON asyncTask = new PostJSON(this.context);
            asyncTask.delegate = this;
            asyncTask.execute(this.context.getString(R.string.get_bitcoin_read_balance),
                    this.bitcoin.toString(),
                    this.token);
        }
    }

    /**
     * @author Scott Johnston
       * @param seller from address
     * @param value  amount of bitcoins to transfer to system invoice account
     * @link https://en.bitcoin.it/wiki/Original_Bitcoin_client/API_calls_list
     * @since 1.0
     */
    public void postSystemInvoice(User seller, double value) {
        if (this.bitcoin != null && !this.bitcoin.isEmpty() && (seller != null)) {
            //create new bitcoin object for this transaction
            Bitcoin bitcoinTransaction = new Bitcoin();
            bitcoinTransaction.fromaddress = this.bitcoin.address;
            bitcoinTransaction.toaddress = seller.wallets.getBitcoinWallet().name;
            bitcoinTransaction.amount = value;
            //Post bitcoin transaction
            PostJSON asyncTask = new PostJSON(this.context);
            asyncTask.delegate = this;
            asyncTask.execute(this.context.getString(R.string.post_bitcoin_create_system_invoice),
                    bitcoinTransaction.toString(),
                    this.token);
        }
    }

    /**
     * @author Scott Johnston
     * @param value  amount of bitcoins to transfer to system receipt account
     * @link https://en.bitcoin.it/wiki/Original_Bitcoin_client/API_calls_list
         * @since 1.0
     */
    public void postSystemReceipt(double value) {
        if (this.bitcoin != null && !this.bitcoin.isEmpty() ) {
            //create new bitcoin object for this transaction
            Bitcoin bitcoinTransaction = new Bitcoin();
            bitcoinTransaction.fromaddress = this.bitcoin.address;
            bitcoinTransaction.amount = value;
            //Post bitcoin transaction
            PostJSON asyncTask = new PostJSON(this.context);
            asyncTask.delegate = this;
            asyncTask.execute(this.context.getString(R.string.post_bitcoin_create_system_receipt),
                    bitcoinTransaction.toString(),
                    this.token);
        }
    }

    /**
     * @author Scott Johnston
     * @param seller from address
     * @param value  amount of bitcoins to transfer to system receipt account
     * @link https://en.bitcoin.it/wiki/Original_Bitcoin_client/API_calls_list
     * @since 1.0
     */
    public void postReceipt(User seller, double value) {
        if (this.bitcoin != null && !this.bitcoin.isEmpty() && (seller != null)) {
            //create new bitcoin object for this transaction
            Bitcoin bitcoinTransaction = new Bitcoin();
            bitcoinTransaction.fromaddress = this.bitcoin.address;
            bitcoinTransaction.toaddress = seller.wallets.getBitcoinWallet().name;
            bitcoinTransaction.amount = value;
            //Post bitcoin transaction
            PostJSON asyncTask = new PostJSON(this.context);
            asyncTask.delegate = this;
            asyncTask.execute(this.context.getString(R.string.post_bitcoin_create_receipt),
                    bitcoinTransaction.toString(),
                    this.token);
        }
    }

    public PostBitcoinListener delegate = null;

    public interface PostBitcoinListener {
        void readBitcoinBalanceEvent(Bitcoin bitcoin);

        void readBitcoinTransactionEvent(Bitcoin bitcoin);

        void writeBitcoinMessageEvent(String message);
    }
}
