package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Search;
import com.koopey.model.Tags;
import com.koopey.model.Transactions;
import com.koopey.model.Users;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Scott on 12/12/2017.
 */

public class SearchTransactionsFragment extends Fragment implements PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "SEARCH:TRANSACTIONS";
    private EditText txtId ;
    private DatePicker txtEnd, txtStart;
    private FloatingActionButton btnSearch;
    private AuthUser authUser = new AuthUser();
    private Search search = new Search();
    private Transactions transactions ;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.txtId = (EditText) getActivity().findViewById(R.id.txtId);
        this.txtStart = (DatePicker) getActivity().findViewById(R.id.txtStart);
        this.txtEnd = (DatePicker) getActivity().findViewById(R.id.txtEnd);
        this.btnSearch = (FloatingActionButton) getActivity().findViewById(R.id.btnSearch);
        this.btnSearch.setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_search));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_search, container, false);
    }

    @Override
    public void onPostResponse(String output) {
           try {
            String header = output.substring(0, 10).toLowerCase();
             if (header.contains("alert")) {
                   Alert alert = new Alert();
                   alert.parseJSON(output);
                   if(alert.isError()){
                       Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_LONG).show();
                   } else  if(alert.isSuccess()){
                       Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_LONG).show();
                   }
               } else            if (header.contains("transactions")) {
                this.transactions = new Transactions();
                this.transactions.parseJSON(output);
                if (transactions.size() == 0) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_no_results), Toast.LENGTH_LONG).show();
                } else {
                    this.getActivity().getIntent().putExtra("transaction", this.transactions );
                    ((MainActivity) getActivity()).showTransactionListFragment();
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.btnSearch.getId()) {
            this.search.id = this.txtId.getText().toString();
            this.search.start = new Date(txtStart.getYear(), txtStart.getMonth(), txtStart.getDayOfMonth()).getTime();
            this.search.end = new Date(txtEnd.getYear(), txtEnd.getMonth(), txtEnd.getDayOfMonth()).getTime();
            this.search.type = "Users";
            this.postSearch();
        }
    }

    private void postSearch(){
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_user_read_many), search.toString(), authUser.getToken());
    }
}
