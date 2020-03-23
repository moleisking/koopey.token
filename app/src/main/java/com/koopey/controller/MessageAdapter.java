package com.koopey.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.koopey.R;
import com.koopey.common.ImageHelper;
import com.koopey.model.AuthUser;
import com.koopey.model.Image;
import com.koopey.model.Message;
import com.koopey.model.Messages;
import com.koopey.model.User;
import com.koopey.model.Users;

/**
 * Created by Scott on 13/10/2016.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    private final String LOG_HEADER = "MSG:ADP";
    private AuthUser authUser;

    public MessageAdapter(Context context, ArrayList<Message> messages, AuthUser authUser) {
        super(context, 0, messages);
        this.authUser = authUser;
    }

    public MessageAdapter(Context context, Messages messages, AuthUser authUser) {
        super(context, 0, messages.getMessageList());
        this.authUser = authUser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Message message = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_message, parent, false);
        }
        LinearLayout rowLinearLayout = (LinearLayout) convertView.findViewById(R.id.rowMessage);
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtItemMessage);
        ImageView imgAvatar = (ImageView) convertView.findViewById(R.id.imgAvatar);

        User sender = message.getSender();
        Users receivers = message.getReceivers();

        //Set control data
        if (sender != null && sender.equals(authUser) ) {
            //Set indentation, sender is my user
            rowLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            //Set text
            txtMessage.setBackgroundColor(getContext().getResources().getColor(R.color.color_background_light));
            txtMessage.setText(message.text);
            //Set image
            if (ImageHelper.isImageUri(authUser.avatar)){
                imgAvatar.setImageBitmap(ImageHelper.IconBitmap(authUser.avatar));
            }else {
                Bitmap defaultBitmap = ((BitmapDrawable)getContext().getResources().getDrawable(R.drawable.default_user)).getBitmap();
                imgAvatar.setImageBitmap(ImageHelper.IconBitmap(defaultBitmap));
            }
        } else  {
            //Set indentation, sender is not my user
            rowLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            txtMessage.setBackgroundColor(getContext().getResources().getColor(R.color.color_background_dark));
            txtMessage.setText(message.text);
            //Set image
            if (sender != null  && ImageHelper.isImageUri(sender.avatar)) {
                imgAvatar.setImageBitmap(ImageHelper.IconBitmap(sender.avatar));
            }else {
                Bitmap defaultBitmap = ((BitmapDrawable)getContext().getResources().getDrawable(R.drawable.default_user)).getBitmap();
                imgAvatar.setImageBitmap(ImageHelper.IconBitmap(defaultBitmap));
            }
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
