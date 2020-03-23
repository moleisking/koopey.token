package me.minitrabajo.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

import me.minitrabajo.common.Utility;

/**
 * Created by Scott on 10/02/2017.
 */
public class MyProductOLD extends Product implements Serializable {
    private static final String LOG_HEADER = "MY:PT:";
    public static final String MY_PRODUCT_FILE_NAME = "myproduct.dat";

    public MyProductOLD()
    { }

    public boolean isValid()
    {
        final String LOG_FUNCTION = LOG_HEADER + "IS:VD";
        boolean hasImage = false;

        //Check if at least one images was uploaded
        for(int i = 0; i < 4;i++)
        {
            if (!this.images.get(i).uri.equals("") ) {
                hasImage = true;
            }
        }
        //Note* userid is also passed in token so userid check is not necessary
        if (hasImage && !this.title.equals("") && this.value >= 0 && this.tags.size() >= 0)
        {
            return true;
        }        else
        {
            return false;
        }
    }

    public Product getProduct()
    {
      /*  Product product = new Product();
        product.id = this.id;
        product.title = this.title;
        product.description = this.description;
        product.value = this.value;
        product.user = this.user;
        product.sold = this.sold;
        product.createTimeStamp = this.createTimeStamp;
        product.tags = this.tags;
        product.setCurrentLatLng(this.getCurrentLatLng());
        product.setRegisteredLatLng(this.getRegisteredLatLng());
        product.images = this.images;
        return product;*/
        return (Product)this;
    }

    public JSONObject toJSONObject() {
        final String LOG_FUNCTION = LOG_HEADER + ":TO:JSON:OBJ:ER";
        JSONObject jsonObject = super.toJSONObject(); //new JSONObject();
       /* try {
            //Strings
            if (!this.id.equals("")) {
                jsonObject.put("id", this.id);
            }
            if (!this.title.equals("")) {
                jsonObject.put("title", this.title);
            }
            if (!this.description.equals("")) {
                jsonObject.put("description", this.description);
            }
            //Doubles
            if (this.createTimeStamp != 0) {
                jsonObject.put("createTimeStamp", this.createTimeStamp);
            }
            if (this.value != 0) {
                jsonObject.put("price", this.value);
            }
            if (this.registeredLatitude != 0) {
                jsonObject.put("regLat", this.registeredLatitude);
            }
            if (this.registeredLongitude != 0) {
                jsonObject.put("regLng", this.registeredLongitude);
            }
            if (this.currentLatitude != 0) {
                jsonObject.put("curLat", this.currentLatitude);
            }
            if (this.currentLongitude != 0) {
                jsonObject.put("curLng", this.currentLongitude);
            }
            //Booleans
            jsonObject.put("sold", this.sold);
            //Tags
            if (this.tags.size() > 0) {
                jsonObject.put("tags", this.tags.toJSONArray());
            }
            //Images
            try {
                //All users should have at least one image.
                jsonObject.put("images",  this.images.toJSONArray());
            } catch (Exception e) {
                Log.v(LOG_HEADER + LOG_FUNCTION, e.getMessage());
            }
            //User
            try {
                //All users should have at least one image.
                jsonObject.put("user",  this.user.toJSONObject());
            } catch (Exception e) {
                Log.v(LOG_HEADER + LOG_FUNCTION, e.getMessage());
            }
        } catch (Exception ex) {
            Log.v(LOG_FUNCTION, ex.getMessage());
        }*/

        return jsonObject;
    }

    @Override
    public String toString() {
        //JSONObject adds backslash in front of forward slashes causing corrupt images
        return this.toJSONObject().toString().replaceAll("\\/", "/");
    }

    @Override
    public void print()
    {
        try{
            Log.v("MyTransaction", "Object");
            super.print();
        } catch (Exception ex){Log.v(LOG_HEADER + ":PT:ER", ex.getMessage());}
    }

}
