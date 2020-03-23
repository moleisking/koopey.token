package com.koopey.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import com.koopey.R;
import com.koopey.common.DistanceHelper;
import com.koopey.common.ImageHelper;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.PostJSON;
import com.koopey.controller.TagAdapter;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Images;

import com.koopey.model.Tag;
import com.koopey.model.Tags;
import com.koopey.model.Transaction;
import com.koopey.model.User;
import com.koopey.model.Wallet;
import com.koopey.model.Wallets;

/*Note: No calls to server through ResponseAPI for profile. User object passed from ResultsFragment. UserAccount userId to post review though messages.*/
public class UserReadFragment extends Fragment implements PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "USER:READ";
    private TextView txtAlias, txtName, txtDescription, txtAddress, txtDistance;
    private ImageView imgUser;
    private AuthUser authUser = new AuthUser();
    private User user = new User();
    private FloatingActionButton btnMessage, btnUpdate;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.imgUser = (ImageView) getActivity().findViewById(R.id.imgUser);
        this.txtAlias = (TextView) getActivity().findViewById(R.id.txtAlias);
        this.txtName = (TextView) getActivity().findViewById(R.id.txtName);
        this.txtDescription = (TextView) getActivity().findViewById(R.id.txtDescription);
        this.txtDistance = (TextView) getActivity().findViewById(R.id.txtDistance);
        this.btnMessage = (FloatingActionButton) getActivity().findViewById(R.id.btnMessage);
        this.btnUpdate = (FloatingActionButton) getActivity().findViewById(R.id.btnUpdate);
        this.imgUser.setOnClickListener(this);
        this.btnMessage.setOnClickListener(this);
        this.btnUpdate.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Define myUser
        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        //Define user object, which is passed from SearchFragment
        if (getActivity().getIntent().hasExtra("user")) {
            this.user = (User) getActivity().getIntent().getSerializableExtra("user");
            if (this.user.type.equals("basic")) {
                this.postUserRead();
            }
        } else if (SerializeHelper.hasFile(this.getActivity(), User.USER_FILE_NAME)) {
            this.user = (User) SerializeHelper.loadObject(this.getActivity(), User.USER_FILE_NAME);
        } else {
            this.user = new User();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_read, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.populateUser();
        this.setVisibility();

        if (this.authUser.equals(this.user)) {
            ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_my_user));
        } else {
            ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_user));
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
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_SHORT).show();
                }
            } else if (header.contains("user")) {
                this.user.parseJSON(output);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == btnMessage.getId()) {
                //Show message fragment with current user
                this.getActivity().getIntent().putExtra("user", this.user);
                ((MainActivity) getActivity()).showMessageListFragment();
            } else if (v.getId() == btnUpdate.getId()) {
                ((MainActivity) getActivity()).showUserUpdateFragment();
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void setVisibility() {
        //Alias
        if (getResources().getBoolean(R.bool.alias)) {
            txtAlias.setVisibility(View.VISIBLE);
        } else {
            txtAlias.setVisibility(View.GONE);
        }
        //Button
        if (getResources().getBoolean(R.bool.alias)) {
            txtAlias.setVisibility(View.VISIBLE);
        } else {
            txtAlias.setVisibility(View.GONE);
        }
        //Name
        if (getResources().getBoolean(R.bool.name) && !getResources().getBoolean(R.bool.alias)) {
            txtName.setVisibility(View.VISIBLE);
        } else {
            txtName.setVisibility(View.GONE);
        }
        //Update
        if (this.authUser.id.equals(this.user.id)) {
            btnUpdate.setVisibility(View.VISIBLE);
        } else {
            btnUpdate.setVisibility(View.GONE);
        }
    }

    protected void populateUser() {
        if (this.user != null) {
            this.txtAlias.setText(this.user.alias);
            this.txtDescription.setText(this.user.description);
            this.txtDistance.setText(DistanceHelper.DistanceToKilometers(this.user.distance));
            //Set user or default image
            try {
                this.imgUser.setImageBitmap(ImageHelper.UriToBitmap(this.user.avatar));
            } catch (Exception ex) {
            }
        } else {
            Log.d(LOG_HEADER + ":ER", "No user found");
        }
    }

    private void postUserRead() {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_user_read), user.toString(), authUser.getToken());
    }
}
