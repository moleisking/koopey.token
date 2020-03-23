package com.koopey.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.ImageHelper;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GetJSON;
import com.koopey.controller.LocationReceiver;
import com.koopey.controller.MessageIntentService;
import com.koopey.controller.MessageReceiver;
import com.koopey.model.Alert;
import com.koopey.model.Asset;
import com.koopey.model.Assets;
import com.koopey.model.AuthUser;
import com.koopey.model.Bitcoin;
import com.koopey.model.Ethereum;
import com.koopey.model.Image;
import com.koopey.model.Images;
import com.koopey.model.Messages;
import com.koopey.model.Tags;
import com.koopey.model.Transaction;
import com.koopey.model.Transactions;
import com.koopey.model.User;
import com.koopey.model.Users;
import com.koopey.model.Wallet;

import java.util.Date;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements GetJSON.GetResponseListener,
        ImageListFragment.OnImageListFragmentListener, NavigationView.OnNavigationItemSelectedListener, MessageIntentService.OnMessageListener /*, View.OnTouchListener*/ {

    private static final int PERMISSION_REQUEST = 1004;
    private final String LOG_HEADER = "MAIN:ACTIVITY";
    private Toolbar toolbar;
    private NavigationView navigationView;
    private View headerLayout;
    private ImageView imgAvatar;
    private TextView txtAliasOrName, txtDescription;
    private AuthUser authUser;
    private Alert alert;
    private Bitcoin bitcoin;
    private Ethereum ethereum;
    private Point touch;
    //private GestureDetector gestureDetector;

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        try {
            //Load user passed from login via saved file
            if (SerializeHelper.hasFile(this, AuthUser.AUTH_USER_FILE_NAME)) {
                this.authUser = (AuthUser) SerializeHelper.loadObject(this, AuthUser.AUTH_USER_FILE_NAME);
            } else {
                this.navigationView.inflateMenu(R.menu.menu_unauthenticated_drawer);
                showLoginActivity();
            }

            //Set toolbar
            this.toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //Set drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    hideKeyboard();
                }
            };
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            //Define views
            this.navigationView = (NavigationView) findViewById(R.id.nav_view);
            this.navigationView.setNavigationItemSelectedListener(this);
            this.headerLayout = navigationView.getHeaderView(0);
            this.imgAvatar = (ImageView) headerLayout.findViewById(R.id.nav_head_imgAvatar);
            this.txtAliasOrName = (TextView) headerLayout.findViewById(R.id.nav_head_txtAliasOrName);
            this.txtDescription = (TextView) headerLayout.findViewById(R.id.nav_head_txtDescription);

            headerLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showMyUserReadFragment();
                    hideDrawer();
                }
            });
            //Set default values
            if (getResources().getBoolean(R.bool.alias)) {
                this.txtAliasOrName.setText(authUser.alias);
            } else {
                this.txtAliasOrName.setText(authUser.name);
            }
            this.txtDescription.setText(authUser.description);
            try {
                if (this.authUser.avatar != null) {
                    this.imgAvatar.setImageBitmap(ImageHelper.IconBitmap(this.authUser.avatar));
                } else {
                    this.imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.default_user));
                }
            } catch (Exception ex) {
                Log.d(LOG_HEADER, "Avatar image not found");
            }

            //Set business model
            this.setVisibility();

            //Request permissions
            if (!this.hasPermissions()) {
                this.requestPermissions();
            }

            this.hideKeyboard();
            //Initialize gesture listener
            //CustomGestureDetector customGestureDetector = new CustomGestureDetector();
            //gestureDetector = new GestureDetector(this, customGestureDetector);
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onGetResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                this.alert = new Alert();
                this.alert.parseJSON(output);
                if (this.alert.isError()) {
                    Toast.makeText(this, getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                }
            } else if (header.contains("bitcoin")) {
                //Downloaded during app load
                this.bitcoin = new Bitcoin();
                this.bitcoin.parseJSON(output);
                SerializeHelper.saveObject(this, bitcoin);
            } else if (header.contains("ethereum")) {
                this.ethereum = new Ethereum();
                this.ethereum.parseJSON(output);
                SerializeHelper.saveObject(this, ethereum);
            } else if (header.contains("messages")) {
                Messages messages = new Messages();
                messages.parseJSON(output);
                SerializeHelper.saveObject(this, messages);
            } else if (header.contains("assets")) {
                Assets assets = new Assets();
                assets.fileType = Assets.MY_ASSETS_FILE_NAME;
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
                this.authUser = new AuthUser();
                this.authUser.parseJSON(output);
                SerializeHelper.saveObject(this, authUser);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_my_assets) {
            showMyAssetListFragment();
        } else if (id == R.id.nav_dashboard) {
            showDashBoardFragment();
        } else if (id == R.id.nav_calendar) {
            showCalendarFragment();
        } else if (id == R.id.nav_user_name_search) {
            showUserNameSearchFragment();
        } else if (id == R.id.nav_transactions) {
            showTransactionListFragment();
        } else if (id == R.id.nav_transaction_search) {
            showTransactionSearchFragment();
        } else if (id == R.id.nav_product_search) {
            showProductSearchFragment();
        } else if (id == R.id.nav_service_search) {
            showProductSearchFragment();
        } else if (id == R.id.nav_results) {
            showPreviousResults();
        } else if (id == R.id.nav_setting) {
            showSettingFragment();
        } else if (id == R.id.nav_conversations) {
            showConversationListFragment();
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_wallets) {
            showWalletListFragment();
        }
        this.hideDrawer();
        //return true;
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            this.showSettingFragment();
        } else if (item.getItemId() == R.id.action_refresh) {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment != null) {
                if (fragment instanceof AssetReadFragment) {
                    ((AssetReadFragment) fragment).populateAsset();
                } else if (fragment instanceof ConversationListFragment) {
                    ((ConversationListFragment) fragment).syncConversations();
                } else if (fragment instanceof MessageListFragment) {
                    ((MessageListFragment) fragment).syncConversation();
                } else if (fragment instanceof MyAssetListFragment) {
                    ((MyAssetListFragment) fragment).syncAssets();
                } else if (fragment instanceof TransactionListFragment) {
                    ((TransactionListFragment) fragment).populateTransactions();
                } else if (fragment instanceof UserReadFragment) {
                    ((UserReadFragment) fragment).populateUser();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Set default fragment to show
        showPreviousResults();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length == 5 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[5] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[6] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[7] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_HEADER, "onRequestPermissionsResult success");
            } else {
                Log.d(LOG_HEADER, "onRequestPermissionsResult error");
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_HEADER, "onRestart");
        startNotificationService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_HEADER, "onStart");
        startNotificationService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_HEADER, "onStop");
        stopNotificationService();
    }

    public void createImageListFragmentEvent(Image image) {
        Log.d(LOG_HEADER, "createImageListFragmentEvent(Image image)");
        Fragment fragment = getFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            if (fragment instanceof AssetCreateFragment) {
                ((AssetCreateFragment) fragment).createImageListFragmentEvent(image);
            } else if (fragment instanceof AssetUpdateFragment) {
                ((AssetUpdateFragment) fragment).createImageListFragmentEvent(image);
            }
        }
    }

    public void deleteImageListFragmentEvent(Image image) {
        Log.d(LOG_HEADER, "deleteImageListFragmentEvent(Image image)");
        Fragment fragment = getFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            if (fragment instanceof AssetCreateFragment) {
                ((AssetCreateFragment) fragment).deleteImageListFragmentEvent(image);
            } else if (fragment instanceof AssetUpdateFragment) {
                ((AssetUpdateFragment) fragment).deleteImageListFragmentEvent(image);
            }
        }
    }

    protected void exit() {
        this.finish();
        System.exit(0);
    }

    public void updateImageListFragmentEvent(Image image) {
        Log.d(LOG_HEADER, "updateImageListFragmentEvent(Image image)");
        Fragment fragment = getFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            if (fragment instanceof AssetCreateFragment) {
              ((AssetCreateFragment)fragment).updateImageListFragmentEvent(image);
            } else if (fragment instanceof AssetUpdateFragment) {
                ((AssetUpdateFragment) fragment).updateImageListFragmentEvent(image);
            }
        }
    }

    public void updateMessages(Messages messages) {
        Log.d(LOG_HEADER + ":UP:MSG", "updateMessages");
        messages.print();
    }

    public void startNotificationService() {
        //Start message and location service
        MessageReceiver.startAlarm(getApplicationContext());
        LocationReceiver.startAlarm(getApplicationContext());
    }

    public void stopNotificationService() {
        //Stop message and location service
        MessageReceiver.stopAlarm(getApplicationContext());
        LocationReceiver.stopAlarm(getApplicationContext());
    }

    protected AuthUser getAuthUserFromFile() {
        return this.authUser;
    }

    private void setVisibility() {
        //Products
        if (this.getResources().getBoolean(R.bool.products)) {
            navigationView.getMenu().findItem(R.id.nav_product_search).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_product_search).setVisible(false);
        }
        //Services
        if (this.getResources().getBoolean(R.bool.services)) {
            navigationView.getMenu().findItem(R.id.nav_service_search).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_service_search).setVisible(false);
        }
        //Transactions
        if (this.getResources().getBoolean(R.bool.transactions)) {
            navigationView.getMenu().findItem(R.id.nav_transactions).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_transactions).setVisible(false);
        }
    }

    protected void showAboutFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new AboutFragment())
                .addToBackStack("fragment_about")
                .commit();
    }

    protected void showBarcodeReadFragment(String barcode) {
        this.getIntent().putExtra("barcode", barcode);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new BarcodeReadFragment())
                .addToBackStack("fragment_barcode_read")
                .commit();
    }

    protected void showBarcodeScannerFragment(Transaction transaction) {
        this.getIntent().putExtra("transaction", transaction);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new BarcodeScannerFragment())
                .addToBackStack("fragment_barcode_scanner")
                .commit();
    }

    public void showCalendarFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new CalendarFragment())
                .addToBackStack("fragment_calendar")
                .commit();
    }

    public void showConversationListFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new ConversationListFragment())
                .addToBackStack("fragment_conversations")
                .commit();
    }

    protected void showDashBoardFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new DashboardFragment())
                .addToBackStack("fragment_dashboard")
                .commit();
    }

    public void showFileReadFragment(com.koopey.model.File file) {
        this.getIntent().putExtra("file", file);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new FileReadFragment())
                .addToBackStack("fragment_file_read")
                .commit();
    }

    public void showImageListFragment(Images images) {
        this.getIntent().putExtra("images", images);
        this.getIntent().putExtra("showCreateButton", true);
        this.getIntent().putExtra("showUpdateButton", true);
        this.getIntent().putExtra("showDeleteButton", true);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new ImageListFragment())
                .addToBackStack("fragment_images")
                .commit();
    }

    public void showImageReadFragment(Image image) {
        this.getIntent().putExtra("image", image);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new ImageReadFragment())
                .addToBackStack("fragment_image_read")
                .commit();
    }

    public void showImageUpdateFragment(Images images) {
        this.getIntent().putExtra("images", images);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new ImageUpdateFragment())
                .addToBackStack("fragment_image_update")
                .commit();
    }

    protected void showLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void showMessageListFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MessageListFragment())
                .addToBackStack("fragment_messages")
                .commit();
    }

   /* public void showMyAssetReadFragment(Asset asset) {
        //NOTE: Fragment will handle update permissions
        this.getIntent().putExtra("MyProduct", asset);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new AssetReadFragment())
                .addToBackStack("fragment_asset_read")
                .commit();
        this.setTitle(getResources().getString(R.string.label_my_asset));
    }*/

    public void showMyAssetListFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MyAssetListFragment())
                .addToBackStack("fragment_my_assets")
                .commit();
    }

    public void showMyUserReadFragment() {
        //NOTE:Fragment will handle update permissions
        User user = authUser.getUser();
        this.getIntent().putExtra("user", user);
        this.getIntent().putExtra("showUpdateButton", true);
        this.getIntent().putExtra("showDeleteButton", true);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new UserReadFragment())
                .addToBackStack("fragment_user_read")
                .commit();
        this.setTitle(getResources().getString(R.string.label_my_user));
    }

    public void showPreviousResults() {
        if (SerializeHelper.hasFile(this, Assets.ASSET_SEARCH_RESULTS_FILE_NAME)) {
            Assets assets = (Assets) SerializeHelper.loadObject(this, Assets.ASSET_SEARCH_RESULTS_FILE_NAME);
            if (assets == null || assets.isEmpty()) {
                this.showDashBoardFragment();
            } else {
                this.showAssetListFragment();
            }
        } else if (SerializeHelper.hasFile(this, Users.USERS_FILE_NAME)) {
            Users users = (Users) SerializeHelper.loadObject(this, Users.USERS_FILE_NAME);
            if (users == null || users.isEmpty()) {
                this.showDashBoardFragment();
            } else {
                this.showUserListFragment();
            }
        } else {
            this.showDashBoardFragment();
        }
    }

    public void showAssetMapFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new AssetMapFragment())
                .addToBackStack("fragment_asset_map")
                .commit();
        this.setTitle(getResources().getString(R.string.label_map));
    }

    public void showAssetCreateFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new AssetCreateFragment())
                .addToBackStack("fragment_asset_create")
                .commit();
    }

    protected void showAssetListFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new AssetListFragment())
                .addToBackStack("fragment_assets")
                .commit();
    }

    public void showAssetUpdateFragment(Asset asset) {
        //Note* Asset object sent by list fragment
        this.getIntent().putExtra("asset", asset);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new AssetUpdateFragment())
                .addToBackStack("fragment_asset_update")
                .commit();
    }

    public void showAssetReadFragment(Asset asset) {
        this.getIntent().putExtra("asset", asset);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new AssetReadFragment())
                .addToBackStack("fragment_asset_read")
                .commit();
    }

    public void showProductSearchFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SearchProductsFragment())
                .addToBackStack("fragment_asset_search")
                .commit();
    }

    protected void showReviewCreateFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new ReviewCreateFragment())
                .addToBackStack("fragment_review_create")
                .commit();
    }

    public void showTagCreateFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new TagCreateFragment())
                .addToBackStack("fragment_tag_create")
                .commit();
    }

    public void showTransactionCreateFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new TransactionCreateFragment())
                .addToBackStack("fragment_transaction_create")
                .commit();
    }

    public void showTransactionCreateFragment(Transaction transaction) {
        this.getIntent().putExtra("transaction", transaction);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new TransactionCreateFragment())
                .addToBackStack("fragment_transaction_create")
                .commit();
    }

    public void showTransactionReadFragment(Transaction transaction) {
        this.getIntent().putExtra("transaction", transaction);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new TransactionReadFragment())
                .addToBackStack("fragment_transaction_read")
                .commit();
    }

    public void showTransactionUpdateFragment(Transaction transaction) {
        this.showTransactionUpdateFragment(transaction, null);
    }

    public void showTransactionUpdateFragment(Transaction transaction, String barcode) {
        if ((barcode != null) && !barcode.equals("")) {
            this.getIntent().putExtra("barcode", barcode);
        }
        if (transaction != null) {
            this.getIntent().putExtra("transaction", transaction);
        }
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new TransactionUpdateFragment())
                .addToBackStack("fragment_transaction_update")
                .commit();
    }

    public void showTransactionListFragment() {
        this.showTransactionListFragment(null);
    }

    public void showTransactionListFragment(Date date) {
        //Pass user object to profile fragment
        if (date != null) {
            this.getIntent().putExtra("date", date.getTime());
        }

        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new TransactionListFragment())
                .addToBackStack("fragment_transactions")
                .commit();
    }

    public void showTransactionSearchFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SearchUsersFragment())
                .addToBackStack("fragment_transaction_search")
                .commit();
    }

    protected void showUserListFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new UserListFragment())
                .addToBackStack("fragment_users")
                .commit();
    }

    public void showUserMapFragment() {
        //Users users
        //this.getIntent().putExtra("users", users);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new UserMapFragment())
                .addToBackStack("fragment_user_map")
                .commit();
        this.setTitle(getResources().getString(R.string.label_map));
    }

    public void showUserNameSearchFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SearchUserFragment())
                .addToBackStack("fragment_user_name_search")
                .commit();
    }

    public void showUserUpdateFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new UserUpdateFragment())
                .addToBackStack("fragment_user_update")
                .commit();
    }

    public void showUserTagSearchFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SearchUsersFragment())
                .addToBackStack("fragment_user_tag_search")
                .commit();
    }

    public void showUserReadFragment(User user) {
        //Note* User object sent by list fragment
        this.getIntent().putExtra("user", user);
        this.getIntent().putExtra("showUpdateButton", false);
        this.getIntent().putExtra("showDeleteButton", false);
        //Load profile fragment
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new UserReadFragment())
                .addToBackStack("fragment_user_read")
                .commit();
        this.setTitle(getResources().getString(R.string.label_user));
    }

    public void showWalletListFragment() {
        this.getIntent().putExtra("wallets", this.authUser.wallets);

        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new WalletListFragment())
                .addToBackStack("fragment_wallets")
                .commit();
    }

    public void showWalletReadFragment(Wallet wallet) {
        if (wallet != null) {
            this.getIntent().putExtra("wallet", wallet);
            this.getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new ConversationListFragment())
                    .addToBackStack("fragment_wallet_read")
                    .commit();
        }
    }

    private void shareWhatsApp() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
        try {
            this.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT);
        }
    }

    public void showSettingFragment() {
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingFragment())
                .addToBackStack("fragment_settings")
                .commit();
    }

    protected void showSavedFiles() {
        String[] savedFiles = getApplicationContext().fileList();
        Log.d("Shared Files", "Print");
        for (int i = 0; i < savedFiles.length; i++) {
            Log.d("File", savedFiles[i]);
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard() {
        //getWindow().setSoftInputMode(
        //        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        //);
        View currentView = this.getCurrentFocus();
        if (currentView != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
        }
    }

    public void showKeyboard() {
        View currentView = this.getCurrentFocus();
        if (currentView != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(currentView.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void hideDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    protected void getTags() {
        GetJSON asyncTask = new GetJSON(this);
        asyncTask.delegate = this;
        asyncTask.execute(this.getString(R.string.get_tags_read), "", "");
    }

    protected void getMyProducts() {
        GetJSON asyncTask = new GetJSON(this);
        asyncTask.delegate = this;
        asyncTask.execute(this.getString(R.string.get_assets_read_mine), "", this.authUser.getToken());
    }

    public void setToolbarUser(String uri, String alias) {
        //Set default image if uri is empty
        if (uri == null || uri.length() == 0) {
            uri = this.getString(R.string.default_user_image);
        }
        //Get controls
        ImageView imgAvatar = (ImageView) this.findViewById(R.id.imgAvatar);
        TextView txtAliasOrName = (TextView) this.findViewById(R.id.txtAlias);
        //Set image
        //Image image = new Image();
        //image.uri = uri;
        //toolbar.setLogo(image.getRoundBitmap());
        //Set alias
        this.setTitle(alias);
    }

    protected void getTransactions() {
        GetJSON asyncTask = new GetJSON(this);
        asyncTask.delegate = this;
        asyncTask.execute(this.getString(R.string.get_transaction_read_many), "", this.authUser.getToken());
    }

    public void getMessages() {
        GetJSON asyncTask = new GetJSON(this);
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.get_message_read_many), "", this.authUser.getToken());
    }

    @TargetApi(23)
    private boolean hasPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((this.checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    && (this.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    && (this.checkSelfPermission(ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)
                    && (this.checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED)
                    && (this.checkSelfPermission(INTERNET) == PackageManager.PERMISSION_GRANTED)
                    && (this.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    && (this.checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                    && (this.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                return true;
            } else {
                return false;
            }
        } else if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @TargetApi(23)
    private void requestPermissions() {
        this.requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, CAMERA, INTERNET,
                READ_EXTERNAL_STORAGE, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() > e2.getX()) {
                // Swipe left (next)
                Log.d("CustomGestureDetector", "Swipe left");
            } else if (e1.getX() < e2.getX()) {
                // Swipe right (previous)
                Log.d("CustomGestureDetector", "Swipe right");
            }
            if (e1.getY() > e2.getY()) {
                // Swipe down
                Log.d("CustomGestureDetector", "Swipe down");
            } else if (e1.getY() < e2.getY()) {
                // Swipe up
                Log.d("CustomGestureDetector", "Swipe up");
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}