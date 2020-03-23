package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Messages;

/**
 * Created by Scott on 21/07/2017.
 */
public class DashboardFragment extends Fragment implements PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "DASHBOARD";
    private TextView txtUnread, txtUnsent, txtPositive, txtNegative;
    private RatingBar starAverage;
    private Switch btnAvailable, btnTrack;
    private AuthUser authUser = new AuthUser();
    private Messages messages;
    private WalletListFragment frgWallets;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Define wallets fragment
        try {
            this.getActivity().getIntent().putExtra("wallets", this.authUser.wallets);
            this.frgWallets = (WalletListFragment) getChildFragmentManager().findFragmentById(R.id.frgWallets);
        } catch (Exception aex) {
            Log.d(LOG_HEADER, aex.getMessage());
        }
        //Initialize objects
        this.txtPositive = (TextView) getActivity().findViewById(R.id.txtPositive);
        this.txtNegative = (TextView) getActivity().findViewById(R.id.txtNegative);
        this.txtUnread = (TextView) getActivity().findViewById(R.id.txtUnread);
        this.txtUnsent = (TextView) getActivity().findViewById(R.id.txtUnsent);
        this.starAverage = (RatingBar) getActivity().findViewById(R.id.starAverage);
        this.btnAvailable = (Switch) getActivity().findViewById(R.id.btnAvailable);
        this.btnTrack = (Switch) getActivity().findViewById(R.id.btnTrack);
        this.btnAvailable.setOnClickListener(this);
        this.btnTrack.setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_dashboard));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        if (SerializeHelper.hasFile(this.getActivity(), Messages.MESSAGES_FILE_NAME)) {
            this.messages = (Messages) SerializeHelper.loadObject(this.getActivity(), Messages.MESSAGES_FILE_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
      /*  if (this.authUser != null) {
            this.txtPositive.setText(String.valueOf(this.authUser.reviews.getPositive()));
            this.txtNegative.setText(String.valueOf(this.myUser.reviews.getNegative()));
            this.starAverage.setNumStars(this.myUser.reviews.getAverage());
        }*/
        if (this.messages != null) {
            this.txtUnread.setText(String.valueOf(this.messages.countUnread()));
            this.txtUnsent.setText(String.valueOf(this.messages.countUnsent()));
        }
        if (getResources().getBoolean(R.bool.transactions)) {
            this.frgWallets.setVisibility(View.VISIBLE);
        } else {
            this.frgWallets.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    this.authUser.wallets.getBitcoinWallet().value = 0.0;
                    this.authUser.wallets.getEthereumWallet().value = 0.0;
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
    public void onClick(View v) {
        if (v.getId() == btnAvailable.getId()) {
            postUserAvailable();
        } else if (v.getId() == btnTrack.getId()) {
            postUserTrack();
        }
    }

    protected void postUserAvailable() {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        //Remove excess data from user
        AuthUser temp = new AuthUser();
        temp.id = authUser.id;
        temp.available = btnAvailable.isChecked();
        asyncTask.execute(getResources().getString(R.string.post_user_update_available), temp.toString(), ((MainActivity) getActivity()).getAuthUserFromFile().getToken());
    }

    protected void postUserTrack() {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        //Remove excess data from user
        AuthUser temp = new AuthUser();
        temp.id = authUser.id;
        temp.track = btnTrack.isChecked();
        asyncTask.execute(getResources().getString(R.string.post_user_update_track), temp.toString(), ((MainActivity) getActivity()).getAuthUserFromFile().getToken());
    }
}