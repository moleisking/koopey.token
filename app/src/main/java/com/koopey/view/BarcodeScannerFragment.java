package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.koopey.R;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Transaction;
/*import com.koopey.hardware.BarcodeGraphic;
import com.koopey.hardware.BarcodeGraphicTracker;
import com.koopey.hardware.BarcodeTrackerFactory;
import com.koopey.hardware.CameraSourcePreview;
import com.koopey.hardware.GraphicOverlay;*/

import java.io.IOException;

/**
 * Created by Scott on 26/12/2017.
 * https://code.tutsplus.com/tutorials/reading-qr-codes-using-the-mobile-vision-api--cms-24680
 */

public class BarcodeScannerFragment extends Fragment implements PostJSON.PostResponseListener, SurfaceHolder.Callback {

    private static final int BARCODE_SCANNER_FRAGMENT = 901;
    private final String LOG_HEADER = "BARCODE:SCANNER";
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private AuthUser authUser;
    private Transaction transaction;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.cameraView = (SurfaceView) getActivity().findViewById(R.id.camera_view);
        if (this.barcodeDetector != null) {
            this.barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() > 0) {
                        String barcode = barcodes.valueAt(0).displayValue;
                        getActivity().getIntent().putExtra("barcode", barcode);
                        ((MainActivity) getActivity()).showTransactionUpdateFragment(transaction,barcode);
                    }
                }
            });
        } else {
            Log.d(LOG_HEADER + ":ER", "onActivityCreated detector");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_barcode));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        if (getActivity().getIntent().hasExtra("transaction") && ((Transaction) getActivity().getIntent().getSerializableExtra("transaction") != null)) {
            this.transaction = (Transaction) getActivity().getIntent().getSerializableExtra("transaction");
        }

        this.barcodeDetector = new BarcodeDetector.Builder(this.getActivity())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();
        if (!barcodeDetector.isOperational()) {
            Log.d(LOG_HEADER, "Barcode detector not operational!");
            return;
        }

        this.cameraSource = new CameraSource
                .Builder(this.getActivity(), barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();
    }

    @Override
    public void onPostResponse(String output) {
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
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            this.cameraSource.start(this.cameraView.getHolder());
        } catch (SecurityException se) {
            Log.e(LOG_HEADER, se.getMessage());
        } catch (IOException ie) {
            Log.e(LOG_HEADER, ie.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.cameraSource.stop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_barcode_scanner, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.cameraSource != null) {
            this.cameraSource.stop();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraView != null) {
            SurfaceHolder surfaceHolder = cameraView.getHolder();
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            surfaceHolder.setSizeFromLayout();
            surfaceHolder.addCallback(this);
            Log.d(LOG_HEADER, "onResume cameraView");
        } else {
            Log.d(LOG_HEADER + ":ER", "onResume");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}