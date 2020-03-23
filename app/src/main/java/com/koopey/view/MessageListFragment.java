package com.koopey.view;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.ImageHelper;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GetJSON;
import com.koopey.controller.MessageAdapter;
import com.koopey.controller.MessageIntentService;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.Message;
import com.koopey.model.Messages;
import com.koopey.model.AuthUser;
import com.koopey.model.User;
import com.koopey.model.Users;

/**
 * Created by Scott on 13/10/2016.
 */
public class MessageListFragment extends ListFragment implements GetJSON.GetResponseListener, PostJSON.PostResponseListener, MessageIntentService.OnMessageListener,  View.OnKeyListener{

    private final String LOG_HEADER = "MESSAGE:LIST";
    private AuthUser authUser;
    private Messages conversation = new Messages();
    private Messages messages = new Messages();
    private Message message = new Message();
    private TextView txtMessage;
    private Users users;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Define views
        this.txtMessage = (TextView) getActivity().findViewById(R.id.txtMessage);

        //Set listeners
        this.txtMessage.setOnKeyListener(this);

        //Populate controls
        this.syncConversation();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_messages));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        //Message users from ConversationListFragment
        if (getActivity().getIntent().hasExtra("users")) {
            this.users = (Users) getActivity().getIntent().getSerializableExtra("users");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onGetResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                } else if (alert.isSuccess()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_SHORT).show();
                }
            } else if (output.contains("messages")) {
                //TODO: Messages can be readall or readallunsent
                Messages temp = new Messages();
                temp.parseJSON(output);
                if (temp.size() > 0) {
                    this.messages.add(temp);
                    this.populateConversation();
                    SerializeHelper.saveObject(this.getActivity(), this.messages);
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                } else if (alert.isSuccess()) {
                    //On successful message post
                    this.messages.add(this.message);
                    this.populateConversation();
                    SerializeHelper.saveObject(this.getActivity(), this.messages);
                    //Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_SHORT).show();
                }
            } else if (output.contains("messages")) {
                Messages temp = new Messages();
                temp.parseJSON(output);
                if (temp.size() > 0) {
                    this.messages.add(temp);
                    this.populateConversation();
                    SerializeHelper.saveObject(this.getActivity(), this.messages);
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        super.onListItemClick(l, view, position, id);
    }

    @Override
    public boolean onKey( View view, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            //Create message, Id and createTimeStamp already generated
            this.buildMessage();

            //Post message to backend
            this.postMessage(this.message);

            //Reset local objects and txtMessage
            this.txtMessage.setText("");

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateMessages(Messages conversations) {
        Log.w("Conversations", "updateConversations");
        conversations.print();
        // this.messages.addAll(conversations);
        Log.w("Conversations:after", "updateConversations");
        this.messages.print();
    }

    private Message buildMessage() {
        //Reset message object
        this.message = new Message();
        this.message.text = txtMessage.getText().toString();

        //Set flags
        this.message.sent = false;
        this.message.delivered = false;

        //Set user sender and receivers
        for (int i = 0; i < this.users.size(); i++) {
            User user = this.users.get(i);
            if (this.authUser.equals(user)) {
                user.type = "sender";
                this.users.set(user);
            } else {
                user.type = "receiver";
                this.users.set(user);
            }
        }
        this.message.users = this.users;

        return message;
    }

    public void getMessages() {
        GetJSON asyncTask = new GetJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.get_message_read_many), "", authUser.getToken());
    }

    public void getMessagesUnsent() {
        GetJSON asyncTask = new GetJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.get_message_read_many_unsent), "", authUser.getToken());
    }

    public void getMessagesUndelivered() {
        GetJSON asyncTask = new GetJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.get_message_read_many_undelivered), "", authUser.getToken());
    }

    private void populateConversation() {
        if (this.messages != null && this.messages.size() > 0 ) {
            // Filter conversation
            this.conversation =  new Messages();
            for (int i = 0; i < this.messages.size(); i++) {
                Message message = this.messages.get(i);
                if (Users.equals(this.users, message.users)) {
                    this.conversation.add(message);
                }
            }

            // Reset adapter with new messages
            MessageAdapter conversationAdapter = new MessageAdapter(this.getActivity(), this.conversation, this.authUser);
            this.setListAdapter(conversationAdapter);
        }
    }

    public void postMessage(Message message) {
        if (message != null) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            asyncTask.delegate = this;
            asyncTask.execute(getResources().getString(R.string.post_message), message.toString(), authUser.token);
        }
    }

    protected void syncConversation() {
        if (SerializeHelper.hasFile(this.getActivity(), Messages.MESSAGES_FILE_NAME) && this.users != null && this.users.size() > 0 ) {

            //Messages found so try read unsent messages
            this.messages = (Messages) SerializeHelper.loadObject(this.getActivity(), Messages.MESSAGES_FILE_NAME);
            this.populateConversation();
            this.getMessages();// TODO:  this.getMessagesUnsent();
        } else {
            //No messages found so try read all messages
            this.messages = new Messages();
            this.getMessages();
        }
    }
}
