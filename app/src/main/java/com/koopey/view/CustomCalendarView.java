package com.koopey.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;

import java.util.Date;

/**
 * Created by Scott on 19/07/2017.
 */

public class CustomCalendarView extends CalendarView {

    private final String LOG_HEADER = "CAL:VW";
    private Date previousSelectedDate = new Date();
    private OnDateChangeListener listener;
    private CheckSameSelectedDateAsyncTask task = new CheckSameSelectedDateAsyncTask();

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(Context context, AttributeSet attribute) {
        super(context, attribute);
    }

    public CustomCalendarView(Context context, AttributeSet attribute, int defStyle) {
        super(context, attribute, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if ((task.getStatus() == AsyncTask.Status.PENDING) || (task.getStatus() == AsyncTask.Status.FINISHED)) {
            task = new CheckSameSelectedDateAsyncTask();
            task.execute();
        }
        return false;
    }

    private void checkSameSelectedDate() {
        Date selectedDate = new Date(super.getDate());
        if (selectedDate.getDay() == previousSelectedDate.getDay() &&
                selectedDate.getMonth() == previousSelectedDate.getMonth() &&
                selectedDate.getYear() == previousSelectedDate.getYear()) {
            if (listener != null) {
                this.listener.onSameSelectedDayChange(this, selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDay());
            }
        }
        this.previousSelectedDate = selectedDate;
    }

    public void setSameSelectedDayChangeListener(OnDateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnDateChangeListener extends CalendarView.OnDateChangeListener {
        void onSameSelectedDayChange(CalendarView view, int year, int month, int day);
    }

    private class CheckSameSelectedDateAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                //Note: Breaking point between 75 - 100
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            checkSameSelectedDate();
        }
    }
}
