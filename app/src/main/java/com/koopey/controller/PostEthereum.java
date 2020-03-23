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

public class PostEthereum implements PostJSON.PostResponseListener {

    private final String LOG_HEADER = "POST:ETHEREUM";
    private final int POST_ETHEREUM = 1002;
    public PostEthereumListener delegate = null;
    private Context context;
    private Ethereum ethereum;
    private String token = "";

    public PostEthereum(Context context, AuthUser myUser) {
        this.context = context;
        this.token = myUser.token;
        this.ethereum.account = myUser.wallets.getEthereumWallet().name;
        //  this.ethereum.address = myUser.wallets.getEthereumWallet().address;
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (output.contains("ethereum")) {
                Ethereum temp = new Ethereum();
                temp.parseJSON(output);
                if (temp.isBalance()) {
                    this.ethereum.balance = temp.balance;
                    delegate.readEthereumBalanceEvent(this.ethereum);
                } else if (temp.isTransaction()) {
                    delegate.readEthereumTransactionEvent(this.ethereum);
                }
            } else if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    delegate.writeEthereumMessageEvent(context.getResources().getString(R.string.error_update));
                } else if (alert.isSuccess()) {
                    delegate.writeEthereumMessageEvent(context.getResources().getString(R.string.info_update));
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    /**
     * @author Scott Johnston
     * @return current user account ethereum balance
     * @link https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_sendtransaction
     * @since 1.0
     */
    public void getBalance() {
        if (this.ethereum != null && !this.ethereum.isEmpty()) {
            PostJSON asyncTask = new PostJSON(this.context);
            asyncTask.delegate = this;
            asyncTask.execute(this.context.getString(R.string.get_ethereum_read_balance),
                    this.ethereum.toString(),
                    token); //"{ account :" + myUser.BTCAccount + "}"
        }
    }

    /**
     * @author Scott Johnston
     * @param value  amount of gas to transfer to system invoice account
     * @link https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_sendtransaction
     * @since 1.0
     */
    public void postSystemInvoice(User seller, String value) {
        if (this.ethereum != null && !this.ethereum.isEmpty() && (seller != null)) {
            //create new ethereum object for this transaction
            Ethereum ethereumTransaction = new Ethereum();
            ethereumTransaction.from = this.ethereum.account;
            ethereumTransaction.to = seller.wallets.getEthereumWallet().name;
            ethereumTransaction.value = value;
            //Post ethereum transaction
            PostJSON asyncTask = new PostJSON(this.context);
            asyncTask.delegate = this;
            asyncTask.execute(this.context.getString(R.string.post_ethereum_create_system_invoice),
                    ethereumTransaction.toString(),
                    token); //"{ account :" + myUser.BTCAccount + "}"
        }
    }

    /**
     * @author Scott Johnston
     * @param seller from account
     * @param value  amount of gas to transfer to system receipt account
     * @link https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_sendtransaction
      * @since 1.0
     */
    public void postSystemReceipt(User seller, String value) {
        if (this.ethereum != null && !this.ethereum.isEmpty() && (seller != null)) {
            //create new ethereum object for this transaction
            Ethereum ethereumTransaction = new Ethereum();
            ethereumTransaction.from = this.ethereum.account;
            ethereumTransaction.to = seller.wallets.getEthereumWallet().name;
            ethereumTransaction.value = value;
            //Post ethereum transaction
            PostJSON asyncTask = new PostJSON(this.context);
            asyncTask.delegate = this;
            asyncTask.execute(this.context.getString(R.string.post_ethereum_create_system_receipt),
                    ethereumTransaction.toString(),
                    token);
        }
    }

    /**
     *  @author Scott Johnston
     * @param seller from account
     * @param value  amount of gas to transfer to seller receipt account
     * @link https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_sendtransaction
     * @since 1.0
     * */
    public void postReceipt(User seller, String value) {
        if (this.ethereum != null && !this.ethereum.isEmpty() && (seller != null)) {
            //create new ethereum object for this transaction
            Ethereum ethereumTransaction = new Ethereum();
            ethereumTransaction.from = this.ethereum.account;
            ethereumTransaction.to = seller.wallets.getEthereumWallet().name;
            ethereumTransaction.value = value;
            //Post ethereum transaction
            PostJSON asyncTask = new PostJSON(this.context);
            asyncTask.delegate = this;
            asyncTask.execute(this.context.getString(R.string.post_ethereum_create_receipt),
                    ethereumTransaction.toString(),
                    token); //"{ account :" + myUser.BTCAccount + "}"
        }
    }

    public interface PostEthereumListener {
        void readEthereumBalanceEvent(Ethereum ethereum);

        void readEthereumTransactionEvent(Ethereum ethereum);

        void writeEthereumMessageEvent(String message);
    }
}
