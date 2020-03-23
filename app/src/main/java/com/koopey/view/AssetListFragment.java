package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.AssetAdapter;
import com.koopey.controller.GetJSON;

import com.koopey.controller.UserAdapter;
import com.koopey.model.Asset;
import com.koopey.model.Assets;

/**
 * Created by Scott on 18/01/2017.
 */
public class AssetListFragment extends ListFragment  {

    private final String LOG_HEADER = "ASSET:LIST";
    private final int ASSET_LIST_FRAGMENT = 315;
    protected Assets assets = new Assets();
    protected FloatingActionButton btnCreate;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.btnCreate = (FloatingActionButton) getActivity().findViewById(R.id.btnCreate);
        this.btnCreate.setVisibility(View.GONE);
        this.syncAssets();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_assets));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assets, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
            if (this.assets != null && this.assets.size() >0) {
               Asset asset = this.assets.get(position);
                    ((MainActivity) getActivity()).showAssetReadFragment(asset);
            }
    }

    protected void populateAssets() {
        if (this.assets != null && this.assets.size() > 0 ) {
            AssetAdapter assetAdapter = new AssetAdapter(this.getActivity(), this.assets);
            this.setListAdapter(assetAdapter);
        }
    }

    protected void syncAssets() {
        if (getActivity().getIntent().hasExtra("assets")) {
            this.assets = (Assets)getActivity().getIntent().getSerializableExtra("assets");
            this.populateAssets();
        } else if (SerializeHelper.hasFile(this.getActivity(), assets.ASSET_SEARCH_RESULTS_FILE_NAME)) {
            this.assets = (Assets) SerializeHelper.loadObject(this.getActivity(), Assets.ASSET_SEARCH_RESULTS_FILE_NAME);
            this.populateAssets();
        } else {
            this.assets =  new Assets();
        }
    }
}
