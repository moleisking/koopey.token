package com.koopey.view;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.koopey.R;
import com.koopey.model.AuthUser;
import com.koopey.model.File;


/**
 * Created by Scott on 09/11/2017.
 */

public class FileReadFragment extends Fragment {
    private final String LOG_HEADER = "FILE:READ:FRAG:";
    private EditText txtName, txtDescription, txtType, txtSize;
    private AuthUser authUser = new AuthUser();
    private File file = new File();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Define objects
        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();
        this.file = (File) getActivity().getIntent().getSerializableExtra("file");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_read, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.txtName = (EditText) getActivity().findViewById(R.id.txtName);
        this.txtDescription = (EditText) getActivity().findViewById(R.id.txtDescription);
        this.txtType = (EditText) getActivity().findViewById(R.id.txtType);
        this.txtSize = (EditText) getActivity().findViewById(R.id.txtSize);
        this.populateFile();
    }

    private void populateFile() {
        try {
            this.txtName.setText(file.name);
            this.txtDescription.setText(String.valueOf(file.description));
            this.txtType.setText(String.valueOf(file.type));
            this.txtSize.setText(String.valueOf(file.size));
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
