package com.koopey.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.koopey.R;
import com.koopey.common.DistanceHelper;
import com.koopey.common.ImageHelper;
import com.koopey.model.*;
import com.koopey.view.MainActivity;
import com.koopey.view.TagTokenAutoCompleteView;

//import static com.koopey.R.id.txtName;

/**
 * Created by Scott on 12/10/2016.
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 */
public class UserAdapter extends ArrayAdapter<User> {

    private final String LOG_HEADER = "USER:ADAPTER";

    public UserAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    public UserAdapter(Context context, Users users) {
        super(context, 0, users.getList());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            // Get the data item for this position
            User user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_user, parent, false);
            }
            // Lookup view for data population
            //TagTokenAutoCompleteView lstTags= (TagTokenAutoCompleteView) convertView.findViewById(R.id.lstTags);
            TextView txtDistance = (TextView) convertView.findViewById(R.id.txtDistance);
            TextView txtAlias = (TextView) convertView.findViewById(R.id.txtAlias);
            TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
            ImageView img = (ImageView) convertView.findViewById(R.id.imgAvatar);
            // Populate the data into the template view using the data object
            //lstTags.allowDuplicates(false);
            //lstTags.setFocusable(false) ;
            //lstTags.setClickable(false);
            //lstTags.clear();
            //Add existing selected tags to control
            //for(Tag t : user.tags.getList()) {
            //    lstTags.addObject(t);
            //}
            txtDistance.setText(DistanceHelper.DistanceToKilometers(user.distance));
            txtAlias.setText(user.alias);
            txtName.setText(user.name);
            try {
                if (user.avatar != null && user.avatar.length() > 0 ) {
                    img.setImageBitmap(ImageHelper.UriToBitmap( user.avatar));
                } else {
                    img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.default_user));
                }
            } catch (Exception iex) {
                Log.d(LOG_HEADER + ":IMG", "Image not loaded");
            }
            if (this.getContext().getResources().getBoolean(R.bool.alias)) {
                txtAlias.setVisibility(View.VISIBLE);
            } else {
                txtAlias.setVisibility(View.INVISIBLE);
            }
            if (this.getContext().getResources().getBoolean(R.bool.name)) {
                txtName.setVisibility(View.VISIBLE);
            } else {
                txtName.setVisibility(View.INVISIBLE);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER, ex.getMessage());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
