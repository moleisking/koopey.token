package com.koopey.view;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import java.util.Date;
import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GetJSON;
import com.koopey.controller.TransactionAdapter;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Transaction;
import com.koopey.model.Transactions;
import com.koopey.model.User;
import com.koopey.model.Users;

/**
 * Created by Scott on 06/04/2017.
 */
public class TransactionListFragment extends ListFragment
        implements GetJSON.GetResponseListener, View.OnClickListener {

    //http://www.truiton.com/2016/09/android-example-programmatically-scan-qr-code-and-bar-code/
    //https://stackoverflow.com/questions/8831050/android-how-to-read-qr-code-in-my-application
    private static final int TRANSACTION_LIST_FRAGMENT = 405;
    private final String LOG_HEADER = "TRANSACTION:LIST";
    private Transactions transactions = new Transactions();
    private AuthUser authUser;
    private TransactionAdapter transactionAdapter;
    private Date start;
    private Date end;
    private FloatingActionButton btnCreate, btnSearch;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.btnCreate = (FloatingActionButton) getActivity().findViewById(R.id.btnCreate);
        this.btnSearch = (FloatingActionButton) getActivity().findViewById(R.id.btnSearch);
        this.btnCreate.setOnClickListener(this);
        this.btnSearch.setOnClickListener(this);
        this.populateTransactions();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_transactions));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == btnCreate.getId()) {
                ((MainActivity) getActivity()).showTransactionCreateFragment();
            } else if (v.getId() == btnSearch.getId()) {
                ((MainActivity) getActivity()).showTransactionSearchFragment();
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        if (getActivity().getIntent().hasExtra("transactions")) {
            this.transactions = (Transactions) getActivity().getIntent().getSerializableExtra("transactions");
        } else if (SerializeHelper.hasFile(this.getActivity(), Transactions.TRANSACTIONS_FILE_NAME)) {
            this.transactions = (Transactions) SerializeHelper.loadObject(this.getActivity(), Transactions.TRANSACTIONS_FILE_NAME);
        } else {
            this.transactions = new Transactions();
            this.getTransactions();
        }

       /* if (getActivity().getIntent().hasExtra("date")) {
            this.start = new Date(getActivity().getIntent().getLongExtra("date", 0));
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onGetResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("transactions")) {
                this.transactions = new Transactions();
                this.transactions.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), transactions);
                this.populateTransactions();
            } else if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isSuccess()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_authentication), Toast.LENGTH_LONG).show();
                } else if (alert.isError()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_authentication), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try {
            Transaction transaction = transactions.get(position);
            if (this.isBuyer(transaction) || this.isSeller(transaction)) {
                getActivity().getIntent().putExtra("transaction", transaction);
                if (transaction.isReceipt()) {
                    ((MainActivity) getActivity()).showTransactionReadFragment(transaction);
                } else {
                    ((MainActivity) getActivity()).showTransactionUpdateFragment(transaction);
                }
            }
        } catch (Exception ex) {
        }
    }

    protected void getTransactions() {
        GetJSON asyncTask = new GetJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(this.getString(R.string.get_transaction_read_many), "", this.authUser.getToken());
    }

    public void setTransactions(Transactions transactions) {
        if (transactions != null) {
            this.transactions = transactions;
            this.populateTransactions();
        }
    }

    public void setVisibility(int visibility) {
        try {
            this.getListView().setVisibility(visibility);
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    protected void populateTransactions() {
        if (this.transactions != null && start != null && start.getTime() > 0) {
            //Only applies filter
            Transactions transactionsOfDate = transactions.getTransactionsOfDate(this.start);
            this.transactionAdapter = new TransactionAdapter(this.getActivity(), transactionsOfDate);
            this.setListAdapter(transactionAdapter);
        } else if (this.transactions != null) {
            //Shows all the transactions
            this.transactionAdapter = new TransactionAdapter(this.getActivity(), this.transactions);
            this.setListAdapter(transactionAdapter);
        }

    }

    private boolean isSeller(Transaction transaction) {
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

    private boolean isBuyer(Transaction transaction) {
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



   /* private void startQRCodeScan() {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, TRANSACTION_LIST_FRAGMENT);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);
            Log.d(LOG_HEADER + ":ER", e.getMessage());
        }
    }*/
}
