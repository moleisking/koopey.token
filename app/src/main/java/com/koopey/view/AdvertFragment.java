package com.koopey.view;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import com.koopey.R;
import com.koopey.model.Advert;

/**
 * Created by Scott on 02/11/2017.
 */

public class AdvertFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private final String LOG_HEADER = "ADV:VW";
    public OnAdvertChangeListener delegate = null;
    RadioGroup.OnCheckedChangeListener radGrplistener;
    private EditText txtValue;
    private RadioGroup optsPeriod;
    private RadioButton optNone, optDay, optWeek, optMonth;
    private Advert advert;
    private Context context;
    private LinearLayout layoutAdvert;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.layoutAdvert = (LinearLayout) getActivity().findViewById(R.id.layoutAdvert);
        this.txtValue = (EditText) getActivity().findViewById(R.id.txtValue);
        this.optsPeriod = (RadioGroup) getActivity().findViewById(R.id.optsPeriod);
        this.optNone = (RadioButton) getActivity().findViewById(R.id.optNone);
        this.optDay = (RadioButton) getActivity().findViewById(R.id.optDay);
        this.optWeek = (RadioButton) getActivity().findViewById(R.id.optWeek);
        this.optMonth = (RadioButton) getActivity().findViewById(R.id.optMonth);
        this.advert = new Advert();
        this.optsPeriod.setOnCheckedChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advert, container, false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        try {
            if (checkedId == optNone.getId()) {
                this.txtValue.setText("0");
                this.advert.type = "none";
                this.advert.startTimeStamp = System.currentTimeMillis();
                this.advert.endTimeStamp = System.currentTimeMillis();
                this.delegate.updateAdvertEvent(advert);
            } else if (checkedId == optDay.getId()) {
                this.advert.type = "day";
                this.advert.startTimeStamp = System.currentTimeMillis();
                this.advert.endTimeStamp = this.advert.startTimeStamp + TimeUnit.DAYS.toMillis(1);
                this.txtValue.setText(String.valueOf(getResources().getInteger(R.integer.advert_day_value)));
                this.delegate.updateAdvertEvent(advert);
            } else if (checkedId == optWeek.getId()) {
                this.advert.type = "week";
                this.advert.startTimeStamp = System.currentTimeMillis();
                this.advert.endTimeStamp = this.advert.startTimeStamp + TimeUnit.DAYS.toMillis(7);
                this.txtValue.setText(String.valueOf(getResources().getInteger(R.integer.advert_week_value)));
                this.delegate.updateAdvertEvent(advert);
            } else if (checkedId == optMonth.getId()) {
                this.advert.type = "month";
                this.advert.startTimeStamp = System.currentTimeMillis();
                this.advert.endTimeStamp = this.advert.startTimeStamp + TimeUnit.DAYS.toMillis(30);
                this.txtValue.setText(String.valueOf(getResources().getInteger(R.integer.advert_month_value)));
                this.delegate.updateAdvertEvent(advert);
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public void setOnAdvertChangeListener(OnAdvertChangeListener delegate) {
        this.delegate = delegate;
    }

    public interface OnAdvertChangeListener {
        void updateAdvertEvent(Advert advert);
    }
}
