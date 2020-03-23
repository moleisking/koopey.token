package me.minitrabajo.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.minitrabajo.R;
import me.minitrabajo.common.Utility;
//import me.minitrabajo.model.MyProduct;
//import me.minitrabajo.model.MyProducts;
import me.minitrabajo.model.Product;
import me.minitrabajo.model.Products;
import me.minitrabajo.model.Tag;
import me.minitrabajo.view.TagTokenAutoCompleteView;

/**
 * Created by Scott on 16/02/2017.
 */
public class MyProductAdapterOLD extends ArrayAdapter<Product> {
    private final String LOG_HEADER = "MY:PT:AD";

    public  MyProductAdapterOLD(Context context, ArrayList<Product> myProducts) {
        super(context, 0, myProducts);
    }

   /* public  MyProductAdapterOLD(Context context, MyProducts myProducts) {
      //  super(context, 0, myProducts.getMyProductList());
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try {
            // Get the data item for this position
            Product myProduct = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_product, parent, false);
            }
            // Lookup view for data population
            TagTokenAutoCompleteView lstTags= (TagTokenAutoCompleteView) convertView.findViewById(R.id.lstTags);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            TextView txtDistance = (TextView) convertView.findViewById(R.id.txtDistance);
            ImageView img = (ImageView) convertView.findViewById(R.id.img1);
            //Add existing selected tags to control
            lstTags.allowDuplicates(false);
            lstTags.setFocusable(false) ;
            lstTags.setClickable(false);
            lstTags.clear();
            for(Tag t : myProduct.tags.getList()) {
                lstTags.addObject(t);
            }
            // Populate the data into the template view using the data object
            txtTitle.setText(myProduct.title);
            txtDistance.setText(myProduct.getCurrentDistanceKilometersText());
            try {
                if (!myProduct.images.getFirstImage().equals("")) {
                  //  img.setImageBitmap(Utility.parseImageUri(myProduct.imageUri1));
                    img.setImageBitmap(myProduct.images.getFirstImage().getBitmap());
                }
                else
                {
                    img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.default_product));
                }
            }catch (Exception iex){
                Log.v(LOG_HEADER + ":IG1","Image not loaded");
            }
           /* try {
                img.setImageDrawable(Utility.RoundBitmap( product.imageUri1) );
            }catch (Exception iex){
                Log.v(LOG_HEADER + "IG2","Image not loaded");
            }*/
            //img.setImageBitmap(user.getImageAsBitmap());
            // Return the completed view to render on screen
        }catch (Exception ex){
            Log.v(LOG_HEADER + ":ER",ex.getMessage());
        }
        return convertView;
    }
}
