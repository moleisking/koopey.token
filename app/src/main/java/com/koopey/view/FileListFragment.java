package com.koopey.view;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.FileAdapter;
import com.koopey.model.File;
import com.koopey.model.Files;

/**
 * Created by Scott on 09/11/2017.
 */

public class FileListFragment extends ListFragment implements View.OnClickListener {
    private final String LOG_HEADER = "FILE:LIST";
    private Files files = new Files();
    ;
    private FloatingActionButton btnCreate;
    private FileAdapter fileAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new FileAdapter(this.getActivity(), files));
        this.btnCreate = (FloatingActionButton) getActivity().findViewById(R.id.btnCreate);
        this.btnCreate.setVisibility(View.VISIBLE);
        this.btnCreate.setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_files));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getIntent().hasExtra("files")) {
            this.files = (Files) getActivity().getIntent().getSerializableExtra("files");
        } else if (SerializeHelper.hasFile(this.getActivity(), Files.FILES_NAME)) {
            this.files = (Files) SerializeHelper.loadObject(this.getActivity(), Files.FILES_NAME);
        } else {
            this.files = new Files();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_files, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try {
            File file = files.get(position);
            ((MainActivity) getActivity()).showFileReadFragment(file);
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == btnCreate.getId()) {
                ((MainActivity) getActivity()).showTransactionCreateFragment();
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }
}
