package com.koopey.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koopey.R;
import com.koopey.model.*;


/**
 * Created by Scott on 16/02/2017.
 */
public class FileAdapter extends ArrayAdapter<File> {
    private final String LOG_HEADER = "FILE:ADAPTER";

    public FileAdapter(Context context, Files files) {
        super(context, 0, files.get());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try {
            // Get the data item for this position
            File file = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_file, parent, false);
            }
            // Lookup view for data population
            TextView txtId = (TextView) convertView.findViewById(R.id.txtId);
            TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
            TextView txtSize = (TextView) convertView.findViewById(R.id.txtSize);
            // Populate the data into the template view using the data object
            txtId.setText(file.id);
            txtName.setText(file.name);
            txtSize.setText(Double.toString( file.size));
            // Return the completed view to render on screen
        }catch (Exception ex){
            Log.d(LOG_HEADER + ":ER",ex.getMessage());
        }
        return convertView;
    }
}
