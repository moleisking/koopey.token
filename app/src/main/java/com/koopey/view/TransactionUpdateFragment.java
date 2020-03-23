package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.koopey.R;
import com.koopey.common.CurrencyHelper;
import com.koopey.common.DateTimeHelper;
import com.koopey.common.ImageHelper;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.PostBitcoin;
import com.koopey.controller.PostEthereum;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.Bitcoin;
import com.koopey.model.Ethereum;
import com.koopey.model.AuthUser;
import com.koopey.model.Transaction;
import com.koopey.model.Transactions;
import com.koopey.model.User;
import com.koopey.model.Users;

/**
 * Created by Scott on 06/04/2017.
 */
public class TransactionUpdateFragment extends Fragment implements PostBitcoin.PostBitcoinListener, PostEthereum.PostEthereumListener, PostJSON.PostResponseListener, View.OnClickListener {

    private static final int TRANSACTION_UPDATE_FRAGMENT = 402;
    private final String LOG_HEADER = "TRANSACTION:UPDATE";
    private TextView txtName, txtReference, txtValue, txtTotal, txtQuantity , txtCurrency1, txtCurrency2, txtStart, txtEnd, txtState;
    private ImageView imgSecret;
    private AuthUser authUser  = new AuthUser();
    private Transaction transaction = new Transaction();
    private Transactions transactions = new Transactions();
    private FloatingActionButton btnUpdate;
    private PostBitcoin postBitcoin;
    private PostEthereum postEthereum;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.imgSecret = (ImageView) getActivity().findViewById(R.id.imgSecret);
        this.txtCurrency1 = (TextView) getActivity().findViewById(R.id.txtCurrency1);
        this.txtCurrency2 = (TextView) getActivity().findViewById(R.id.txtCurrency2);
        this.txtEnd = (TextView) getActivity().findViewById(R.id.txtEnd);
        this.txtState = (TextView) getActivity().findViewById(R.id.txtState);
        this.txtStart = (TextView) getActivity().findViewById(R.id.txtStart);
        this.txtName = (TextView) getActivity().findViewById(R.id.txtName);
        this.txtReference = (TextView) getActivity().findViewById(R.id.txtReference);
        this.txtValue = (TextView) getActivity().findViewById(R.id.txtValue);
        this.txtTotal = (TextView) getActivity().findViewById(R.id.txtTotal);
        this.txtQuantity = (TextView) getActivity().findViewById(R.id.txtQuantity);
        this.btnUpdate = (FloatingActionButton) getActivity().findViewById(R.id.btnUpdate);
        this.btnUpdate.setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_transaction));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == this.btnUpdate.getId()) {
                if (this.isBuyer()){
                    this.postTransactionByBuyer();
                } else if (this.isSeller()){
                    ((MainActivity) getActivity()).showBarcodeScannerFragment(transaction);
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        if (getActivity().getIntent().hasExtra("transaction") && ((Transaction) getActivity().getIntent().getSerializableExtra("transaction") != null)) {
            this.transaction = (Transaction) getActivity().getIntent().getSerializableExtra("transaction");
            if (getActivity().getIntent().hasExtra("barcode")) {
                this.transaction.secret = this.getActivity().getIntent().getStringExtra("barcode");
                this.getActivity().getIntent().removeExtra("barcode");
                this.postTransactionBySeller();
            }
        }

        if (SerializeHelper.hasFile(this.getActivity(), Transactions.TRANSACTIONS_FILE_NAME)) {
            this.transactions = (Transactions) SerializeHelper.loadObject(this.getActivity(), Transactions.TRANSACTIONS_FILE_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_update, container, false);
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()){
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_LONG).show();
                } else if (alert.isSuccess()){
                    this.transaction.state = "receipt";
                    if (this.transactions != null && !this.transactions.isEmpty())
                    this.transactions.set(this.transaction);
                    SerializeHelper.saveObject(this.getActivity(), this.transactions);
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_success), Toast.LENGTH_LONG).show();
                    ((MainActivity) getActivity()).showTransactionListFragment();
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.populateTransaction();
        if(this.transaction.isReceipt() ){
            this.imgSecret.setVisibility(View.GONE);
            this.btnUpdate.setVisibility(View.GONE);
        } else if(!this.transaction.isReceipt() ) {
            if(this.isBuyer() ) {
                this.trySetSecret();
                this.imgSecret.setVisibility(View.VISIBLE);
                this.btnUpdate.setVisibility(View.VISIBLE);
                this.btnUpdate.setImageDrawable(getResources().getDrawable(R.drawable.ic_payment_black_24dp));
            } else if(this.isSeller() ) {
                this.imgSecret.setVisibility(View.GONE);
                this.btnUpdate.setVisibility(View.VISIBLE);
                this.btnUpdate.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
            }
        }
    }

    private boolean isSeller() {
        boolean result = false;
        Users users = transaction.users;
        for (int i = 0; i < users.size(); i++ ) {
            User user = users.get(i);
            if (user.id.equals(this.authUser.id) && user.type.equals("seller")) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean isBuyer() {
        boolean result = false;
        Users users = transaction.users;
        for (int i = 0; i < users.size(); i++ ) {
            User user = users.get(i);
            if (user.id.equals(this.authUser.id) && user.type.equals("buyer")) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void trySetSecret(){
        if (this.transaction != null) {
            try {
                if ( !this.transaction.isReceipt()  && !this.transaction.secret.equals("") && (this.transaction.secret.length() > 0)) {
                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                    BitMatrix bitMatrix = qrCodeWriter.encode(this.transaction.secret, BarcodeFormat.QR_CODE, 1024, 1024);
                    this.imgSecret.setImageBitmap(ImageHelper.BitmapFromBitMatrix(bitMatrix));
                } else {
                    this.imgSecret.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.d(LOG_HEADER + ":ER", e.getMessage());
            }
        }
    }

    private void populateTransaction() {
        try {
            if (this.transaction != null) {
                this.txtName.setText(this.transaction.name);
                this.txtReference.setText(this.transaction.reference);
                this.txtValue.setText(String.valueOf(this.transaction.itemValue));
                this.txtTotal.setText(String.valueOf(this.transaction.totalValue));
                this.txtQuantity.setText(String.valueOf(this.transaction.quantity));
                this.txtState.setText(this.transaction.state);
                this.txtStart.setText(DateTimeHelper.epochToString(this.transaction.startTimeStamp,this.transaction.timeZone ));
                this.txtEnd.setText(DateTimeHelper.epochToString(this.transaction.endTimeStamp,this.transaction.timeZone ));
                this.txtCurrency1.setText(CurrencyHelper.currencyCodeToSymbol(this.transaction.currency));
                this.txtCurrency2.setText(CurrencyHelper.currencyCodeToSymbol(this.transaction.currency));
                                  if (this.transaction.isQuote()) {
                        this.txtState.setTextColor(getActivity().getResources().getColor(R.color.color_negative));
                    } else if (this.transaction.isInvoice()) {
                        this.txtState.setTextColor(getActivity().getResources().getColor(R.color.color_orange));
                    } else if (this.transaction.isReceipt()) {
                        this.txtState.setTextColor(getActivity().getResources().getColor(R.color.color_positive));
                    }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    private void postTransaction() {
        if (this.transaction != null) {
            if (this.transaction.isBuyer(this.authUser)) {
                PostJSON asyncTask = new PostJSON(this.getActivity());//this.getActivity()
                asyncTask.delegate = this;
                asyncTask.execute(getResources().getString(R.string.post_transaction_update), this.transaction.toString(), this.authUser.getToken());
                if(this.transaction.isBitcoin()){
                    this.postBitcoin =  new PostBitcoin(this.getActivity(), this.authUser);
                    this.postBitcoin.postReceipt(this.authUser, (this.transaction.totalValue / this.transaction.countBuyers()) );
                } else if(this.transaction.isEthereum()){
                    this.postEthereum =  new PostEthereum(this.getActivity(), this.authUser);
                    this.postEthereum .postReceipt(this.authUser, String.valueOf((this.transaction.totalValue / this.transaction.countBuyers()) ) );
                }
            } else  if (this.transaction.isSeller(this.authUser)) {
                PostJSON asyncTask = new PostJSON(this.getActivity());//this.getActivity()
                asyncTask.delegate = this;
                asyncTask.execute(getResources().getString(R.string.post_transaction_update_state_by_buyer), this.transaction.toString(), this.authUser.getToken());
            }
        }
    }

    private void postTransactionByBuyer() {
        if (this.transaction != null) {
            if (this.transaction.isBuyer(this.authUser)) {
                PostJSON asyncTask = new PostJSON(this.getActivity());//this.getActivity()
                asyncTask.delegate = this;
                asyncTask.execute(getResources().getString(R.string.post_transaction_update), this.transaction.toString(), this.authUser.getToken());
                if (this.transaction.isBitcoin()) {
                    this.postBitcoin = new PostBitcoin(this.getActivity(), this.authUser);
                    this.postBitcoin.postReceipt(this.authUser, (this.transaction.totalValue / this.transaction.countBuyers()));
                } else if (this.transaction.isEthereum()) {
                    this.postEthereum = new PostEthereum(this.getActivity(), this.authUser);
                    this.postEthereum.postReceipt(this.authUser, String.valueOf((this.transaction.totalValue / this.transaction.countBuyers())));
                }
            }
        }
    }

    private void postTransactionBySeller() {
        if (this.transaction != null) {
            PostJSON asyncTask = new PostJSON(this.getActivity());//this.getActivity()
            asyncTask.delegate = this;
            asyncTask.execute(getResources().getString(R.string.post_transaction_update_state_by_buyer), this.transaction.toString(), this.authUser.getToken());
        }
    }

    @Override
    public void readBitcoinBalanceEvent(Bitcoin bitcoin){
//Start Transaction
    }

    @Override
    public void readBitcoinTransactionEvent(Bitcoin bitcoin){

    }
    @Override
    public  void writeBitcoinMessageEvent(String message){
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void readEthereumBalanceEvent(Ethereum ethereum){

    }

    @Override
    public  void readEthereumTransactionEvent(Ethereum ethereum){

    }
    @Override
    public  void writeEthereumMessageEvent(String message){
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
