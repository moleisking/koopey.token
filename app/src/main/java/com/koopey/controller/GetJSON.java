/*    -Example of Authenticate-
   POST /authenticate HTTP/1.1
   Host: localhost:3003
   Cache-Control: no-cache
   Postman-Token: 87497dd5-0cf0-375f-213f-83818a54a1a0
   Content-Type: application/x-www-form-urlencoded
   name=scott&pass=12345&email=moleisking%40gmail.com
   */

package com.koopey.controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

//import com.google.common.net.*;

//http://www.wikihow.com/Execute-HTTP-POST-Requests-in-Android
//https://www.youtube.com/watch?v=ryY7Dy3z-7Q
//http://stackoverflow.com/questions/2938502/sending-post-data-in-android
//https://blog.dahanne.net/2009/08/16/how-to-access-http-resources-from-android/
//http://littlesvr.ca/grumble/2014/07/21/android-programming-connect-to-an-https-server-with-self-signed-certificate/


public class GetJSON extends AsyncTask <String, String, String>
{
    private final String LOG_HEADER = "GET:JSON";
    public GetJSON.GetResponseListener delegate = null;
    private Context context;

    public interface GetResponseListener {
        void onGetResponse(String output);
    }

	public GetJSON(Context context)
	{
		this.context = context;
	}

    @Override
    protected void onPreExecute() { super.onPreExecute(); }

    @Override
    protected String doInBackground(String... params)
    {
        //params: url, parameters, header
        Log.w(LOG_HEADER, "Start");
        if(params.length >=2)
        {
            try {
                Log.w("doInBackground[0]", params[0]);
                Log.w("doInBackground[1]", params[1]);
                Log.w("doInBackground[2]", params[2]);
            }catch (Exception exlog){}

            //HttpURLConnection connection = null;
            HttpsURLConnection connection = null;
            String reply = "{ alert : { 'type' : 'error' , 'message':'GetJSON failed'}}";

            try            {
                // My CRT file that I put in the assets folder
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                InputStream caInput = new BufferedInputStream(context.getAssets().open("primary.crt"));
                Certificate ca = cf.generateCertificate(caInput);
                System.out.println("CA=" + ((X509Certificate) ca).getSubjectDN());

                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                // Create an SSLContext that uses our TrustManager
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);


                //Set Network IO
                URL url = new URL(params[0].toString());
                //connection = (HttpURLConnection) url.openConnection();
                connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(context.getSocketFactory());
                connection.setHostnameVerifier(new HostnameVerifier()                {
                    @Override
                    public boolean verify(String hostname, SSLSession session)
                    {
                        if(hostname.equals("192.168.1.100")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                // Use this if you need SSL authentication
                //String userpass = user + ":" + password;
                //String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
                // connection.setRequestProperty("Authorization", basicAuth);

                //Build Header
                connection.setDoInput(true);
               // connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setReadTimeout(45000);
                connection.setConnectTimeout(45000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");

                if(params.length == 3 && params[2]!= null && !params[2].equals(""))                {
                    connection.setRequestProperty("Authorization" , params[2]);
                }
                Log.w(LOG_HEADER +":HEAD", "Built");

                /*//Write Parameters
                if (params[1]!=null && !params[1].equals("")) {
                    PrintWriter out = new PrintWriter(connection.getOutputStream());
                    out.print(params[1]);
                    out.close();
                    Log.w("doInBackground:PARAM", "Sent");
                }*/

                //Get Reply Status 400, 404, 200
                String http_code = String.valueOf(connection.getResponseCode());
                String http_message = connection.getResponseMessage();
                Log.w(http_code, http_message);

                //Read Response
                InputStream in = connection.getInputStream();
                reply = this.convertStreamToString(in);
                Log.w(LOG_HEADER + ":REPLY", reply);
            }
            catch (IOException ioe)            {
                Log.d(LOG_HEADER +"IO:ERR", ioe.getMessage());
            }
            catch (Exception ex)            {
                Log.d(LOG_HEADER + ":ERR", ex.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                Log.w(LOG_HEADER + ":END", "Connection Closed");
            }

            return reply;
        }
        else
        {
            Log.w(LOG_HEADER , "Not Enough Parameters");
            return "{ alert : { 'type' : 'error' , 'message':" + LOG_HEADER + "'}}";
        }
    }


    @Override
    protected void onPostExecute(String result) { delegate.onGetResponse(result); }

    /*@Override
    protected void onCancelled() { delegate.processCancel(); }*/

	private String convertStreamToString(InputStream is)
    {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkinfo = cm.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected()) {
            return true;
        }
        return false;
    }

}
