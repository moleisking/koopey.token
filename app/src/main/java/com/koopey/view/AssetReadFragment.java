package com.koopey.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.CurrencyHelper;
import com.koopey.common.ImageHelper;
import com.koopey.controller.PostJSON;

import com.koopey.controller.TagAdapter;
import com.koopey.model.Alert;
import com.koopey.model.Asset;
import com.koopey.model.AuthUser;
import com.koopey.model.Image;
import com.koopey.model.Tag;
import com.koopey.model.Tags;
import com.koopey.model.Transaction;

/**
 * Created by Scott on 18/01/2017.
 */
public class AssetReadFragment extends Fragment implements PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "ASSET:FRAGMENT";

    private Asset asset = new Asset();
    private AuthUser authUser = new AuthUser();
    private CheckBox txtSold;
    private FloatingActionButton btnMessage, btnPurchase, btnUpdate, btnDelete;
    private ImageView imgAsset, imgAvatar;
    private RatingBar ratAverage;
    private TagAdapter tagAdapter;
    private Tags tags = new Tags();
    private TagTokenAutoCompleteView lstTags;
    private TextView txtAlias, txtCurrency, txtDescription, txtDistance, txtName, txtTitle, txtValue;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set views
        this.btnDelete = (FloatingActionButton) getActivity().findViewById(R.id.btnDelete);
        this.btnMessage = (FloatingActionButton) getActivity().findViewById(R.id.btnMessage);
        this.btnPurchase = (FloatingActionButton) getActivity().findViewById(R.id.btnPurchase);
        this.btnUpdate = (FloatingActionButton) getActivity().findViewById(R.id.btnUpdate);
        this.imgAsset = (ImageView) getActivity().findViewById(R.id.imgAsset);
        this.imgAvatar = (ImageView) getActivity().findViewById(R.id.imgAvatar);
        this.lstTags = (TagTokenAutoCompleteView) getActivity().findViewById(R.id.lstTags);
        this.ratAverage = (RatingBar) getActivity().findViewById(R.id.ratAverage);
        this.txtAlias = (TextView) getActivity().findViewById(R.id.txtAlias);
        this.txtCurrency = (TextView) getActivity().findViewById(R.id.txtCurrency);
        this.txtDescription = (TextView) getActivity().findViewById(R.id.txtDescription);
        this.txtDistance = (TextView) getActivity().findViewById(R.id.txtDistance);
        this.txtName = (TextView) getActivity().findViewById(R.id.txtName);
        this.txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        this.txtValue = (TextView) getActivity().findViewById(R.id.txtValue);

        //Set listeners
        this.btnDelete.setOnClickListener(this);
        this.btnMessage.setOnClickListener(this);
        this.btnPurchase.setOnClickListener(this);
        this.btnUpdate.setOnClickListener(this);
        this.imgAsset.setOnClickListener(this);
        this.imgAvatar.setOnClickListener(this);

        //Show of hide components
        this.populateTags();
        this.populateAsset();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.authUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        if (getActivity().getIntent().hasExtra("asset")) {
            this.asset = (Asset) getActivity().getIntent().getSerializableExtra("asset");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_read, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        this.setVisibility();

        if(this.authUser.equals(this.asset.user)) {
            ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_my_asset));
        }  else {
            ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_asset));
        }
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                } else if (alert.isSuccess()) {
                    if (alert.message.equals("asset.delete")){
                        Toast.makeText(this.getActivity(), getResources().getString(R.string.info_delete), Toast.LENGTH_SHORT).show();
                    } else if (alert.message.equals("asset.update")) {
                        Toast.makeText(this.getActivity(), getResources().getString(R.string.info_update), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnDelete.getId()) {
            this.showDeleteDialog();
        } else        if (v.getId() == btnMessage.getId()) {
            //Send user to main then to message fragment
            this.getActivity().getIntent().putExtra("Asset", asset);
            ((MainActivity) getActivity()).showMessageListFragment();
        } else     if (v.getId() == btnDelete.getId()) {
       //            ((MainActivity) getActivity()).showMessageListFragment();
        } else if (v.getId() == btnPurchase.getId()) {
            Transaction myTransaction = new Transaction();
            ((MainActivity) getActivity()).showTransactionCreateFragment(myTransaction);
        } else if (v.getId() == btnUpdate.getId()) {
            ((MainActivity) getActivity()).showAssetUpdateFragment(this.asset);
        } else if (v.getId() == imgAsset.getId()) {
            this.showImageListFragment();
        } else if (v.getId() == this.imgAvatar.getId()) {
            ((MainActivity) getActivity()).showUserReadFragment(this.asset.user);
        }
    }

    private void populateTags() {
        this.tagAdapter = new TagAdapter(this.getActivity(), this.tags, this.authUser.language);
        this.lstTags.setAdapter(tagAdapter);
        this.lstTags.allowDuplicates(false);
    }

    protected void populateAsset() {
        if (this.asset != null) {
            this.txtAlias.setText(this.asset.user.alias);
            this.txtName.setText(this.asset.user.name);
            this.txtTitle.setText(this.asset.title);
            this.txtDescription.setText(this.asset.description);
            this.txtValue.setText(Double.toString(this.asset.value));
            this.txtCurrency.setText(CurrencyHelper.currencyCodeToSymbol(this.asset.currency));

            for (Tag t : this.asset.tags.getList()) {
                this.lstTags.addObject(t);
            }

            if (this.asset.images.size() > 0) {
                this.imgAsset.setImageBitmap(asset.images.get(0).getBitmap());
            } else {
                this.imgAsset.setImageBitmap(ImageHelper.UriToBitmap(getResources().getString(R.string.default_user_image)));
            }

            if (ImageHelper.isImageUri(this.asset.user.avatar)) {
                this.imgAvatar.setImageBitmap(ImageHelper.IconBitmap(this.asset.user.avatar));
            } else {
                this.imgAvatar.setImageBitmap(ImageHelper.IconBitmap(getResources().getString(R.string.default_user_image)));
            }
        }
    }

    private void postAssetDelete() {
        PostJSON asyncTask = new PostJSON(this.getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(getResources().getString(R.string.post_asset_delete), asset.toString(), authUser.getToken());
        Toast.makeText(this.getActivity(), getResources().getString(R.string.label_delete), Toast.LENGTH_LONG).show();
    }

    public void setVisibility() {
        //Alias
        if (getResources().getBoolean(R.bool.alias) &&
                !getResources().getBoolean(R.bool.alias)) {
            this.txtAlias.setVisibility(View.VISIBLE);
        } else {
            this.txtAlias.setVisibility(View.GONE);
        }
        //Delete
        if (this.authUser.id.equals(this.asset.user.id)) {
            this.btnDelete.setVisibility(View.VISIBLE);
        } else {
            this.btnDelete.setVisibility(View.GONE);
        }
        //Name
        if (getResources().getBoolean(R.bool.name)) {
            this.txtName.setVisibility(View.VISIBLE);
        } else {
            this.txtName.setVisibility(View.GONE);
        }
        //Transactions
        if (getResources().getBoolean(R.bool.transactions)) {
            this.btnPurchase.setVisibility(View.VISIBLE);
        } else {
            this.btnPurchase.setVisibility(View.GONE);
        }
        //Update
        if (this.authUser.id.equals(this.asset.user.id)) {
            this.btnUpdate.setVisibility(View.VISIBLE);
        } else {
            this.btnUpdate.setVisibility(View.GONE);
        }
    }

    public void showImageListFragment() {
        this.getActivity().getIntent().putExtra("images", asset.images);
        this.getActivity().getIntent().putExtra("showCreateButton", false);
        this.getActivity().getIntent().putExtra("showUpdateButton", false);
        this.getActivity().getIntent().putExtra("showDeleteButton", false);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new ImageListFragment())
                .addToBackStack("fragment_images")
                .commit();
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.label_delete))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                       postAssetDelete();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}
