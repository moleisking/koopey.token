package me.minitrabajo.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scott on 10/02/2017.
 */
public class MyProductsOLD extends Products implements Serializable {
    private static final String LOG_HEADER = "MY:PDS";
    public static final String MY_PRODUCTS_FILE_NAME = "myproducts.dat";
    protected List<Product> myProducts;

    public MyProductsOLD()
    {
        myProducts = new ArrayList<Product>(0);
    }

    public Product getMyProduct(int index)
    {
        return myProducts.get(index);
    }

    public Product getMyProduct(String id)
    {
        Product result = null;
        for (int i =0; i < myProducts.size();i++)
        {
            if (myProducts.get(i).id.equals(id))
            {
                result = myProducts.get(i);
                break;
            }
        }
        return result;
    }

    public void add(Product p)
    {
        try
        {
            myProducts.add(p);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    public void remove(Product p)
    {
        myProducts.remove(p);
    }

    public int size()
    {
        return myProducts.size();
    }

    public List<Product> getMyProductList()
    {
        return this.myProducts;
    }

    protected void setList(List<Product> myProducts)
    {
        this.myProducts = myProducts;
    }

    public boolean isEmpty()
    {
        return this.size() == 0 ? true : false;
    }


    public void parseJSON(String json) {
        final String LOG_FUNCTION = LOG_HEADER + ":PASS:JSON:STR";
        Log.v(LOG_FUNCTION,json);
        JSONArray jsonArray ;//= new JSONObject(json).getJSONArray("images");
        try {
            //Check JSON format, which could be [ or {
            if(json.length() >= 1){
                if(json.substring(0,1).equals("[")){
                    //[] array format
                    jsonArray = new JSONArray(json);
                    this.parseJSON(jsonArray);
                }
                else if(json.substring(0,1).equals("{")){
                    //{products:''} object format
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("products");
                    this.parseJSON(jsonArray);
                }
            }
        } catch (Exception ex) {
            Log.v(LOG_FUNCTION + ":ER", ex.getMessage());
        }
    }

    public void parseJSON(JSONArray jsonArray) {
        final String LOG_FUNCTION = LOG_HEADER + ":PASS:JSON:OBJ";
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Product myProduct = new Product();
                if(!jsonObject.toString().equals("{}")){
                    myProduct.parseJSON(jsonObject);
                    myProduct.print();
                    this.add(myProduct);
                }
            }
        } catch (Exception ex) {
            Log.v(LOG_FUNCTION + ":ER", ex.getMessage());
        }
    }

    public void print()
    {
        try{
            Log.v("MyProducts", "Object");
            Log.v("MyProducts Size", String.valueOf(this.size()));
            for(int i = 0; i < myProducts.size(); i++)
            {
                myProducts.get(i).print();
                if (i==3){break;}
            }
        } catch (Exception ex){Log.v(LOG_HEADER + ":PT:ER", ex.getMessage());}
    }

}
