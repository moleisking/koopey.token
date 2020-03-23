package com.koopey.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.koopey.R;
import com.koopey.common.ImageHelper;
import com.koopey.model.Wallet;
import com.koopey.model.Wallets;

/**
 * Created by Scott on 06/10/2017.
 */

public class WalletAdapter extends ArrayAdapter<Wallet> {

    private final String LOG_HEADER = "WALLET:ADAPTER";
    private boolean showImage = true;
    private boolean showValue = false;

    public WalletAdapter(Context context, Wallets wallets, boolean showImages, boolean showValues) {
        super(context, 0, wallets.get());
        this.showImage = showImages;
        this.showValue = showValues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)    {
        try {
            // Get the data item for this position
            Wallet wallet = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_wallet, parent, false);
            }
            // Lookup view for data population
           // LinearLayout layWallet = (LinearLayout) convertView.findViewById(R.id.layWallet);
            ImageView imgQRCode = (ImageView) convertView.findViewById(R.id.imgQRCode);
            TextView txtCurrency = (TextView) convertView.findViewById(R.id.txtCurrency);
            TextView txtValue = (TextView) convertView.findViewById(R.id.txtValue);

            // Populate the data into the template view using the data object
            txtCurrency.setText(wallet.currency.toUpperCase());
            if (this.showValue) {
                txtValue.setVisibility(View.VISIBLE);
                    txtValue.setText(Double.toString(wallet.value));
            } else{
                txtValue.setVisibility(View.INVISIBLE);
            }
            try {
                if( this.showImage && !wallet.name.equals("") && (wallet.name.length() > 0)  ) {
                        QRCodeWriter qrCodeWriter = new QRCodeWriter();
                        BitMatrix bitMatrix = qrCodeWriter.encode(wallet.name, BarcodeFormat.QR_CODE, 1024, 1024);
                        imgQRCode.setImageBitmap(ImageHelper.BitmapFromBitMatrix(bitMatrix));
                } else {
                    imgQRCode.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.d(LOG_HEADER + ":ER",e.getMessage());
            }
            // Return the completed view to render on screen
        }catch (Exception ex){
            Log.d(LOG_HEADER + ":ER",ex.getMessage());
        }
        return convertView;
    }
}
