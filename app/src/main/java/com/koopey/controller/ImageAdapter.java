package com.koopey.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.koopey.R;
import com.koopey.model.Image;
import com.koopey.model.Images;

/**
 * Created by Scott on 06/10/2017.
 */

public class ImageAdapter extends ArrayAdapter<Image> {
    private final String LOG_HEADER = "IMG:ADT";

    public ImageAdapter(Context context, Images images) {
        super(context, 0, images.get());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try {
            // Get the data item for this position
            Image image = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_image, parent, false);
            }
            // Lookup view for data population
            ImageView img = (ImageView) convertView.findViewById(R.id.img);
           // Switch btnPrimary = (Switch)convertView.findViewById(R.id.btnPrimary);
            // Populate the data into the template view using the data object
            img.setImageBitmap(image.getBitmap());

            // Return the completed view to render on screen
        }catch (Exception ex){
            Log.d(LOG_HEADER + ":ER",ex.getMessage());
        }
        return convertView;
    }
}
