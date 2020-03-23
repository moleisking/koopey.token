package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.GetJSON;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Asset;
import com.koopey.model.Review;
import com.koopey.model.User;

/**
 * Created by Scott on 03/02/2017.
 */
public class ReviewCreateFragment extends Fragment implements PostJSON.PostResponseListener, View.OnClickListener {

    private final String LOG_HEADER = "REVIEW:CREATE";
    private EditText txtComment;
    // private RatingBar ratReview;
    private AuthUser myUser;
    private User user;
    private Asset asset;
    private Review review;
    private FloatingActionButton btnThumbUp, btnThumbDown, btnCancel;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.txtComment = (EditText) getActivity().findViewById(R.id.txtComment);
        this.btnThumbUp = (FloatingActionButton) getActivity().findViewById(R.id.btnThumbUp);
        this.btnThumbDown = (FloatingActionButton) getActivity().findViewById(R.id.btnThumbDown);
        this.btnCancel = (FloatingActionButton) getActivity().findViewById(R.id.btnCancel);
        //ratReview = (RatingBar) getActivity().findViewById(R.id.ratReview);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_review));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onClick(View v) {
        try {
            this.review = new Review();
            this.review.judgeId = this.myUser.id;
            this.review.comment = this.txtComment.getText().toString();
            if (v.getId() == btnThumbUp.getId()) {
                this.review.value = 1;
                this.postCreateReview();
            } else if (v.getId() == btnThumbDown.getId()) {
                this.review.value = 0;
                this.postCreateReview();
            } else if (v.getId() == btnCancel.getId()) {
                ((MainActivity) getActivity()).showUserReadFragment(user);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.myUser = ((MainActivity) getActivity()).getAuthUserFromFile();

        if (this.getActivity().getIntent().hasExtra("user")) {
            this.user = (User) getActivity().getIntent().getSerializableExtra("user");
            this.review.userId = this.user.id;
        }

        if (this.getActivity().getIntent().hasExtra("asset")) {
            this.asset = (Asset) getActivity().getIntent().getSerializableExtra("asset");
            this.review.productId = this.asset.id;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_create, container, false);
    }

    @Override
    public void onPostResponse(String output) {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("review")) {
                Review review = new Review();
                review.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), review);
                //Start phase two
                //this.postCreateReview();
            } else if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_create), Toast.LENGTH_SHORT).show();
                } else if (alert.isSuccess()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_create), Toast.LENGTH_SHORT).show();
                    if (this.asset != null) {
                        ((MainActivity) getActivity()).showAssetReadFragment(asset);
                    } else if (this.user != null) {
                        ((MainActivity) getActivity()).showUserReadFragment(user);
                    }
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

   /* private void postReadReview() {
         if (this.asset != null) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            asyncTask.delegate = this;
            asyncTask.execute(getResources().getString(R.string.post_review_create), this.review.toString(), this.myUser.getToken());
        }
    }*/

    private void postCreateReview() {
        if (this.asset != null) {
            PostJSON asyncTask = new PostJSON(this.getActivity());
            asyncTask.delegate = this;
            asyncTask.execute(getResources().getString(R.string.post_review_create), this.review.toString(), this.myUser.getToken());
        }
    }

    public interface OnReviewCreateFragmentListener {
        void onReviewCreate(Review review);

    }
}
