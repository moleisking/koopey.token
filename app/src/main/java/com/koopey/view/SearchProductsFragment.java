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
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLng;
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
import com.koopey.model.Users;

public class SearchProductsFragment extends Fragment implements  GetJSON.GetResponseListener,  GPSReceiver.OnGPSReceiverListener, PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "SEARCH:PRODUCTS";
    private ArrayAdapter<CharSequence> currencyCodeAdapter;
    private ArrayAdapter<CharSequence> currencySymbolAdapter;
    private FloatingActionButton btnSearch;
    private GPSReceiver gps;
    private TagTokenAutoCompleteView lstTags;
    private Tags tags;
    private Assets products;
    private Users users;
    private LatLng currentLatLng;
    private AuthUser myUser = new AuthUser();
    private EditText txtMin, txtMax;
    private TagAdapter tagAdapter;
    private Spinner lstCurrency;
    private Search search = new Search();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
              this.lstCurrency = (Spinner) getActivity().findViewById(R.id.lstCurrency);
        this.lstTags = (TagTokenAutoCompleteView) getActivity().findViewById(R.id.lstTags);
        this.txtMin = (EditText) getActivity().findViewById(R.id.txtMin);
        this.txtMax = (EditText) getActivity().findViewById(R.id.txtMax);
        this.btnSearch = (FloatingActionButton) getActivity().findViewById(R.id.btnSearch);
        //txtSearch = (TextView)container.findViewById(R.id.txtName);
        //radRadius = (RadioGroup) container.findViewById(R.id.radRadius);
        //Set object configurations
        this.btnSearch.setOnClickListener(this);
        this.populateCurrencies();
        this.lstTags.setLanguage(this.myUser.language);
        this.lstTags.setAdapter(tagAdapter);
        this.lstTags.allowDuplicates(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_search));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onClick(View v) {
        this.search.currency = CurrencyHelper.currencySymbolToCode(lstCurrency.getSelectedItem().toString());
        this.search.radius = getResources().getInteger(R.integer.default_radius);
        this.search.min = Integer.valueOf(this.txtMin.getText().toString());
        this.search.max = Integer.valueOf(this.txtMax.getText().toString());
        this.search.latitude = this.currentLatLng.latitude;
        this.search.longitude = this.currentLatLng.longitude;
        this.search.type = "Products";
        this.search.tags.setTagList(lstTags.getObjects());
        this.postSearch();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Define basic objects
        this.myUser = ((MainActivity) getActivity()).getAuthUserFromFile();
        //Define tags
        if (SerializeHelper.hasFile(this.getActivity(), tags.TAGS_FILE_NAME)) {
            this.tags = (Tags) SerializeHelper.loadObject(this.getActivity(), Tags.TAGS_FILE_NAME);
            this.tagAdapter = new TagAdapter(this.getActivity(), this.tags, this.myUser.language);
        } else {
            ((MainActivity) getActivity()).getTags();
        }
        //Start GPS
        currentLatLng = new LatLng(0.0d, 0.0d);
        gps = new GPSReceiver(this.getActivity());
        gps.delegate = this;
        gps.Start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_search, container, false);
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
                this.tags = new Tags();
                this.tags.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), tags);
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
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
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

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("products")) {
                this.products = new Assets();
                this.products.parseJSON(output);
                if (products.size() == 0) {
                    Toast.makeText(this.getActivity(), "No results", Toast.LENGTH_LONG).show();
                } else {
                    getActivity().getIntent().putExtra("products", this.products);
                    ((MainActivity) getActivity()).showAssetListFragment();
                }
            }  else if (header.contains("alert")) {
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

    private void populateCurrencies() {
        this.currencyCodeAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_codes, android.R.layout.simple_spinner_item);
        this.currencySymbolAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currency_symbols, android.R.layout.simple_spinner_item);
        this.currencySymbolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.lstCurrency.setAdapter(currencySymbolAdapter);
    }

    private void postSearch() {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_asset_search), search.toString(), myUser.getToken());
    }
}
