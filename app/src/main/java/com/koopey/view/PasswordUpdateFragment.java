package com.koopey.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.koopey.R;
import com.koopey.common.HashHelper;
import com.koopey.common.ImageHelper;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GPSReceiver;
import com.koopey.controller.GetJSON;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Bitcoin;
import com.koopey.model.Ethereum;
import com.koopey.model.Image;
import com.koopey.model.Tags;

public class PasswordUpdateFragment extends Fragment implements GetJSON.GetResponseListener, PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "PASSWORD:UPDATE";
    private final int PASSWORD_UPDATE_FRAGMENT = 502;
    private EditText  txtPasswordOld, txtPasswordNew;
       private FloatingActionButton btnUpdate;
    private AuthUser authUser;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Define views
        this.btnUpdate = (FloatingActionButton) getActivity().findViewById(R.id.btnUpdate);
        this.txtPasswordOld = (EditText) getActivity().findViewById(R.id.txtName);
        this.txtPasswordNew = (EditText) getActivity().findViewById(R.id.txtEmail);

        //Set listeners
        this.btnUpdate.setOnClickListener(this);

        //Populate controls
        this.populateUser();
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == btnUpdate.getId()) {
                if (!txtPasswordNew.getText().equals("")) {
                    this.authUser.name = txtPasswordNew.getText().toString();
                }
                if (!txtPasswordOld.getText().equals("")) {
                    authUser.password = txtPasswordOld.getText().toString();
                }
                authUser.hash = HashHelper.parseMD5(authUser.toString());
                //Post new data
                this.postPasswordChange();
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password_update, container, false);
    }

    @Override
    public void onGetResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
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
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                } else if (alert.isSuccess()) {
                    SerializeHelper.saveObject(this.getActivity(), authUser);
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void populateUser() {
        if (this.authUser != null) {
            this.txtPasswordNew.setText(this.authUser.name);
            this.txtPasswordOld.setText(this.authUser.email);
        }
    }

    private void postPasswordChange() {
        if (this.authUser != null) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            asyncTask.delegate = this;
            asyncTask.execute(getResources().getString(R.string.post_auth_password_change), authUser.toString(), ((MainActivity) getActivity()).getAuthUserFromFile().getToken());
        }
    }

    private void postPasswordForgotten() {
        if (this.authUser != null) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            asyncTask.delegate = this;
            asyncTask.execute(getResources().getString(R.string.post_auth_password_forgotten), authUser.toString(), ((MainActivity) getActivity()).getAuthUserFromFile().getToken());
        }
    }
}