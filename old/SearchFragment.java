package me.minitrabajo.view;

/*
*  private FloatingActionButton btnSearch; not necessary due to events being passed back to MainActivity in onFragmentViewClick
* */
import android.os.Bundle;
import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;

import me.minitrabajo.R;
import me.minitrabajo.common.Utility;
import me.minitrabajo.controller.PostJSON;
import me.minitrabajo.controller.TagAdapter;
import me.minitrabajo.controller.GPS;
import me.minitrabajo.controller.ResponseAPI;
import me.minitrabajo.controller.ResponseGPS;
import me.minitrabajo.model.Products;
import me.minitrabajo.model.Tags;
import me.minitrabajo.model.Tag;
import me.minitrabajo.model.MyUser;
import me.minitrabajo.model.Users;

public class SearchFragment extends Fragment implements ResponseAPI, ResponseGPS , View.OnClickListener
{
    private final String LOG_HEADER = "SH:URS:FT";
    private RadioGroup lstProductOrService;
    private TagTokenAutoCompleteView lstTags;
    private Tags tags;
    private Products products;
    private Users users;
    private GPS gps;
    private LatLng currentLatLng;
    private MyUser myUser = new MyUser();
    private NumberPicker txtMin, txtMax;
    private TagAdapter tagAdapter;
    private FloatingActionButton btnSearch;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Define basic objects
        myUser = ((MainActivity)getActivity()).getMyUserFromFile();
        //Define tags
        if(Utility.hasFile(this.getActivity() ,Tags.TAGS_FILE_NAME))
        {
            this.tags =  (Tags)Utility.loadObject(this.getActivity() ,Tags.TAGS_FILE_NAME);
        }
        else
        {
            Log.d(LOG_HEADER + "fillTags", "Tags not found");
        }
        //Add all tags to TagAdaptor control
        this.tagAdapter = new TagAdapter(this.getActivity(),  this.tags, this.myUser.language );
        //Start GPS
        currentLatLng = new LatLng(0.0d,0.0d);
        gps = new GPS(this.getActivity());
        gps.delegate = this;
        gps.Start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //Initialize objects
        lstProductOrService = (RadioGroup)getActivity().findViewById(R.id.rdolstProductOrService);
        lstTags = (TagTokenAutoCompleteView)getActivity().findViewById(R.id.lstTags);
        txtMin = (NumberPicker)getActivity().findViewById(R.id.txtMin);
        txtMax = (NumberPicker)getActivity().findViewById(R.id.txtMax);
        btnSearch = (FloatingActionButton)getActivity().findViewById(R.id.btnSearch);
        //txtSearch = (TextView)container.findViewById(R.id.txtName);
        //radRadius = (RadioGroup) container.findViewById(R.id.radRadius);
        //Set object configurations
        btnSearch.setOnClickListener(this);
        lstTags.setAdapter(tagAdapter);
        lstTags.allowDuplicates(false);
        txtMin.setMaxValue(5000);
        txtMin.setMinValue(0);
        txtMin.setValue(0);
        txtMax.setMaxValue(5000);
        txtMax.setMinValue(0);
        txtMax.setValue(500);
    }

    @Override
    public void processFinish(String output)
    {
        final String LOG_FUNCTION = LOG_HEADER + ":PS";
        Log.w(LOG_HEADER + ":PS", output);
        try
        {
            String header = output.substring(0,10).toLowerCase();
            if(header.contains("users")) {
                //Get JSON and add to object
                JSONObject myJson = new JSONObject(output);
                users = new Users();
                users.parseJSON(myJson.toString());

                if (users.size() == 0) {
                    Toast.makeText(this.getActivity(), "No results", Toast.LENGTH_LONG).show();
                } else {
                    //Pass users to list, then load list
                    Utility.saveObject(this.getActivity(), users);
                    //Move to list fragment
                    ((MainActivity) getActivity()).showUsersFragment();
                }
                Log.w(LOG_FUNCTION, "Print");
                users.print();
            }
            else if(header.contains("products")) {
                //Get JSON and add to object
                JSONObject myJson = new JSONObject(output);
                products = new Products();
                products.parseJSON( myJson.toString());

                if(products.size() == 0)
                {
                    Toast.makeText(this.getActivity(),"No results",Toast.LENGTH_LONG).show();
                }
                else
                {
                    //Pass users to list, then load list
                    Utility.saveObject(this.getActivity(), products);
                    //Move to list fragment
                    ((MainActivity)getActivity()).showProductsFragment();
                }
                Log.w(LOG_FUNCTION, "Print");
                products.print();
            }
        }
        catch (Exception ex)
        {
            Log.w(LOG_FUNCTION + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onGPSConnectionResolutionRequest(ConnectionResult connectionResult )
    {
        try {
            connectionResult.startResolutionForResult(this.getActivity(), ResponseGPS.CONNECTION_FAILURE_RESOLUTION_REQUEST);
        }catch (Exception ex){Log.d(LOG_HEADER + ":ON:GPS:F",ex.getMessage());}
    }

    @Override
    public void onGPSWarning(String message)
    {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGPSPositionResult(LatLng position)
    {
        this.currentLatLng = position;
        gps.Stop();
        Log.d(LOG_HEADER + ":ON:GPS:POS",position.toString());
    }

    @Override
    public void onClick(View v) {
        if(lstProductOrService.getCheckedRadioButtonId() == R.id.rdoProduct){
            Log.d(LOG_HEADER + ":ON:SH:CK", "Product search clicked");
            onSearchProductsClick();

        }
        else if(lstProductOrService.getCheckedRadioButtonId() == R.id.rdoService){
            Log.d(LOG_HEADER + ":ON:SH:CK", "Service search clicked");
            onSearchUsersClick();
        }
    }

    private void onSearchUsersClick()
    {
        //Tags selectedTags = new Tags();
       // selectedTags.setTagList(lstTags.getObjects());

        //Searches only registered location
        Log.d("Search:onSearchClick()","Post");
        //String url = getResources().getString(R.string.url_post_user_search);
        //tagsActive = tagsActive.length() > 0 ? tagsActive.substring(0,tagsActive.length()-1) : tagsActive;
        //String parameters = "tags="+ selectedTags.toJSONTagIds() + "&radius="+ 5 + "&regLat=" + currentLatLng.latitude + "&regLng=" + currentLatLng.longitude + "&curLat=" + currentLatLng.latitude + "&curLng=" + currentLatLng.longitude + "&min=" + txtMin.getValue() + "&max=" + txtMax.getValue();

        PostJSON asyncTask =new  PostJSON();
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.url_post_user_search),this.searchToJSONObject(),myUser.getToken());
    }

    private void onSearchProductsClick()
    {
        //Tags selectedTags = new Tags();
        //selectedTags.setTagList(lstTags.getObjects());

        Log.d("Search:onSearchClick()","Post");
        //String url = getResources().getString(R.string.url_post_product_search);
       // tagsActive = tagsActive.length() > 0 ? tagsActive.substring(0,tagsActive.length()-1) : tagsActive;
       // String parameters = "tags="+ selectedTags.toJSONTagIds() + "&radius="+ 5 + "&curLat=" + currentLatLng.latitude + "&curLng=" + currentLatLng.longitude + "&min=" + txtMin.getValue() + "&max=" + txtMax.getValue();

        PostJSON asyncTask =new  PostJSON();
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.url_post_product_search),this.searchToJSONObject(),myUser.getToken());
    }

    public String searchToJSONObject() {
        final String LOG_FUNCTION = LOG_HEADER + ":TO:JSON:OBJ:ER";
        JSONObject jsonObject = new JSONObject();
        try {
            //Strings
            Tags selectedTags = new Tags();
            selectedTags.setTagList(lstTags.getObjects());
            jsonObject.put("radius", 5);
            if (selectedTags.size() > 0) {
               // jsonObject.put("tagIds", selectedTags.toJSONTagIds());
                jsonObject.put("tags", selectedTags.toJSONArray());
            }
            if (this.currentLatLng.latitude != 0) {
                jsonObject.put("curLat", this.currentLatLng.latitude);
            }
            if (this.currentLatLng.longitude != 0) {
                jsonObject.put("curLng", this.currentLatLng.longitude);
            }
            if (this.txtMin.getValue() > 0) {
                jsonObject.put("min", this.txtMin.getValue());
            }
            if (this.txtMax.getValue() > 0) {
                jsonObject.put("max", this.txtMax.getValue());
            }
        } catch (Exception ex) {
            Log.v(LOG_FUNCTION, ex.getMessage());
        }
        return jsonObject.toString();
    }

}
