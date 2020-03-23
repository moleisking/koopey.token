package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import com.koopey.R;
import com.koopey.common.HashHelper;
import com.koopey.common.ImageHelper;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GPSReceiver;
import com.koopey.controller.GetJSON;
import com.koopey.controller.PostJSON;

import com.koopey.controller.TagAdapter;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Location;
import com.koopey.model.Tags;
import com.koopey.model.Image;
import com.koopey.model.Wallet;

/**
 * Created by Scott on 14/02/2017.
 */
public class UserCreateFragment extends Fragment implements GetJSON.GetResponseListener, GPSReceiver.OnGPSReceiverListener, PlaceSelectionListener,
        PopupMenu.OnMenuItemClickListener, PostJSON.PostResponseListener, View.OnClickListener {

    public static final int REQUEST_GALLERY_IMAGE = 197;
    private static final int DEFAULT_IMAGE_SIZE = 256;
    private final String LOG_HEADER = "USER:CREATE";
    private ArrayAdapter<CharSequence> currencyCodeAdapter;
    private ArrayAdapter<CharSequence> currencySymbolAdapter;
    private DatePicker txtBirthday;
    private EditText txtAddress, txtAlias, txtEmail, txtDescription, txtMobile, txtName, txtPassword;
    private FloatingActionButton btnCreate, btnLogin;
    private GPSReceiver gps;
    private ImageView imgAvatar;
    private AuthUser authUser = new AuthUser();
    private Spinner lstCurrency;
    private PopupMenu imagePopupMenu;
    private PlaceAutocompleteFragment placeFragment;
    private boolean imageChanged = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Define place fragment
        try {
            this.placeFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            this.placeFragment.setOnPlaceSelectedListener(this);

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            this.placeFragment.setFilter(typeFilter);

            this.txtAddress = ((EditText) placeFragment.getView().findViewById(R.id.place_autocomplete_search_input));
            this.txtAddress.setHint(R.string.label_address);
        } catch (Exception aex) {
            Log.d(LOG_HEADER, aex.getMessage());
        }

        //Define controls
        this.btnCreate = (FloatingActionButton) getActivity().findViewById(R.id.btnCreate);
        this.btnLogin = (FloatingActionButton) getActivity().findViewById(R.id.btnLogin);
        this.imgAvatar = (ImageView) getActivity().findViewById(R.id.imgAvatar);
        this.txtAlias = (EditText) getActivity().findViewById(R.id.txtAlias);
        this.txtName = (EditText) getActivity().findViewById(R.id.txtName);
        this.txtEmail = (EditText) getActivity().findViewById(R.id.txtEmail);
        this.txtMobile = (EditText) getActivity().findViewById(R.id.txtMobile);
        this.txtPassword = (EditText) getActivity().findViewById(R.id.txtPassword);
        this.txtDescription = (EditText) getActivity().findViewById(R.id.txtDescription);
        this.txtBirthday = (DatePicker) getActivity().findViewById(R.id.txtBirthday);
        this.lstCurrency = (Spinner) getActivity().findViewById(R.id.lstCurrency);

        //Set listeners
        this.btnCreate.setOnClickListener(this);
        this.imgAvatar.setOnClickListener(this);

        //Populate controls
        this.txtBirthday.updateDate(1979, 1, 1);
        this.populateCurrencies();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOCATION) {
                // Toast.makeText(this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
            } else if (requestCode == REQUEST_GALLERY_IMAGE) {
                this.imgAvatar.setImageBitmap(ImageHelper.onGalleryImageResult(data));
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_LOCATION) {
                Toast.makeText(this.getActivity(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
            } else if (requestCode == REQUEST_GALLERY_IMAGE) {
                Toast.makeText(this.getActivity(), "Gallery upload cancelled.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_create));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Start GPS
        gps = new GPSReceiver(this.getActivity());
        gps.delegate = this;
        gps.Start();

        //Check Permissions
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_create, container, false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnCreate.getId()) {
            this.setDevice();
            //current and registered locations are set in overload methods
            if (!this.txtAlias.getText().equals("")) {
                this.authUser.alias = this.txtAlias.getText().toString();
            }
            if (!txtName.getText().equals("")) {
                this.authUser.name = this.txtName.getText().toString();
            }
            if (!txtPassword.getText().equals("")) {
                this.authUser.password = this.txtPassword.getText().toString();
            }
            if (!this.txtEmail.getText().equals("")) {
                this.authUser.email = this.txtEmail.getText().toString().toLowerCase();
            }
            if (!this.txtMobile.getText().equals("")) {
                this.authUser.mobile = this.txtMobile.getText().toString();
            }
            if (!this.txtDescription.getText().equals("")) {
                this.authUser.description = this.txtDescription.getText().toString();
            }

            this.authUser.birthday = new Date(txtBirthday.getYear(), txtBirthday.getMonth(), txtBirthday.getDayOfMonth()).getTime();
            //Create wallet
            Wallet wallet = new Wallet();
            wallet.value = Double.valueOf(getResources().getString(R.string.default_credit));
            wallet.type = "primary";
            wallet.currency = "tok";
            this.authUser.wallets.add(wallet);
            //Create hash
            this.authUser.hash = HashHelper.parseMD5(authUser.toString());
            //Post new data
            if (this.authUser.isCreate() && imageChanged) {
                postUserCreate();
            } else {
                // txtError.setText(R.string.error_field_required);
            }
        } else if (v.getId() == btnLogin.getId()) {
            this.showLoginActivity();
        } else if (v.getId() == imgAvatar.getId()) {
            this.showImagePopupMenu(imgAvatar);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.placeFragment != null) {
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
            this.authUser.location.longitude = position.longitude;
            this.authUser.location.latitude = position.latitude;
            this.authUser.location.position = Location.convertLatLngToPosition(position.latitude, position.longitude);
            gps.Stop();
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":GPS", ex.getMessage());
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_image_gallery:
                this.startGalleryRequest(this.imgAvatar);
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
        //Sets myuser registered address from google place object
        this.authUser.location.longitude = place.getLatLng().longitude;
        this.authUser.location.latitude = place.getLatLng().latitude;
        this.authUser.location.position = Location.convertLatLngToPosition(this.authUser.location.latitude, this.authUser.location.longitude);
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("user")) {
                this.showLoginActivity();
            } else if (header.contains("tags")) {
                Tags tags = new Tags();
                tags.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), tags);
            } else if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_create), Toast.LENGTH_SHORT).show();
                } else if (alert.isSuccess()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_create), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public void setDevice() {
        TelephonyManager tm = (TelephonyManager) this.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            try {
                this.authUser.device = tm.getDeviceId();
            } catch (SecurityException ex) {
                Log.d(LOG_HEADER, ex.getMessage());
            }
        } else if (this.authUser.device == null || this.authUser.device.equals("")) {
            this.authUser.device = Settings.Secure.getString(this.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            this.authUser.device = "0000000000";
        }
    }

    private void populateCurrencies() {
        this.currencyCodeAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_codes, android.R.layout.simple_spinner_item);
        this.currencySymbolAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_symbols, android.R.layout.simple_spinner_item);
        currencySymbolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.lstCurrency.setAdapter(currencySymbolAdapter);
        lstCurrency.setSelection(currencyCodeAdapter.getPosition(authUser.currency));
    }

    private void postUserCreate() {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_user_create), authUser.toString(), "");
    }

    private void showLoginActivity() {
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        startActivity(intent);
        this.getActivity().finish();
    }

    public void showImagePopupMenu(View v) {
        this.imagePopupMenu = new PopupMenu(this.getActivity(), v, Gravity.BOTTOM);
        imagePopupMenu.setOnMenuItemClickListener(this);
        imagePopupMenu.inflate(R.menu.menu_image);
        imagePopupMenu.show();
    }

    public void startGalleryRequest(View image) {
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
