package com.koopey.view;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.Tag;

/**
 * Created by Scott on 07/10/2016.
 */
public class TagCreateFragment extends Fragment implements PostJSON.PostResponseListener, View.OnClickListener {
    private final String LOG_HEADER = "TG:FT";
    private TextView txtWord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_create, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.txtWord = (TextView) getActivity().findViewById(R.id.txtName);
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

    @Override
    public void onClick(View v) {
       /* Log.d("Tag:onSearchClick()", "Post");
        String url = getResources().getString(R.string.post_user_tag_search);
        Tag tag = new Tag();
        tag.en = txtWord.getText().toString();
        PostJSON asyncTask = new PostJSON();
        asyncTask.delegate = this;
        asyncTask.execute(url, tag.toString(), "");*/
    }

}
