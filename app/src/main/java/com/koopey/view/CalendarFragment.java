package com.koopey.view;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.koopey.R;
import com.koopey.common.SerializeHelper;
import com.koopey.model.Transactions;

/**
 * Created by Scott on 13/07/2017.
 * http://abhiandroid.com/ui/calendarview
 * https://github.com/SundeepK/CompactCalendarView
 */
public class CalendarFragment extends Fragment implements View.OnClickListener, CompactCalendarView.CompactCalendarViewListener {
    private final String LOG_HEADER = "CAL:FT";
    private Transactions transactions;
    private ImageButton btnMonthNext, btnMonthPrevious;
    private CompactCalendarView vwCalendar;
    private FloatingActionButton btnCreate;
    private TextView txtMonth;
    private ActionBar toolbar;
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.label_calendar));
        ((MainActivity) getActivity()).hideKeyboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getIntent().hasExtra("transactions")) {
            this.transactions = (Transactions) getActivity().getIntent().getSerializableExtra("transactions");
        } else if (SerializeHelper.hasFile(this.getActivity(), Transactions.TRANSACTIONS_FILE_NAME)) {
            transactions = (Transactions) SerializeHelper.loadObject(this.getActivity(), Transactions.TRANSACTIONS_FILE_NAME);
        } else {
            transactions = new Transactions();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.vwCalendar = (CompactCalendarView) getActivity().findViewById(R.id.compactcalendar_view);
        this.btnMonthPrevious = (ImageButton) getActivity().findViewById(R.id.btnMonthPrevious);
        this.btnMonthNext = (ImageButton) getActivity().findViewById(R.id.btnMonthNext);
        //toolbar = (ActionBar) getActivity().findViewById(R.id.toolbar);
        this.btnCreate = (FloatingActionButton) getActivity().findViewById(R.id.btnCreate);
        this.txtMonth = (TextView) getActivity().findViewById(R.id.txtMonth);
        //Set controls
        //((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        this.txtMonth.setText(dateFormatForMonth.format(new Date()));
        //Set listeners
        this.btnCreate.setOnClickListener(this);
        this.vwCalendar.setListener(this);
        this.btnMonthPrevious.setOnClickListener(this);
        this.btnMonthNext.setOnClickListener(this);
        //Load data
        this.populateCalendar();
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == btnMonthNext.getId()) {
                this.vwCalendar.showNextMonth();
            } else if (v.getId() == btnMonthPrevious.getId()) {
                this.vwCalendar.showPreviousMonth();
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
    }

    @Override
    public void onDayClick(Date dateClicked) {
        ((MainActivity) getActivity()).showTransactionListFragment(dateClicked);
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        this.txtMonth.setText(dateFormatForMonth.format(firstDayOfNewMonth));
    }

    private void populateCalendar() {
        for (int i = 0; i <= transactions.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 14);
            calendar.set(Calendar.MONTH, 10);
            calendar.set(Calendar.YEAR, 2017);
            calendar.set(Calendar.HOUR_OF_DAY, 17);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Event event = new Event(Color.GREEN, calendar.getTimeInMillis());
            this.vwCalendar.addEvent(event);
        }
        this.vwCalendar.invalidate();
    }
}