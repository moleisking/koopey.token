package com.koopey.view;

/**
 * Created by Scott on 12/08/2016.
 */

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.ConversationAdapter;
import com.koopey.controller.GetJSON;
import com.koopey.controller.MessageIntentService;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.Message;
import com.koopey.model.Messages;
import com.koopey.model.AuthUser;
import com.koopey.model.Users;

//import android.support.v4.app.Fragment;

public class ConversationListFragment extends ListFragment implements GetJSON.GetResponseListener, MessageIntentService.OnMessageListener {

    private final String LOG_HEADER = "CONVERSATION:LIST";
    private AuthUser authUser;
    private Messages conversations = new Messages();
    private Messages messages = new Messages();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.syncConversations();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_conversations));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversations, container, false);
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
            } else if (header.contains("messages")) {
                //TODO: Messages can be readall or readallunsent
                Messages temp = new Messages();
                temp.parseJSON(output);
                if (temp.size() > 0) {
                    this.messages.add(temp);
                    this.populateConversations();
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

        if (this.messages.size() > 0) {
            // Save conversation users for MessageList
            getActivity().getIntent().putExtra("users", this.messages.get(position).users);
            ((MainActivity) getActivity()).showMessageListFragment();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void updateMessages(Messages conversations) {
        Log.w("Conversations", "updateConversations");
        conversations.print();
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

    private boolean isDuplicateConversation(Message message) {
        boolean duplicate = false;
        for (int i = 0; i < this.conversations.size(); i++) {
            if (Users.equals(message.users, this.conversations.get(i).users)) {
                duplicate = true;
                break;
            }
        }
        return duplicate;
    }

    private void populateConversations() {
        if (this.messages != null && this.messages.size() > 0 ) {

            // Filter conversations
            this.conversations =  new Messages();
            for (int i = 0; i < this.messages.size(); i++) {
                Message message = messages.get(i);
                if (!isDuplicateConversation(message)) {
                    this.conversations.add(message);
                } else {
                    Log.w(LOG_HEADER + ":ER","No match");
                }
            }

            // Reset adapter with new conversations
            ConversationAdapter conversationsAdapter = new ConversationAdapter(this.getActivity(), this.conversations, this.authUser);
            this.setListAdapter(conversationsAdapter);
        } else {
            this.messages = new Messages();
            Log.w(LOG_HEADER + ":ER","No messages");
        }
    }

    protected void syncConversations() {
        //NOTE: MainActivity refresh and this.onActivityCreated
        if (SerializeHelper.hasFile(this.getActivity(), Messages.MESSAGES_FILE_NAME)) {
            //Messages found so try read unsent messages
            this.messages = (Messages) SerializeHelper.loadObject(this.getActivity(), Messages.MESSAGES_FILE_NAME);
            this.populateConversations();
            this.getMessages();// TODO: this.getMessagesUnsent();
        } else {
            //No messages found so try read all messages
            this.messages = new Messages();
            this.getMessages();
        }
    }


}
