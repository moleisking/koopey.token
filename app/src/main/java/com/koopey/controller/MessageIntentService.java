package com.koopey.controller;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Messages;


/**
 * Created by Scott on 17/01/2017.
 */
public class MessageIntentService extends IntentService implements GetJSON.GetResponseListener {
    private static final String LOG_HEADER = "MESSAGE:SERVICE";
    private static final int MESSAGE_NOTIFICATION = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private AuthUser authUser = new AuthUser();
    private Messages messages;
    // public ResponseMSG messageDelegate = null;

    public MessageIntentService() {
        super(MessageIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Log.d(LOG_HEADER, "start");
        Intent intent = new Intent(context, MessageIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Log.d(LOG_HEADER, "delete");
        Intent intent = new Intent(context, MessageIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_HEADER, "handle");
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                // processStartNotification();
                if (authUser != null) {
                    getMessages();
                }
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
        Log.d(LOG_HEADER, "process delete");
    }

    private void processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?
        Log.d(LOG_HEADER, "process start");
        //getMessages();
        //build notification
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Scheduled Notification")
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.color_background_light))
                .setContentText("This notification has been triggered by Notification Service")
                .setSmallIcon(R.drawable.ic_error_outline_black_24dp);
        //set delete action
        builder.setDeleteIntent(MessageReceiver.getDeleteIntent(this));
        //add to notification
        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(MESSAGE_NOTIFICATION, builder.build());
    }

    private void getMessages() {
        if (SerializeHelper.hasFile(this, AuthUser.AUTH_USER_FILE_NAME)) {
            this.authUser = (AuthUser) SerializeHelper.loadObject(getApplicationContext(), AuthUser.AUTH_USER_FILE_NAME);
            if (authUser != null && !authUser.isEmpty()) {
                Log.d(LOG_HEADER, "getMessages");
                String url = getResources().getString(R.string.get_message_read_many_undelivered);
                GetJSON asyncTask = new GetJSON(this.getApplication());
                // GetJSON asyncTask =new GetJSON(context);
                asyncTask.delegate = this;
                asyncTask.execute(url, "", this.authUser.getToken());
            }
        }
    }

    @Override
    public void onGetResponse(String output) {
        try {
            //Get user account
            this.authUser = new AuthUser();
            this.authUser = (AuthUser) SerializeHelper.loadObject(getApplicationContext(), AuthUser.AUTH_USER_FILE_NAME);
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            //Get JSON and add to object
            if (header.contains("messages")) {
                Log.d(LOG_HEADER, "Read undelivered");
                messages = new Messages();
                messages.parseJSON(output);
                messages.print();
                if (output.length() > 20) {
                    //New messages found
                    Log.d(LOG_HEADER, "New messages found");
                    processStartNotification();
                } else {
                    //No messages found
                    Log.d(LOG_HEADER, "No messages found");
                }
                //Pass new conversations to message fragment where they can be added to the list and file
                //messageDelegate.updateMessages(messages);
            } else if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    Log.d(LOG_HEADER + ":ER", getResources().getString(R.string.error_update));
                } else if (alert.isSuccess()) {
                    Log.d(LOG_HEADER, getResources().getString(R.string.info_update));
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public interface OnMessageListener {
        void updateMessages(Messages messages);
    }
}
