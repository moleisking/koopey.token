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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import com.koopey.R;
import com.koopey.common.CurrencyHelper;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GPSReceiver;
import com.koopey.controller.GetJSON;
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

public class SearchUsersFragment extends Fragment implements  GetJSON.GetResponseListener,  GPSReceiver.OnGPSReceiverListener, SeekBar.OnSeekBarChangeListener, PostJSON.PostResponseListener, View.OnClickListener  {

    private final String LOG_HEADER = "SEARCH:USERS";
    private ArrayAdapter<CharSequence> currencyCodeAdapter;
    private ArrayAdapter<CharSequence> currencySymbolAdapter;
    private TagTokenAutoCompleteView lstTags;
    private Tags tags;
    private Assets products;
    private Users users;
    private GPSReceiver gps;
    private LatLng currentLatLng = new LatLng(0.0d, 0.0d);
    private AuthUser myUser = new AuthUser();
    private EditText txtMin, txtMax;
    private TagAdapter tagAdapter;
    private FloatingActionButton btnSearch;
    private RadioGroup radGrpPeriod;
    private RadioButton optHour, optDay, optWeek, optMonth;
    private Spinner lstCurrency;
    private Search search = new Search();
    private SeekBar seeRadius;
    private TextView txtRadius;
    private int radius = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.lstTags = (TagTokenAutoCompleteView) getActivity().findViewById(R.id.lstTags);
        this.lstCurrency = (Spinner) getActivity().findViewById(R.id.lstCurrency);
        this.txtMin = (EditText) getActivity().findViewById(R.id.txtMin);
        this.txtMax = (EditText) getActivity().findViewById(R.id.txtMax);
        this.btnSearch = (FloatingActionButton) getActivity().findViewById(R.id.btnSearch);
        this.radGrpPeriod = (RadioGroup) getActivity().findViewById(R.id.radGrpPeriod);
        this.optHour = (RadioButton) getActivity().findViewById(R.id.optHour);
        this.optDay = (RadioButton) getActivity().findViewById(R.id.optDay);
        this.optWeek = (RadioButton) getActivity().findViewById(R.id.optWeek);
        this.optMonth = (RadioButton) getActivity().findViewById(R.id.optMonth);
        this.seeRadius= (SeekBar) getActivity().findViewById(R.id.seeRadius);
        this.txtRadius= (TextView) getActivity().findViewById(R.id.txtRadius);
        this.btnSearch.setOnClickListener(this);
        //txtMin.setMaxValue(5000);
        //txtMin.setMinValue(0);
        //txtMin.setValue(0);
        //txtMax.setMaxValue(5000);
        //txtMax.setMinValue(0);
        //txtMax.setValue(500);
        //Load data into fields
        this.populateCurrencies();
        this.populateTags();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_search));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onClick(View v) {
        this.buildSearch();
        this.postSearch();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.myUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        if (SerializeHelper.hasFile(this.getActivity(), Tags.TAGS_FILE_NAME)) {
            this.tags = (Tags) SerializeHelper.loadObject(this.getActivity(), Tags.TAGS_FILE_NAME);
            this.tagAdapter = new TagAdapter(this.getActivity(), this.tags, this.myUser.language);
        } else {
            ((MainActivity) getActivity()).getTags();
        }
        //Start GPS
        gps = new GPSReceiver(this.getActivity());
        gps.delegate = this;
        gps.Start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_search_tag, container, false);
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
                }
            } else if (header.contains("tags")) {
                Tags tags = new Tags();
                tags.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), tags);
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
        radius = progresValue;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        this.txtRadius.setText(" " + radius + "/" + seekBar.getMax() + getResources().getString(R.string.default_period));
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if(alert.isSuccess()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_LONG).show();
                } else if (alert.isError()){
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_LONG).show();
                }
            } else if (header.contains("users")) {
                this.users = new Users();
                this.users.parseJSON(output);
                if (this.users.size() == 0) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_no_results), Toast.LENGTH_LONG).show();
                } else {
                    SerializeHelper.saveObject(this.getActivity(), this.users);
                    ((MainActivity) getActivity()).showUserListFragment();
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onGPSConnectionResolutionRequest(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this.getActivity(), GPSReceiver.OnGPSReceiverListener.CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":GPS", ex.getMessage());
        }
    }

    @Override
    public void onGPSWarning(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGPSPositionResult(LatLng position) {
        this.currentLatLng = position;
        gps.Stop();
        Log.d(LOG_HEADER + ":GPS", position.toString());
    }

    private void populateCurrencies() {
        this.currencyCodeAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_codes, android.R.layout.simple_spinner_item);
        this.currencySymbolAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_symbols, android.R.layout.simple_spinner_item);
        this.currencySymbolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.lstCurrency.setAdapter(currencySymbolAdapter);
    }

    private void populateTags() {
        this.tagAdapter = new TagAdapter(this.getActivity(), this.tags, this.myUser.language);
        this.lstTags.allowDuplicates(false);
        this.lstTags.setAdapter(this.tagAdapter);
        this.lstTags.setTokenLimit(15);
    }

    private void buildSearch() {
        this.search.currency = CurrencyHelper.currencySymbolToCode(lstCurrency.getSelectedItem().toString());
        this.search.radius = getResources().getInteger(R.integer.default_radius);
        this.search.min = Integer.valueOf(this.txtMin.getText().toString());
        this.search.max = Integer.valueOf(this.txtMax.getText().toString());
        this.search.latitude = this.currentLatLng.latitude;//40.4101013; //
        this.search.longitude = this.currentLatLng.longitude;//-3.705122299999971;//
        this.search.measure = this.myUser.measure;
        this.search.type = "users";
        this.search.tags.setTagList(lstTags.getObjects());
        if (this.radGrpPeriod.getCheckedRadioButtonId() == this.optHour.getId()) {
            this.search.period = "hour";
        } else if (this.radGrpPeriod.getCheckedRadioButtonId() == this.optDay.getId()) {
            this.search.period = "day";
        } else if (this.radGrpPeriod.getCheckedRadioButtonId() == this.optWeek.getId()) {
            this.search.period = "week";
        } else if (this.radGrpPeriod.getCheckedRadioButtonId() == this.optMonth.getId()) {
            this.search.period = "month";
        }
    }

    private void postSearch() {
       /* PostJSON asyncTask = new PostJSON();
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_user_tag_search), search.toString(), myUser.getToken());*/
    }
}
