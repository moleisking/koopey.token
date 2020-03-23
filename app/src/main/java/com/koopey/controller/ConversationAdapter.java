package com.koopey.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.koopey.view.MainActivity;

/**
 * Created by Scott on 13/10/2016.
 */
public class ConversationAdapter extends ArrayAdapter<Message> {

    private final String LOG_HEADER = "CONVERSATION:ADAPTER";
    private AuthUser authUser;

    public ConversationAdapter(Context context, ArrayList<Message> messages, AuthUser authUser) {
        super(context, 0, messages);
        this.authUser = authUser;
    }

    public ConversationAdapter(Context context, Messages conversations, AuthUser authUser) {
        super(context, 0, conversations.getMessageList());
        this.authUser = authUser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Message message = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_conversation, parent, false);
        }

        // Lookup view for data population
        ImageView img = (ImageView) convertView.findViewById(R.id.imgAvatar);
        TextView txtId = (TextView) convertView.findViewById(R.id.txtId);
        TextView txtSummary = (TextView) convertView.findViewById(R.id.txtSummary);
        TextView txtAlias = (TextView) convertView.findViewById(R.id.txtAlias);

        // Populate the data into the template view using the data object
        txtSummary.setText(message.getTextSummary());

        // Select correct image and title
        Users users = message.users;
        for (int i = 0 ; i < users.size(); i++){
            User user = users.get(i);
            if (!user.equals(authUser)){
                img.setImageBitmap(ImageHelper.IconBitmap(user.avatar)  );
                // Set correct title
                if (message.users.size() > 2){
                    txtAlias.setText(user.alias + "++");
                } else {
                    txtAlias.setText(user.alias);
                }
            }
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
