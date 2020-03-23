package com.koopey.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.koopey.R;
import com.koopey.common.CurrencyHelper;
import com.koopey.common.DistanceHelper;
import com.koopey.model.*;
//import com.koopey.model.MyProduct;
//import com.koopey.model.MyProducts;


/**
 * Created by Scott on 12/10/2016.
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 */
public class AssetAdapter extends ArrayAdapter<Asset>
{
    private final String LOG_HEADER = "ASSET:ADAPTER";

    public AssetAdapter(Context context, ArrayList<Asset> assets) {
        super(context, 0, assets);
    }

    public AssetAdapter(Context context, Assets assets) {
        super(context, 0, assets.get());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try {
            // Get the data item for this position
            Asset asset = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_asset, parent, false);
            }
            // Lookup view for data population
            //TagTokenAutoCompleteView lstTags= (TagTokenAutoCompleteView) convertView.findViewById(R.id.lstTags);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            TextView txtDistance = (TextView) convertView.findViewById(R.id.txtDistance);
            TextView txtCurrency = (TextView) convertView.findViewById(R.id.txtCurrency);
            TextView txtValue = (TextView) convertView.findViewById(R.id.txtValue);
            ImageView img = (ImageView) convertView.findViewById(R.id.imgAsset);
            // Populate the data into the template view using the data object
            //lstTags.allowDuplicates(false);
            //lstTags.setFocusable(false) ;
            //lstTags.setClickable(false);
            //lstTags.clear();
            //Add existing selected tags to control
            //for(Tag t : product.tags.getList()) {
            //    lstTags.addObject(t);
            //}
            txtTitle.setText(asset.title);
            txtDistance.setText( DistanceHelper.DistanceToKilometers(asset.distance));
            txtValue.setText(Double.toString( asset.value));
            txtCurrency.setText(CurrencyHelper.currencyCodeToSymbol( asset.currency));
            try {
                if (!asset.images.getFirstImage().equals("")) {
                    img.setImageBitmap( asset.images.getFirstImage().getBitmap() );
                }
                else
                {
                    img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.default_product));
                }
            }catch (Exception iex){
                Log.d(LOG_HEADER + ":ER","Image not loaded");
            }

        }catch (Exception ex){
            Log.d(LOG_HEADER + ":ER",ex.getMessage());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
