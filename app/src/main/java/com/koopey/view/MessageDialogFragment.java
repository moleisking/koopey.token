package com.koopey.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Message;
import com.koopey.model.User;
import com.koopey.model.Users;


/**
 * Created by Scott on 26/09/2017.
 * https://stackoverflow.com/questions/18579590/how-to-send-data-from-dialogfragment-to-a-fragment
 */

public class MessageDialogFragment extends DialogFragment implements PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "MSG:DG:";
    public OnMessageDialogFragmentListener delegate = (OnMessageDialogFragmentListener) getTargetFragment();

    private TextInputEditText txtMessage;
    private Button btnCancel, btnCreate, btnDelete;
    private Message message = new Message();
    private AuthUser authUser;
    private User user;
    private boolean showCreateButton = false;
    private boolean showUpdateButton = false;
    private boolean showDeleteButton = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_message));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            //Set listeners
            this.btnCancel.setOnClickListener(this);
            this.btnCreate.setOnClickListener(this);
            this.btnDelete.setOnClickListener(this);
            //Set message
            this.message = (Message) this.getActivity().getIntent().getSerializableExtra("message");
            this.setMessage();
            //Set buttons
            this.showCreateButton = this.getActivity().getIntent().getBooleanExtra("showCreateButton", false);
            this.showUpdateButton = this.getActivity().getIntent().getBooleanExtra("showUpdateButton", false);
            this.showDeleteButton = this.getActivity().getIntent().getBooleanExtra("showDeleteButton", false);
            this.setVisibility();
        } catch (Exception ex) {
            Log.w(LOG_HEADER, ":ER" + ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == this.btnCancel.getId()) {
                this.dismiss();
            } else if (v.getId() == this.btnCreate.getId()) {
                this.postMessage( this.buildMessage());
                ((OnMessageDialogFragmentListener) getTargetFragment()).createMessageDialogEvent(message);
                this.dismiss();
            } else if (v.getId() == this.btnDelete.getId()) {
                ((OnMessageDialogFragmentListener) getTargetFragment()).deleteMessageDialogEvent(message);
                this.dismiss();
             }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SerializeHelper.hasFile(this.getActivity(), AuthUser.AUTH_USER_FILE_NAME)) {
            this.authUser = (AuthUser) SerializeHelper.loadObject(this.getActivity(), AuthUser.AUTH_USER_FILE_NAME);
        }

        //Passed from AssetRead of UserRead
        if (getActivity().getIntent().hasExtra("user")) {
            this.user = (User) getActivity().getIntent().getSerializableExtra("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        getDialog().setTitle(getResources().getString(R.string.label_message));

        //Set controls
        this.txtMessage = (TextInputEditText) rootView.findViewById(R.id.txtMessage);
        this.btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        this.btnCreate = (Button) rootView.findViewById(R.id.btnCreate);
        this.btnDelete = (Button) rootView.findViewById(R.id.btnDelete);
        //this.delegate = (OnFeeDialogFragmentListener) getTargetFragment();
        return rootView;
    }

    @Override
    public void onResume() {
        //Sets the width to 90% of screen size
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        window.setLayout((int) (width * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
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
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    private Message buildMessage(){
        Message message = new Message();

        //Set message and flags
        message.text = this.txtMessage.getText().toString();
        message.sent = false;
        message.delivered = false;

        //Set Sender, getUserBasicWithAvatar returns as type basic
        User sender = this.authUser.getUserBasicWithAvatar();
        this.authUser.type = "sender";

        //Set receiver
        User receiver = this.user;
        receiver.type = "receiver";

        //Set user sender and receivers
        Users users  = new Users();
        users.add(receiver);
        users.add(sender);

        return message;
    }

    public void postMessage(Message message) {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_message), message.toString(), authUser.token);
    }

    /*@Override
    public void onDismiss(DialogInterface dialog) {
        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.onDismiss(dialog);
    }*/


    public void setMessage() {
        this.txtMessage.setText(this.message.text);
    }

    public void setVisibility() {
        if (this.showCreateButton) {
            this.btnCreate.setVisibility(View.VISIBLE);
        } else {
            this.btnCreate.setVisibility(View.GONE);
        }
               if (this.showDeleteButton) {
            this.btnDelete.setVisibility(View.VISIBLE);
        } else {
            this.btnDelete.setVisibility(View.GONE);
        }
    }

    public interface OnMessageDialogFragmentListener {
        void createMessageDialogEvent(Message message);

        void deleteMessageDialogEvent(Message message);
    }
}
