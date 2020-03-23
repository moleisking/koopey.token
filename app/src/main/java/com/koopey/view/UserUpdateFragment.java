package com.koopey.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import com.koopey.R;
import com.koopey.common.HashHelper;
import com.koopey.common.ImageHelper;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GetJSON;
import com.koopey.controller.PostJSON;
import com.koopey.controller.TagAdapter;
import com.koopey.controller.GPSReceiver;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Bitcoin;
import com.koopey.model.Ethereum;
import com.koopey.model.Image;
import com.koopey.model.Tag;
import com.koopey.model.Tags;

import com.koopey.model.User;
import com.koopey.model.Wallet;



public class UserUpdateFragment extends  Fragment implements GetJSON.GetResponseListener, PostJSON.PostResponseListener ,
        GPSReceiver.OnGPSReceiverListener,   PlaceSelectionListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final int DEFAULT_IMAGE_SIZE = 256;
    public static final int REQUEST_GALLERY_IMAGE = 197;
    private ArrayAdapter<CharSequence> currencyCodeAdapter;
    private ArrayAdapter<CharSequence> currencySymbolAdapter;
    private Bitcoin bitcoin;
    private Ethereum ethereum;
    private final String LOG_HEADER = "USER:UPDATE";
    private final int USER_UPDATE_FRAGMENT = 102;
    private EditText  txtAddress , txtDescription, txtEducation, txtEmail, txtMobile, txtName ;
       private FloatingActionButton btnUpdate;
    private GPSReceiver gps;
    private ImageView imgAvatar;
    private AuthUser authUser;
    private  PlaceAutocompleteFragment placeFragment;
    private PopupMenu imagePopupMenu;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_update));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Define place fragment
        try {
            this.placeFragment = (PlaceAutocompleteFragment)
                    getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            this.placeFragment.setOnPlaceSelectedListener(this);

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            placeFragment.setFilter(typeFilter);

            this.txtAddress = ((EditText) placeFragment.getView().findViewById(R.id.place_autocomplete_search_input));
            this.txtAddress.setHint(R.string.label_address);
        } catch (Exception aex) {
            Log.d(LOG_HEADER + ":ER", aex.getMessage());
        }

        //Define views
        this.btnUpdate = (FloatingActionButton) getActivity().findViewById(R.id.btnUpdate);
        this.imgAvatar = (ImageView) getActivity().findViewById(R.id.imgUser);
        this.txtName = (EditText) getActivity().findViewById(R.id.txtName);
        this.txtEmail = (EditText) getActivity().findViewById(R.id.txtEmail);
        this.txtMobile = (EditText) getActivity().findViewById(R.id.txtMobile);
        this.txtEducation = (EditText) getActivity().findViewById(R.id.txtEducation);
        this.txtDescription = (EditText) getActivity().findViewById(R.id.txtDescription);

        //Set listeners
        this.btnUpdate.setOnClickListener(this);
        this.imgAvatar.setOnClickListener(this);

        //Populate controls
        this.populateUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_IMAGE) {
                this.imgAvatar.setImageBitmap(ImageHelper.onGalleryImageResult(data));
                this.authUser.avatar = ImageHelper.BitmapToSmallUri(((BitmapDrawable) imgAvatar.getDrawable()).getBitmap());
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == btnUpdate.getId()) {
                if (!txtName.getText().equals("")) {
                    this.authUser.name = txtName.getText().toString();
                }
                if (!txtEmail.getText().equals("")) {
                    authUser.email = txtEmail.getText().toString().toLowerCase();
                }
                if (!txtMobile.getText().equals("")) {
                    authUser.mobile = txtMobile.getText().toString();
                }
                if (!txtDescription.getText().equals("")) {
                    authUser.description = txtDescription.getText().toString();
                }
                if (!txtEducation.getText().equals("")) {
                    authUser.education = txtEducation.getText().toString();
                }
                authUser.hash = HashHelper.parseMD5(authUser.toString());
                this.postUserUpdate();
            } else if (v.getId() == imgAvatar.getId()) {
this.showImagePopupMenu(v);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Define myUser
        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        //Start GPS
        gps = new GPSReceiver(getActivity());
        gps.delegate = this;
        gps.Start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_user_update, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.placeFragment != null){
            getChildFragmentManager().beginTransaction().remove(placeFragment).commit();
        }
    }

    @Override
    public void onError(Status status) {
        Log.i(LOG_HEADER + "LOC:ER", status.toString());
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
            } else if (header.contains("user")) {
                User user = new User();
                user.parseJSON(output);
                authUser.syncronize(user);
                SerializeHelper.saveObject(this.getActivity(), authUser);
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
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onGPSWarning(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGPSPositionResult(LatLng position) {
        try {
            this.authUser.location.latitude =  position.latitude;
            this.authUser.location.longitude =  position.longitude;
            gps.Stop();
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_image_gallery:
                this.startGalleryRequest();
                return true;
            case R.id.nav_image_cancel:
                imagePopupMenu.dismiss();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        //Sets authUser registered address from google place object
        this.authUser.location.latitude =  place.getLatLng().latitude;
        this.authUser.location.longitude =  place.getLatLng().longitude;
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
            } else if (header.contains("bitcoin")) {
                this.bitcoin = new Bitcoin();
                this.bitcoin.parseJSON(output);
                this.bitcoin.print();
                SerializeHelper.saveObject(this.getActivity(), this.bitcoin);
                Toast.makeText(this.getActivity(), getResources().getString(R.string.info_create_bitcoin), Toast.LENGTH_SHORT).show();
            } else if (header.contains("ethereum")) {
                this.ethereum = new Ethereum();
                this.ethereum.parseJSON(output);
                this.ethereum.print();
                SerializeHelper.saveObject(this.getActivity(), this.ethereum);
                Toast.makeText(this.getActivity(), getResources().getString(R.string.info_create_ethereum), Toast.LENGTH_SHORT).show();
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
            this.txtName.setText(authUser.name);
            this.txtEmail.setText(authUser.email);
            this.txtMobile.setText(authUser.mobile);
            this.txtDescription.setText(authUser.description);
            this.txtAddress.setText(authUser.location.address);
            if (ImageHelper.isImageUri(authUser.avatar)){
                this.imgAvatar.setImageBitmap(ImageHelper.UriToBitmap( authUser.avatar));
            }
        }
    }

    private void postUserUpdate() {
        if (this.authUser != null) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            asyncTask.delegate = this;
            asyncTask.execute(getResources().getString(R.string.post_user_update), authUser.toString(), ((MainActivity) getActivity()).getAuthUserFromFile().getToken());
        }
    }

    private void postImageUpdate(Image image) {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_user_update_avatar), image.toString(), authUser.getToken());
    }

    public void showImagePopupMenu(View v) {
        this.imagePopupMenu = new PopupMenu(this.getActivity(), v, Gravity.BOTTOM);
        this.imagePopupMenu.setOnMenuItemClickListener( this);
        this.imagePopupMenu.inflate(R.menu.menu_image);
        this.imagePopupMenu.show();
    }

    public void startGalleryRequest() {
        //Note* return-data = true to return a Bitmap, false to directly save the cropped image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.putExtra("outputX", DEFAULT_IMAGE_SIZE);
        intent.putExtra("outputY", DEFAULT_IMAGE_SIZE);
        intent.putExtra("return-data", true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY_IMAGE);
    }
}