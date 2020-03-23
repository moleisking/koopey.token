package com.koopey.view;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Wallet;
import com.koopey.model.Wallets;

/**
 * Created by Scott on 26/09/2017.
 * https://stackoverflow.com/questions/18579590/how-to-send-data-from-dialogfragment-to-a-fragment
 */

public class WalletDialogFragment extends DialogFragment implements PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "WALLET:DIALOG:";
    public OnWalletDialogFragmentListener delegate = (OnWalletDialogFragmentListener) getTargetFragment();
    private ArrayAdapter<CharSequence> currencyCodeAdapter;
    private ArrayAdapter<CharSequence> currencySymbolAdapter;
    private TextInputEditText txtValue;
    private Button btnCancel, btnCreate, btnDelete, btnUpdate;
    private Spinner lstCurrency;
    private Wallet wallet = new Wallet();
    private Wallets wallets;
    private AuthUser authUser;
    private boolean showCreateButton = false;
    private boolean showUpdateButton = false;
    private boolean showDeleteButton = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SerializeHelper.hasFile(this.getActivity(), AuthUser.AUTH_USER_FILE_NAME)) {
            this.authUser = (AuthUser) SerializeHelper.loadObject(this.getActivity(), AuthUser.AUTH_USER_FILE_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wallet_dialog, container, false);
        getDialog().setTitle(getResources().getString(R.string.label_wallets));
        //Set controls
        this.txtValue = (TextInputEditText) rootView.findViewById(R.id.txtValue);
        this.lstCurrency = (Spinner) rootView.findViewById(R.id.lstCurrency);
        this.btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        this.btnCreate = (Button) rootView.findViewById(R.id.btnCreate);
        this.btnUpdate = (Button) rootView.findViewById(R.id.btnUpdate);
        this.btnDelete = (Button) rootView.findViewById(R.id.btnDelete);
        this.populateCurrencies();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            //Set listeners
            this.btnCancel.setOnClickListener(this);
            this.btnCreate.setOnClickListener(this);
            this.btnUpdate.setOnClickListener(this);
            this.btnDelete.setOnClickListener(this);
            //Set fee
            if(this.getActivity().getIntent().hasExtra("wallet")) {
                this.wallet = (Wallet) this.getActivity().getIntent().getSerializableExtra("wallet");
            }
            this.populateWallet();
            //Set buttons
            if(this.getActivity().getIntent().hasExtra("showCreateButton")) {
                this.showCreateButton = this.getActivity().getIntent().getBooleanExtra("showCreateButton", false);
            }
            if(this.getActivity().getIntent().hasExtra("showUpdateButton")) {
                this.showUpdateButton = this.getActivity().getIntent().getBooleanExtra("showUpdateButton", false);
            }
            if(this.getActivity().getIntent().hasExtra("showDeleteButton")) {
                this.showDeleteButton = this.getActivity().getIntent().getBooleanExtra("showDeleteButton", false);
            }
            this.setButtons();
        } catch (Exception ex) {
            Log.w(LOG_HEADER, ":ER" + ex.getMessage());
        }
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if(alert.isSuccess()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_LONG).show();
                } else if (alert.isError()){
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == this.btnCancel.getId()) {
                this.dismiss();
            } else if (v.getId() == this.btnCreate.getId()) {
                this.getWalletFromViews();
                ((OnWalletDialogFragmentListener) getTargetFragment()).createWalletDialogEvent(wallet);
                this.dismiss();
            } else if (v.getId() == this.btnDelete.getId()) {
                ((OnWalletDialogFragmentListener) getTargetFragment()).deleteWalletDialogEvent(wallet);
                this.dismiss();
            } else if (v.getId() == this.btnUpdate.getId()) {
                this.getWalletFromViews();
                ((OnWalletDialogFragmentListener) getTargetFragment()).updateWalletDialogEvent(wallet);
                this.dismiss();
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        window.setLayout((int) (width * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.onDismiss(dialog);
    }

    public void getWalletFromViews() {
        //Check to prevent double casting error
        if (txtValue.getText().toString().equals("")) {
            txtValue.setText("0");
            this.wallet.value = 0.0d;
        } else {
            this.wallet.value = Double.parseDouble(txtValue.getText().toString());
        }
        if (lstCurrency.getSelectedItem().toString().equals("")) {
            lstCurrency.setSelection(0);
            this.wallet.currency = getResources().getString(R.string.default_currency);
        } else {
            this.wallet.currency = lstCurrency.getSelectedItem().toString().toLowerCase();
        }
    }

    private void populateCurrencies() {
        this.currencyCodeAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_codes, android.R.layout.simple_spinner_item);
        this.currencySymbolAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_symbols, android.R.layout.simple_spinner_item);
        this.currencySymbolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.lstCurrency.setAdapter(currencySymbolAdapter);
        if (this.showUpdateButton || this.showDeleteButton) {
            lstCurrency.setSelection(currencyCodeAdapter.getPosition(wallet.currency));
        }
    }

    public void populateWallet() {
        if (this.wallet != null) {
            this.txtValue.setText(Double.toString(wallet.value));
            this.lstCurrency.setSelection(currencyCodeAdapter.getPosition(wallet.currency));
        } else {
            this.wallet = new Wallet();
        }
    }

    public void setButtons() {
        if (this.showCreateButton) {
            this.btnCreate.setVisibility(View.VISIBLE);
        } else {
            this.btnCreate.setVisibility(View.GONE);
        }
        if (this.showUpdateButton) {
            this.btnUpdate.setVisibility(View.VISIBLE);
        } else {
            this.btnUpdate.setVisibility(View.GONE);
        }
        if (this.showDeleteButton) {
            this.btnDelete.setVisibility(View.VISIBLE);
        } else {
            this.btnDelete.setVisibility(View.GONE);
        }
    }

    public interface OnWalletDialogFragmentListener {
        void createWalletDialogEvent(Wallet wallet);

        void updateWalletDialogEvent(Wallet wallet);

        void deleteWalletDialogEvent(Wallet wallet);
    }
}
