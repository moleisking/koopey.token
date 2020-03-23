package com.koopey.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLng;

import com.koopey.R;
import com.koopey.common.CurrencyHelper;
import com.koopey.common.DistanceHelper;
import com.koopey.common.HashHelper;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GPSReceiver;
import com.koopey.controller.GetJSON;
import com.koopey.controller.PostJSON;


import com.koopey.controller.TagAdapter;

import com.koopey.model.Alert;
import com.koopey.model.Asset;
import com.koopey.model.AuthUser;
import com.koopey.model.Location;
import com.koopey.model.Reviews;
import com.koopey.model.Tag;
import com.koopey.model.Tags;
import com.koopey.model.Image;

/**
 * Created by Scott on 18/01/2017.
 */
public class AssetUpdateFragment extends Fragment implements GetJSON.GetResponseListener, GPSReceiver.OnGPSReceiverListener,
        ImageListFragment.OnImageListFragmentListener, PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "AUTH:UPDATE";

    private Asset asset;
    private Reviews reviews;
    private AuthUser authUser;
    private EditText txtTitle, txtDescription, txtValue;
    private FloatingActionButton btnUpdate, btnDelete;
    private GPSReceiver gps;
    private ImageView img;
    private Tags tags = new Tags();
    private TagAdapter tagAdapter;
    private TagTokenAutoCompleteView lstTags;
    private Spinner lstCurrency;
    private Switch btnSold;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialize objects
        this.btnDelete = (FloatingActionButton) getActivity().findViewById(R.id.btnDelete);
        this.btnSold = (Switch) getActivity().findViewById(R.id.btnSold);
        this.btnUpdate = (FloatingActionButton) getActivity().findViewById(R.id.btnUpdate);
        this.img = (ImageView) getActivity().findViewById(R.id.img);
        this.lstCurrency = (Spinner) getActivity().findViewById(R.id.lstCurrency);
        this.lstTags = (TagTokenAutoCompleteView) getActivity().findViewById(R.id.lstTags);
        this.txtTitle = (EditText) getActivity().findViewById(R.id.txtTitle);
        this.txtDescription = (EditText) getActivity().findViewById(R.id.txtDescription);
        this.txtValue = (EditText) getActivity().findViewById(R.id.txtValue);

        this.btnUpdate.setOnClickListener(this);
        this.btnDelete.setOnClickListener(this);
        this.img.setOnClickListener(this);
        this.lstTags.setLanguage(this.authUser.language);
        this.lstTags.allowDuplicates(false);
        this.lstTags.setAdapter(tagAdapter);

        this.populateCurrencies();
        this.populateTags();
        this.populateAsset();
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
            if (v.getId() == this.btnDelete.getId()) {
                this.showDeleteDialog();
            } else if (v.getId() == btnUpdate.getId()) {
                this.asset.currency = CurrencyHelper.currencySymbolToCode(this.lstCurrency.getSelectedItem().toString());
                if (!this.txtTitle.getText().equals("")) {
                    this.asset.title = this.txtTitle.getText().toString();
                }
                if (!this.txtDescription.getText().equals("")) {
                    this.asset.description = this.txtDescription.getText().toString();
                }
                if (!this.txtValue.getText().equals("")) {
                    this.asset.value = Double.valueOf(txtValue.getText().toString());
                }
                if (this.asset.tags.compareTo(this.lstTags.getSelectedTags()) != 0) {
                    this.asset.tags.setTagList(this.lstTags.getObjects());
                }
                this.asset.location = authUser.location;
                this.asset.available = btnSold.isChecked();
                this.asset.hash = HashHelper.parseMD5(asset.toString());
                //Post data to server for update
                this.postAssetUpdate();

            } else if (v.getId() == this.img.getId()) {
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
        if (SerializeHelper.hasFile(this.getActivity(), Tags.TAGS_FILE_NAME)) {
            this.tags = (Tags) SerializeHelper.loadObject(this.getActivity(), Tags.TAGS_FILE_NAME);
        } else {
            ((MainActivity) this.getActivity()).getTags();
        }

        //Start GPS
        gps = new GPSReceiver(getActivity());
        gps.delegate = this;
        gps.Start();

        //Try to define asset object, which is passed from MyProductsFragment
        if (getActivity().getIntent().hasExtra("asset")) {
            this.asset = (Asset) getActivity().getIntent().getSerializableExtra("asset");
            this.tagAdapter = new TagAdapter(this.getActivity(), tags, this.asset.tags, this.authUser.language);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_update, container, false);
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
    public void onGPSPositionResult(LatLng position) {
        try {
            this.asset.location.latitude = position.latitude;
            this.asset.location.longitude = position.longitude;
            this.asset.location.position = DistanceHelper.LatLngToPosition(position.latitude, position.longitude);
            gps.Stop();
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":GPS", ex.getMessage());
        }
    }

    @Override
    public void onGPSWarning(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("asset")) {
                Asset asset = new Asset();
                asset.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), this.asset);
            }else  if (header.contains("reviews")) {
                this.reviews.parseJSON(output);
                    SerializeHelper.saveObject(this.getActivity(), this.reviews);
            } else if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                } else if (alert.isSuccess()) {
                    if (alert.message.equals("asset.delete")){
                        Toast.makeText(this.getActivity(), getResources().getString(R.string.info_delete), Toast.LENGTH_SHORT).show();
                    } else if (alert.message.equals("asset.update")){
                        Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.setVisibility();
    }

    private void setVisibility() {
        if (this.getResources().getBoolean(R.bool.transactions)) {
            this.txtValue.setVisibility(View.VISIBLE);
            this.lstCurrency.setVisibility(View.VISIBLE);
        } else {
            this.txtValue.setVisibility(View.GONE);
            this.lstCurrency.setVisibility(View.GONE);
        }
    }

    private void populateTags() {
        this.tagAdapter = new TagAdapter(this.getActivity(), this.tags, this.asset.tags, this.authUser.language);
        this.lstTags.allowDuplicates(false);
        this.lstTags.setAdapter(tagAdapter);
        this.lstTags.setTokenLimit(15);
    }

    private void populateCurrencies() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.lstCurrency.setAdapter(adapter);
        this.lstCurrency.setSelection(adapter.getPosition(this.asset.currency));
    }

    public void populateAsset() {
        if (this.asset != null) {
            this.txtTitle.setText(this.asset.title);
            this.txtDescription.setText(this.asset.description);
            this.txtValue.setText(this.asset.value.toString());
            for (Tag t : this.asset.tags.getList()) {
                this.lstTags.addObject(t);
            }
            try {
                this.img.setImageBitmap(asset.images.get(0).getBitmap());
            } catch (Exception e) {
            }
        }
    }

    public void createImageListFragmentEvent(Image image) {
        this.asset.images.add(image);
        this.postImageCreate(image);
    }

    public void updateImageListFragmentEvent(Image image) {
        this.asset.images.set(image);
        this.postImageUpdate(image);
    }

    public void deleteImageListFragmentEvent(Image image) {
        this.asset.images.remove(image);
        this.postImageDelete(image);
    }

    private void postImageCreate(Image image) {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_asset_create_image), image.toString(), authUser.getToken());
    }

    private void postImageUpdate(Image image) {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_asset_update_image), image.toString(), authUser.getToken());
    }

    private void postImageDelete(Image image) {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_asset_delete_image), image.toString(), authUser.getToken());
    }


    private void postAssetUpdate() {
        if (this.asset.user.id.equals(this.authUser.id)) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            asyncTask.delegate = this;
            asyncTask.execute(getResources().getString(R.string.post_asset_update), asset.toString(), authUser.getToken());
            Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_LONG).show();
        }
    }

    private void postAssetDelete() {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_asset_delete), asset.toString(), authUser.getToken());
        Toast.makeText(this.getActivity(), getResources().getString(R.string.label_delete), Toast.LENGTH_LONG).show();
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.label_delete))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        postAssetDelete();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}
