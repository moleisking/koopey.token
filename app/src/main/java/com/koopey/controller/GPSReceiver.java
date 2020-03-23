package com.koopey.controller;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Scott on 05/10/2016.
 */
public class GPSReceiver implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String LOG_HEADER = "GPS:RECEIVER";
    //private PendingResult<LocationSettingsResult> mLocationSettingRequestResult;
    //private double currentLatitude;
    private LatLng currentLatLng = new LatLng(0.0d,0.0d);
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Context context;

    public OnGPSReceiverListener delegate = null;

    public interface OnGPSReceiverListener {

        //static final int REQUEST_CONNECTION_FAILURE_RESOLUTION = 0;
        //static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;

        static final int REQUEST_LOCATION = 2;
        static final  int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

        void onGPSConnectionResolutionRequest(ConnectionResult connectionResult );
        void onGPSWarning(String string);
        void onGPSPositionResult(LatLng position);
    }

    public GPSReceiver(Context context)
    {
        //currentLatLng = new LatLng(0.0d,0.0d);
        if (googleApiClient == null) {
            Log.w("onCreate", "Create the GoogleApiClient object");
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Log.w("onCreate", "Create the LocationRequest object");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    /*public void setResponseGPSListener(ResponseGPS listener){
        delegate = listener;
    }*/

    @Override
    public void onConnected(Bundle bundle)
    {
        Log.i("Location", "Location services connected.");

        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (location == null)
            {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

            } else {
                //If everything went fine lets get latitude and longitude
                currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                //this.currentLatitude = location.getLatitude();
                //this.currentLongitude = location.getLongitude();
                Log.d("GPS:onConnected:S",currentLatLng.toString());
                delegate.onGPSPositionResult(currentLatLng);
                Log.d("GPS:onConnected:E",String.valueOf(currentLatLng.latitude)+":"+ String.valueOf(currentLatLng.longitude));
            }
        }
        catch (SecurityException ex)
        {
            Log.i("GPS:onConnect", ex.getMessage());
            delegate.onGPSWarning("GPS failed to connect");
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.d("GPS:onConnectionSuspend", "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GPS:onConnectionFailed", "Failed");
        if (connectionResult.hasResolution()) {
            // Start an Activity that tries to resolve the error
            delegate.onGPSConnectionResolutionRequest(connectionResult);
        } else {
                // If no resolution is available, display a dialog to the user with the error.
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        //this.currentLatitude = location.getLatitude();
        //this.currentLongitude = location.getLongitude();
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        delegate.onGPSPositionResult(new LatLng(location.getLatitude(),location.getLongitude() ));
        Log.d("GPS:LocationChange",String.valueOf(currentLatLng.latitude)+":"+ String.valueOf(currentLatLng.longitude));
    }

    public void Start() {
        googleApiClient.connect();
        Log.i("GPS:onResume()", "Called");
    }

    public void Stop()
    {
        Log.d("GPS:onPause()", "Called");
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }


}
