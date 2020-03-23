package com.koopey.view;

/*
*  private FloatingActionButton btnSearch; not necessary due to events being passed back to MainActivity in onFragmentViewClick
* */

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GPSReceiver;
import com.koopey.controller.PostJSON;
import com.koopey.controller.TagAdapter;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Assets;
import com.koopey.model.Search;
import com.koopey.model.Tags;
import com.koopey.model.Transaction;
import com.koopey.model.Users;

//import org.florescu.android;

//import android.support.v4.app.Fragment;

public class SearchUserFragment extends Fragment implements PostJSON.PostResponseListener, View.OnClickListener {
    private final String LOG_HEADER = "SEARCH:USER";

    private TagTokenAutoCompleteView lstTags;
    private Tags tags;
    private Search search = new Search();
    private Users users ;
    private AuthUser myUser = new AuthUser();
    private EditText txtAlias, txtName;
    private FloatingActionButton btnSearch;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.lstTags = (TagTokenAutoCompleteView) getActivity().findViewById(R.id.lstTags);
        this.txtName = (EditText) getActivity().findViewById(R.id.txtName);
        this.txtAlias = (EditText) getActivity().findViewById(R.id.txtAlias);
        this.btnSearch = (FloatingActionButton) getActivity().findViewById(R.id.btnSearch);
        this.btnSearch.setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_search));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.myUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        if (SerializeHelper.hasFile(this.getActivity(), Tags.TAGS_FILE_NAME)) {
            this.tags = (Tags) SerializeHelper.loadObject(this.getActivity(), Tags.TAGS_FILE_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_search_name, container, false);
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = output.substring(0, 10).toLowerCase();
            if (header.contains("users")) {
                //Get JSON and add to object
                JSONObject myJson = new JSONObject(output);
                this.users = new Users();
                this.users.parseJSON(myJson.toString());

                if (this.users.size() == 0) {
                    Toast.makeText(this.getActivity(), "No results", Toast.LENGTH_LONG).show();
                } else {
                    //Pass users to list, then load list
                    SerializeHelper.saveObject(this.getActivity(), this.users);
                    //Move to list fragment
                    ((MainActivity) getActivity()).showUserListFragment();
                }
            } else if (header.contains("tags")) {
                Tags tags = new Tags();
                tags.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), tags);
            } else if (header.contains("alert")) {
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
        search.alias =  this.txtAlias.getText().toString();
        search.name= this.txtName.getText().toString();
        search.type = "Users";
        this.postSearch();
    }

    private void postSearch(){
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_user_read_many), search.toString(), myUser.getToken());
    }
}
