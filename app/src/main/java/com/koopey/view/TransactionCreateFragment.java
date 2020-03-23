package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.Asset;
import com.koopey.model.AuthUser;
import com.koopey.model.Transaction;
import com.koopey.model.Transactions;

/**
 * Created by Scott on 06/04/2017.
 */
public class TransactionCreateFragment extends Fragment implements PostJSON.PostResponseListener, View.OnClickListener {
    private final String LOG_HEADER = "TRANSACTION:CREATE:";
    private EditText txtName, txtValue, txtTotal, txtQuantity;
    private FloatingActionButton btnCreate;
    private Spinner lstCurrency;
    private ArrayAdapter<CharSequence> currencyCodeAdapter;
    private ArrayAdapter<CharSequence> currencySymbolAdapter;
    private Transaction transaction = new Transaction();
    private Transactions transactions;
    private AuthUser authUser;
    private Asset asset;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.txtName = (EditText) getActivity().findViewById(R.id.txtName);
        this.txtValue = (EditText) getActivity().findViewById(R.id.txtValue);
        this.txtTotal = (EditText) getActivity().findViewById(R.id.txtTotal);
        this.txtQuantity = (EditText) getActivity().findViewById(R.id.txtQuantity);
        this.lstCurrency = (Spinner) getActivity().findViewById(R.id.lstCurrency);
        this.btnCreate = (FloatingActionButton) getActivity().findViewById(R.id.btnCreate);
        this.btnCreate.setOnClickListener(this);
        this.populateCurrencies();
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
            if (v.getId() == btnCreate.getId()) {
                //Build product object
                this.transaction.users.addBuyer(authUser.getUserBasicWithAvatar());
                this.transaction.users.addSeller(asset.user.getUserBasicWithAvatar());
                this.transaction.name = txtName.getText().toString();
                this.transaction.itemValue = Double.valueOf(txtValue.getText().toString());
                this.transaction.quantity = Integer.valueOf(txtQuantity.getText().toString());
                this.transaction.totalValue = Double.valueOf(txtTotal.getText().toString());
                this.transaction.currency = lstCurrency.getSelectedItem().toString();
                //Post new data
                if (!this.transaction.isEmpty()) {
                    postTransaction();
                    //Add asset to local MyAssets file
                    this.transactions.add(transaction);
                    SerializeHelper.saveObject(this.getActivity(), transactions);
                    ((MainActivity) getActivity()).showMyAssetListFragment();
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.label_create), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_field_required), Toast.LENGTH_LONG).show();
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

        if (getActivity().getIntent().hasExtra("asset")) {
            this.asset = (Asset) getActivity().getIntent().getSerializableExtra("asset");
        }

        if (SerializeHelper.hasFile(this.getActivity(), Transactions.TRANSACTIONS_FILE_NAME)) {
            this.transactions = (Transactions) SerializeHelper.loadObject(this.getActivity(), Transactions.TRANSACTIONS_FILE_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_create, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.btnCreate.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()){
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_create), Toast.LENGTH_LONG).show();
                } else if (alert.isSuccess()){
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_create), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    private void populateCurrencies() {
        this.currencyCodeAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_codes, android.R.layout.simple_spinner_item);
        this.currencySymbolAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_symbols, android.R.layout.simple_spinner_item);
        this.currencySymbolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.lstCurrency.setAdapter(currencySymbolAdapter);
        lstCurrency.setSelection(currencyCodeAdapter.getPosition(transaction.currency));
    }

    private void postTransaction() {
        if (this.transaction != null && this.asset != null) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            asyncTask.delegate = this;
            asyncTask.execute(getResources().getString(R.string.post_transaction_create), transaction.toString(), authUser.getToken());
        } else {
            Toast.makeText(this.getActivity(), getResources().getString(R.string.error_create), Toast.LENGTH_LONG).show();
        }
    }
}
