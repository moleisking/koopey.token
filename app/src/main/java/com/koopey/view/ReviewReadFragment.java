package com.koopey.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.controller.PostJSON;
import com.koopey.model.Alert;
import com.koopey.model.AuthUser;
import com.koopey.model.Review;
import com.koopey.model.User;

/**
 * Created by Scott on 03/02/2017.
 */
public class ReviewReadFragment extends Fragment implements PostJSON.PostResponseListener  {

    private final String LOG_HEADER = "REVIEW:READ";
    private EditText txtComment;
    private RatingBar ratReview;
    private AuthUser authUser;
    private Review review;
    private FloatingActionButton btnThumbUp, btnThumbDown;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.txtComment = (EditText)getActivity().findViewById(R.id.txtComment);
        this.btnThumbUp = (FloatingActionButton)getActivity().findViewById(R.id.btnThumbUp);
        this.btnThumbDown = (FloatingActionButton)getActivity().findViewById(R.id.btnThumbDown);
        //ratReview = (RatingBar) getActivity().findViewById(R.id.ratReview);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)    {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_review));
        ((MainActivity) getActivity()).hideKeyboard();

        this.authUser = ((MainActivity)getActivity()).getAuthUserFromFile();
        if (this.getActivity().getIntent().hasExtra("review")) {
            this.review = (Review) getActivity().getIntent().getSerializableExtra("review");
        } else if (SerializeHelper.hasFile(this.getActivity(), Review.REVIEW_FILE_NAME)) {
            this.review = (Review) SerializeHelper.loadObject(this.getActivity(), Review.REVIEW_FILE_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)    {
        return inflater.inflate(R.layout.fragment_review_read, container, false);
    }

    @Override
    public void onPostResponse(String output)    {
        try {
            String header = (output.length() >= 20) ? output.substring(0, 19).toLowerCase() : output;
            if (header.contains("review")) {
                Review review = new Review();
                review.parseJSON(output);
                SerializeHelper.saveObject(this.getActivity(), review);
            } else if (header.contains("alert")) {
                Alert alert = new Alert();
                alert.parseJSON(output);
                if (alert.isError()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.error_create), Toast.LENGTH_SHORT).show();
                } else if (alert.isSuccess()) {
                    Toast.makeText(this.getActivity(), getResources().getString(R.string.info_create), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            Log.w(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if (this.review != null){
            this.txtComment.setText(this.review.comment);
            if(this.review.value == 0){
                this.btnThumbDown.setVisibility(View.VISIBLE);
                this.btnThumbUp.setVisibility(View.GONE);
            } else if(this.review.value >= 1){
                this.btnThumbDown.setVisibility(View.GONE);
                this.btnThumbUp.setVisibility(View.VISIBLE);
            }
        }
    }
}
