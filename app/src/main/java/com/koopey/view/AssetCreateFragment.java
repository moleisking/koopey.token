package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLng;

import com.koopey.R;
import com.koopey.common.HashHelper;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GPSReceiver;
import com.koopey.controller.GetJSON;
import com.koopey.controller.PostJSON;

import com.koopey.controller.TagAdapter;
import com.koopey.model.Alert;
import com.koopey.model.Asset;
import com.koopey.model.Assets;
import com.koopey.model.Image;
//import com.koopey.model.MyProducts;
import com.koopey.model.AuthUser;
import com.koopey.model.Tags;

/**
 * Created by Scott on 14/02/2017.
 */
public class AssetCreateFragment extends Fragment implements GetJSON.GetResponseListener, GPSReceiver.OnGPSReceiverListener,
        ImageListFragment.OnImageListFragmentListener,         PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "ASSET:CREATE";
    private EditText txtTitle, txtDescription, txtValue;
    private ImageView img;
    private AuthUser authUser;
    private Asset asset = new Asset();
    private Assets assets = new Assets();
    private TagTokenAutoCompleteView lstTags;
    private Tags tags = new Tags();
    private GPSReceiver gps;
    private Spinner lstCurrency;
    private FloatingActionButton btnCreate;
    private TagAdapter tagAdapter;
    private ArrayAdapter<CharSequence> currencyCodeAdapter;
    private ArrayAdapter<CharSequence> currencySymbolAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialize objects
        this.img = (ImageView) getActivity().findViewById(R.id.img);
        this.txtTitle = (EditText) getActivity().findViewById(R.id.txtTitle);
        this.txtDescription = (EditText) getActivity().findViewById(R.id.txtDescription);
        this.txtValue = (EditText) getActivity().findViewById(R.id.txtValue);
        this.lstTags = (TagTokenAutoCompleteView) getActivity().findViewById(R.id.lstTags);
        this.btnCreate = (FloatingActionButton) getActivity().findViewById(R.id.btnCreate);

        //Populate controls
        this.populateTags();
        this.populateCurrencies();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_my_asset));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == btnCreate.getId()) {
                //Create asset object
                this.asset.user = authUser.getUserBasicWithAvatar();
                this.asset.title = txtTitle.getText().toString();
                this.asset.description = txtDescription.getText().toString();
                this.asset.value = Double.valueOf(txtValue.getText().toString());
                this.asset.hash = HashHelper.parseMD5(asset.toString());
                //Check asset object
                if (this.asset.isValid()) {
                    //Post new asset to server
                    postAssetCreate();
                    //Add asset to local file
                    this.assets.add(asset);
                    SerializeHelper.saveObject(this.getActivity(), assets);
                    ((MainActivity) getActivity()).showMyAssetListFragment();
                }
                Toast.makeText(this.getActivity(), getResources().getString(R.string.label_create), Toast.LENGTH_LONG).show();
            } else if (v.getId() == img.getId()) {
                ((MainActivity) getActivity()).showImageListFragment(this.asset.images);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        //Define tags
        if (SerializeHelper.hasFile(this.getActivity(), tags.TAGS_FILE_NAME)) {
            this.tags = (Tags) SerializeHelper.loadObject(this.getActivity(), Tags.TAGS_FILE_NAME);
            this.tagAdapter = new TagAdapter(this.getActivity(), tags, authUser.language);
        } else {
            ((MainActivity) this.getActivity()).getTags();
        }

        //Start GPS
        gps = new GPSReceiver(getActivity());
        gps.delegate = this;
        gps.Start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_create, container, false);
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
            } else if (header.contains("tags")) {
                Tags tags = new Tags();
                tags.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), tags);
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onGPSConnectionResolutionRequest(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this.getActivity(), GPSReceiver.OnGPSReceiverListener.CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":GPS", ex.getMessage());
        }
    }

    @Override
    public void onGPSWarning(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGPSPositionResult(LatLng position) {
        try {
            this.asset.location.latitude = position.latitude;
            this.asset.location.longitude = position.longitude;
            gps.Stop();
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":GPS", ex.getMessage());
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
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void createImageListFragmentEvent(Image image) {
        this.asset.images.add(image);
        // this.postImageCreate(image);
    }

    public void updateImageListFragmentEvent(Image image) {
        this.asset.images.set(image);
        //this.postImageUpdate(image);
    }

    public void deleteImageListFragmentEvent(Image image) {
        this.asset.images.remove(image);
        //this.postImageDelete(image);
    }

    private void populateTags() {
        this.tagAdapter = new TagAdapter(this.getActivity(), this.tags, this.asset.tags, this.authUser.language);
        this.lstTags.allowDuplicates(false);
        this.lstTags.setAdapter(tagAdapter);
        this.lstTags.setTokenLimit(15);
    }

    private void populateCurrencies() {
        this.currencyCodeAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_codes, android.R.layout.simple_spinner_item);
        this.currencySymbolAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_symbols, android.R.layout.simple_spinner_item);
        this.currencySymbolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.lstCurrency.setAdapter(currencySymbolAdapter);
    }

    private void postAssetCreate() {
        PostJSON asyncTask = new PostJSON(this.getActivity());//this.getActivity()
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_asset_create), asset.toString(), authUser.getToken());
    }
}