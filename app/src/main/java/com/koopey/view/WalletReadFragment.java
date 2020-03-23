package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.koopey.R;
import com.koopey.common.ImageHelper;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.PostJSON;
import com.koopey.controller.TagAdapter;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Bitcoin;
import com.koopey.model.Ethereum;
import com.koopey.model.User;
import com.koopey.model.Wallet;


public class WalletReadFragment extends Fragment implements PostJSON.PostResponseListener {
    private final String LOG_HEADER = "WALLET:READ";
    private TextView  txtCurrency, txtValue;
    private ImageView imgQRCode;
    private AuthUser authUser = new AuthUser();
    private Wallet wallet = new Wallet();
    private boolean showValue = true;
    private boolean showImage = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.imgQRCode = (ImageView) getActivity().findViewById(R.id.imgQRCode);
        this.txtCurrency = (TextView) getActivity().findViewById(R.id.txtCurrency);
        this.txtValue = (TextView) getActivity().findViewById(R.id.txtValue);
        if(this.getActivity().getIntent().hasExtra("wallet")) {
            this.wallet = (Wallet) this.getActivity().getIntent().getSerializableExtra("wallet");
            this.populateWallet();
        } else if (SerializeHelper.hasFile(this.getActivity(), Wallet.WALLET_FILE_NAME)) {
            this.wallet = (Wallet) SerializeHelper.loadObject(this.getActivity(), Wallet.WALLET_FILE_NAME);
        } else {
            this.wallet = new Wallet();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_wallet));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet_read, container, false);
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        TypedArray a = activity.obtainStyledAttributes(attrs, R.styleable.ParentChildListFragment);
        this.showValue = a.getBoolean(R.styleable.ParentChildListFragment_showValue, true);
        this.showImage = a.getBoolean(R.styleable.ParentChildListFragment_showImage, true);
        a.recycle();
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("bitcoin")) {
                Bitcoin bitcoin = new Bitcoin();
                bitcoin.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), bitcoin);
                this.txtValue.setText(String.valueOf(bitcoin.amount) );
                this.txtCurrency.setText("BTC");
            } else if (header.contains("ethereum")) {
                Ethereum ethereum = new Ethereum();
                ethereum.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), ethereum);
                this.txtValue.setText(String.valueOf(ethereum.balance) );
                this.txtCurrency.setText("ETH");
            } else if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                } else if (alert.isSuccess()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.checkVisibility();
    }

    private void populateWallet() {
        if (this.wallet != null && !this.wallet.isEmpty()) {
            this.txtCurrency.setText(this.wallet.currency.toUpperCase());
            if(showValue) {
                if (this.wallet.currency.equals("btc") ){
                    this.postBitcoinBalance();
                } else if (this.wallet.currency.equals("eth")) {
                    this.postEthereumBalance();
                } else if (this.wallet.currency.equals("tok")) {
                    this.txtValue.setText(this.wallet.value.toString());
                }
            }
            if(showImage) {
                try {
                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                    BitMatrix bitMatrix = qrCodeWriter.encode(this.wallet.name,
                            BarcodeFormat.QR_CODE, 1024, 1024);
                    imgQRCode.setImageBitmap(ImageHelper.BitmapFromBitMatrix(bitMatrix));
                } catch (Exception e) {
                }
            }
        }
    }

    private void checkVisibility() {
        if (!this.showImage ) {
            this.imgQRCode.setVisibility(View.GONE);
        } else {
            this.imgQRCode.setVisibility(View.VISIBLE);
        }
        if (!this.showValue){
            this.txtValue.setVisibility(View.GONE);
        } else {
            this.txtValue.setVisibility(View.VISIBLE);
        }
    }

    protected void postBitcoinBalance() {
        Wallet bitcoinWallet = this.authUser.wallets.getBitcoinWallet();
        if (bitcoinWallet != null && !bitcoinWallet.name.equals("")) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            Bitcoin bitcoin = new Bitcoin();
            bitcoin.address = this.authUser.wallets.getBitcoinWallet().name;
            asyncTask.delegate = this;
            asyncTask.execute(this.getString(R.string.get_bitcoin_read_balance),
                    bitcoin.toString(),
                    this.authUser.getToken()); //"{ account :" + myUser.BTCAccount + "}"
        }
    }

    protected void postEthereumBalance() {
        Wallet ethereumWallet = this.authUser.wallets.getEthereumWallet();
        if (ethereumWallet != null && !ethereumWallet.name.equals("")) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            Ethereum ethereum = new Ethereum();
            ethereum.account = this.authUser.wallets.getEthereumWallet().name;
            asyncTask.delegate = this;
            asyncTask.execute(this.getString(R.string.get_ethereum_read_balance),
                    ethereum.toString(),
                    this.authUser.getToken()); //"{ account :" + myUser.BTCAccount + "}"
        }
    }

    public void setWallet(Wallet wallet) {
        if (wallet != null) {
            this.wallet = wallet;
            this.populateWallet();
            this.checkVisibility();

        }
    }
}
