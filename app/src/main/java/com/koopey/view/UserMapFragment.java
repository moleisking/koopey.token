package com.koopey.view;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.model.User;
import com.koopey.model.Users;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

//implements OnMapReadyCallback
public class UserMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int MAP_PERMISSION_REQUEST = 1003;
    private final String LOG_HEADER = "USER:MAP";
    private MapView mapView;
    private GoogleMap googleMap;
    private Users users;
    private HashMap markers = new HashMap<Marker, String>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_users));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getIntent().hasExtra("users")) {
            this.users = (Users) getActivity().getIntent().getSerializableExtra("users");
        } else if (SerializeHelper.hasFile(this.getActivity(), Users.USERS_FILE_NAME)) {
            this.users = (Users) SerializeHelper.loadObject(this.getActivity(), Users.USERS_FILE_NAME);
        } else {
            users = new Users();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        //Define Views
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Request permissions for location
        if (!this.hasMapPermissions()) {
            this.requestMapPermissions();
        }

        //Set map ready listener
        mapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        this.googleMap = mMap;
        this.populateMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MAP_PERMISSION_REQUEST) {
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                this.populateMap();
            }
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        User user = this.users.get((String) markers.get(marker));
        ((MainActivity) getActivity()).showUserReadFragment(user);
        return true;
    }

    @TargetApi(23)
    private boolean hasMapPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if ((this.getActivity().checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    && (this.getActivity().checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                return true;
            } else {
                return false;
            }
        } else if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @TargetApi(23)
    private void requestMapPermissions() {
        this.getActivity().requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, MAP_PERMISSION_REQUEST);
    }

    public void onMapClick(View view) {
        Log.w(LOG_HEADER, "Map button Clicked -> Do Nothing");
    }

    private void populateMap() {
        try {
            // For showing a move to my location button
            this.googleMap.setMyLocationEnabled(true);
            this.googleMap.setOnMarkerClickListener(this);
            //Add all the users
            for (int i = 0; i < this.users.size(); i++) {
                User user = this.users.get(i);
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.title(user.alias);
                markerOptions.snippet(user.description);
                markerOptions.position(user.location.getLatLng());
                Marker marker = this.googleMap.addMarker(markerOptions);
                markers.put(marker, user.id);
                // For zooming automatically to the location of the marker
                if (i == this.users.size() - 1) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(user.location.getLatLng()).zoom(12).build();
                    this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        } catch (SecurityException ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
