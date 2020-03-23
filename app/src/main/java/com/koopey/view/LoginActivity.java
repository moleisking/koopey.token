package com.koopey.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GetJSON;
import com.koopey.controller.PostJSON;
import com.koopey.model.*;
import com.koopey.common.SecurityHelper;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
/*, LoaderCallbacks<Cursor> ,*/
public class LoginActivity extends AppCompatActivity implements GetJSON.GetResponseListener, PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "LOGIN:ACTIVITY:";
    private TextInputEditText txtEmail;
    private TextInputEditText txtPassword;
    private Button btnLogin, btnRegister;
    private View mProgressView;
    private View mLoginFormView;
    private AuthUser authUser;
    private Tags tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String encrypted = SecurityHelper.encrypt("hello world", "12345");

        Log.d("Security:encrypted",encrypted);
        Log.d("Security:decrypted",SecurityHelper.decrypt(encrypted, "12345"));

        try {
            //Define views
            this.mLoginFormView = findViewById(R.id.login_form);
            this.mProgressView = findViewById(R.id.login_progress);
            this.txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);
            this.txtPassword = (TextInputEditText) findViewById(R.id.txtPassword);
            this.btnLogin = (Button) findViewById(R.id.btnLogin);
            this.btnRegister = (Button) findViewById(R.id.btnRegister);

            //Set Listeners
            this.btnLogin.setOnClickListener(this);
            this.btnRegister.setOnClickListener(this);


            txtEmail.setText("moleisking@gmail.com");
            txtPassword.setText("12345");
            //Download tags
            if (SerializeHelper.hasFile(this, Tags.TAGS_FILE_NAME)) {
                Log.d(LOG_HEADER, "Tag file found");
                tags = (Tags) SerializeHelper.loadObject(this, Tags.TAGS_FILE_NAME);
            } else {
                Log.d(LOG_HEADER, "No tag file found");
                tags = new Tags();
                getTags();
            }
            //Check if user has logged in previously
            if (SerializeHelper.hasFile(this, AuthUser.AUTH_USER_FILE_NAME)) {
                this.authUser = (AuthUser) SerializeHelper.loadObject(getApplicationContext(), AuthUser.AUTH_USER_FILE_NAME);

                if (this.authUser.hasToken()) {
                    //Already logged in go straight to main application
                    Log.d(LOG_HEADER, "MyUser file found");
                    showMainActivity();
                }
                if (this.authUser != null && this.authUser.getToken().equals("") && this.authUser.email.equals("")) {
                    //Check for corrupt file
                    Log.d(LOG_HEADER, "Found corrupt file");
                    deleteFile(AuthUser.AUTH_USER_FILE_NAME);
                }
            } else {
                this.authUser = new AuthUser();
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == this.btnLogin.getId()) {
                this.onLoginClick(v);
            } else if (v.getId() == this.btnRegister.getId()) {
                this.onRegisterClick(v);
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void onGetResponse(String output) {
        showProgress(false);
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isSuccess()) {
                    Toast.makeText(this, getResources().getString(R.string.info_authentication), Toast.LENGTH_LONG).show();
                } else if (alert.isError()) {
                    Toast.makeText(this, getResources().getString(R.string.error_authentication), Toast.LENGTH_LONG).show();
                }
            } else if (header.contains("assets")) {
                Assets assets = new Assets(Assets.MY_ASSETS_FILE_NAME);
                assets.parseJSON(output);
                SerializeHelper.saveObject(this, assets);
            } else if (header.contains("tags")) {
                Tags tags = new Tags();
                tags.parseJSON(output);
                SerializeHelper.saveObject(this, tags);
            } else if (header.contains("transactions")) {
                Transactions transactions = new Transactions();
                transactions.parseJSON(output);
                SerializeHelper.saveObject(this, transactions);
            } else if (header.contains("user")) {
                authUser = new AuthUser();
                authUser.parseJSON(output);
                authUser.print();
                Toast.makeText(this, getResources().getString(R.string.info_authentication), Toast.LENGTH_SHORT).show();
                SerializeHelper.saveObject(this, authUser);
                showMainActivity();
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onPostResponse(String output) {
        showProgress(false);
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isSuccess()) {
                    Toast.makeText(this, getResources().getString(R.string.info_authentication), Toast.LENGTH_LONG).show();
                } else if (alert.isError()) {
                    Toast.makeText(this, getResources().getString(R.string.error_authentication), Toast.LENGTH_LONG).show();
                }
            } else if (header.contains("user")) {

                this.authUser = new AuthUser();
                this.authUser.parseJSON(output);
                this.authUser.print();
                Toast.makeText(this, getResources().getString(R.string.info_authentication), Toast.LENGTH_SHORT).show();
                SerializeHelper.saveObject(this, authUser);
                showMainActivity();
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        //Shows the progress UI and hides the login form.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            this.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            this.mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show and hide the relevant UI components.
            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    protected void onLoginClick(View view) {
        // Reset errors text
        this.txtEmail.setError(null);
        this.txtPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = txtEmail.getText().toString().toLowerCase();
        String password = txtPassword.getText().toString();

        if (password == null || password.trim().equals("") || (password.length() < 4)) {
            // Check for a valid password, if the user entered one.
            this.txtPassword.setError(getString(R.string.error_invalid_password));
            this.txtPassword.requestFocus();
        } else if (email == null || email.trim().equals("")) {
            // Error email is empty
            this.txtEmail.setError(getString(R.string.error_field_required));
            this.txtEmail.requestFocus();
        } else if (!email.contains("@") && !email.contains(".")) {
            // Error text is not email
            this.txtEmail.setError(getString(R.string.error_invalid_email));
            this.txtEmail.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);
            postAuthentication();
        }
    }

    protected void onRegisterClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void showMainActivity() {
        //Note* intent.putExtra("MyUser", myUser) creates TransactionTooLargeException
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    protected void getMyAssets(AuthUser myUser) {
        GetJSON asyncTask = new GetJSON(this);
        asyncTask.delegate = this;
        asyncTask.execute(this.getString(R.string.get_assets_read_mine), "", myUser.getToken());
    }

    protected void getTransactions(AuthUser myUser) {
        GetJSON asyncTask = new GetJSON(this);
        asyncTask.delegate = this;
        asyncTask.execute(this.getString(R.string.get_transaction_read_many), "", myUser.getToken());
    }

    protected void getTags() {
        GetJSON asyncTask = new GetJSON(this);
        asyncTask.delegate = this;
        asyncTask.execute(this.getString(R.string.get_tags_read), "", "");
    }

    private void postAuthentication() {
        AuthUser myUser = new AuthUser();
        myUser.email = txtEmail.getText().toString().trim();
        myUser.password = txtPassword.getText().toString();
        PostJSON asyncTask = new PostJSON(this);
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_auth_login), myUser.toString(), "");
    }

    protected void exit() {
        this.finish();
        System.exit(0);
    }
}