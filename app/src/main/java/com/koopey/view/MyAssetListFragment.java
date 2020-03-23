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

import java.util.ArrayList;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.AssetAdapter;
import com.koopey.controller.GetJSON;


import com.koopey.model.Alert;
import com.koopey.model.Asset;
import com.koopey.model.Assets;
import com.koopey.model.AuthUser;


/**
 * Created by Scott on 10/02/2017.
 */
public class MyAssetListFragment extends AssetListFragment implements GetJSON.GetResponseListener, View.OnClickListener {
    private final String LOG_HEADER = "MY:ASSETS";
    private AuthUser authUser;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.btnCreate = (FloatingActionButton) getActivity().findViewById(R.id.btnCreate);
        this.btnCreate.setVisibility(View.VISIBLE);
        this.btnCreate.setOnClickListener(this);
        this.syncAssets();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_my_assets));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnCreate.getId()) {
            ((MainActivity) getActivity()).showAssetCreateFragment();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();
    }

    @Override
    public void onGetResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isSuccess()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_authentication), Toast.LENGTH_LONG).show();
                } else if (alert.isError()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_authentication), Toast.LENGTH_LONG).show();
                }
            } else if (header.contains("assets")) {
                this.assets = new Assets();
                this.assets.parseJSON(output);
                this.saveAssets();
                this.populateAssets();
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

            if (this.assets != null && this.assets.size() >0) {
                Asset asset = (Asset) assets.get(position);
                //AssetReadFragment will hide or show the update button
                ((MainActivity) getActivity()).showAssetReadFragment(asset);
            }
    }

    @Override
    protected void syncAssets() {
        if (SerializeHelper.hasFile(this.getActivity(), Assets.MY_ASSETS_FILE_NAME)) {
            this.assets = (Assets) SerializeHelper.loadObject(this.getActivity(), Assets.MY_ASSETS_FILE_NAME);
            this.populateAssets();
            this.getAssets();
        } else {
            this.assets = new Assets();
            this.getAssets();
        }
    }

    private void saveAssets() {
        this.assets.fileType = Assets.MY_ASSETS_FILE_NAME;
        SerializeHelper.saveObject(this.getActivity(), this.assets);
    }

    private  void getAssets() {
        GetJSON asyncTask = new GetJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(this.getString(R.string.get_assets_read_mine), "", this.authUser.getToken());
    }
}
