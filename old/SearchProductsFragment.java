package me.minitrabajo.view;

/**
 * Created by Scott on 18/01/2017.
 */

import android.os.Bundle;
import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;

import me.minitrabajo.R;
import me.minitrabajo.common.Utility;
import me.minitrabajo.controller.TagAdapter;
import me.minitrabajo.controller.GPS;
import me.minitrabajo.controller.PostForm;
import me.minitrabajo.controller.ResponseAPI;
import me.minitrabajo.controller.ResponseGPS;
import me.minitrabajo.model.Products;
import me.minitrabajo.model.Tags;
import me.minitrabajo.model.Tag;
import me.minitrabajo.model.MyUser;
import me.minitrabajo.model.Users;

public class SearchProductsFragment extends Fragment implements ResponseAPI, ResponseGPS {
    private MultiAutoCompleteTextView txtTag;
    //private RadioGroup radRadius;
    private Tags tags;
    private Products products;
    private GPS gps;
    private LatLng currentLatLng;
    private MyUser userAccount;
    private String tagsActive = "";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Define myUser
        userAccount = new MyUser();
        userAccount = ((MainActivity)getActivity()).getUserAccount();

        //Define tags
        tags = new Tags();
        if(Utility.hasFile(this.getActivity() ,tags.TAGS_FILE_NAME))
        {
            tags =  (Tags)Utility.loadObject(this.getActivity() ,Tags.TAGS_FILE_NAME);
        }
        else
        {
            Log.w("Search:fillTags", "Tags not found");
        }

        //Start GPS
        currentLatLng = new LatLng(0.0d,0.0d);
        gps = new GPS(this.getActivity());
        gps.delegate = this;
        gps.Start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v("Search:OnCreate","Started");

        //Define view
        LinearLayout ll = (LinearLayout )inflater.inflate(R.layout.fragment_users_search, container, false);
        //txtSearch = (TextView)container.findViewById(R.id.txtName);
        //radRadius = (RadioGroup) container.findViewById(R.id.radRadius);
        txtTag= (MultiAutoCompleteTextView) ll.findViewById(R.id.txtTag);
        txtTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                //String text =  txtTag.getText().toString();
                tagsActive +=  String.valueOf( tags.getTagList().get(position).id) + ",";
                Log.v("Search:Tags ",  tagsActive );
            }
        });

        //fillTags();


        Log.v("Search:Latitude",String.valueOf(currentLatLng.latitude));
        Log.v("Search:Longitude",String.valueOf(currentLatLng.longitude));

        return ll;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        TagAdapter adapter = new TagAdapter(getActivity().getApplicationContext(), (ArrayList<Tag>) tags.getTagList());
        //ArrayAdapter<String> adapter = new ArrayAdapter<String> (getActivity().getApplicationContext(),  R.layout.row_tag, R.id.txtItemTag, tags.getTagStringArray());
        // ArrayAdapter<String> adapter = new ArrayAdapter<String> (getActivity().getApplicationContext(),  R.layout.row_tag, R.id.txtItemTag, tags.getTagStringArray());
        txtTag.setAdapter(adapter);
        txtTag.setThreshold(2);
        txtTag.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    @Override
    public void processFinish(String output)
    {
        Log.w("Search:processFinish", output);
        try
        {
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
                //Set distance field for each user
                for(int i = 0; i < products.size(); i++)
                {
                    products.getProduct(i).setDistanceMeters(currentLatLng);
                }
                //Pass users to list, then load list
                Utility.saveObject(this.getActivity(), products);
                //Move to list fragment
                ((MainActivity)getActivity()).showProductsFragment();
            }
            Log.w("Search:Process:Products", "Print");
            products.print();
        }
        catch (Exception ex)
        {
            Log.w("Search:Process:Err", ex.getMessage());
        }
    }

    @Override
    public void onGPSConnectionResolutionRequest(ConnectionResult connectionResult )
    {
        try {
            connectionResult.startResolutionForResult(this.getActivity(), ResponseGPS.CONNECTION_FAILURE_RESOLUTION_REQUEST);
        }catch (Exception ex){Log.v("Search:onGPSConFail",ex.getMessage());}
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
        Log.v("Search:GPSPosRes",position.toString());
    }

    protected void fillTags()
    {

    }


}
