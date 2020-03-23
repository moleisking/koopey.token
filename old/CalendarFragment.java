package me.minitrabajo.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CalendarView;

import java.util.ArrayList;
import java.util.Date;

import me.minitrabajo.R;
import me.minitrabajo.common.Utility;
import me.minitrabajo.controller.TransactionAdapter;
import me.minitrabajo.model.Transaction;
import me.minitrabajo.model.Transactions;

/**
 * Created by Scott on 13/07/2017.
 * http://abhiandroid.com/ui/calendarview
 */
public class CalendarFragment extends Fragment implements View.OnClickListener, CustomCalendarView.OnDateChangeListener {
    private final String LOG_HEADER = "CAL:FT";
    private Transactions myTransactions;
    private CustomCalendarView btnCalendar;
    private FloatingActionButton btnCreate;
    private int todayFirstClicked = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Define transactions object
        //Note* MyTransactionCreate will create stored MyTransactions if it does not exist
        if (Utility.hasFile(this.getActivity(), Transactions.MY_TRANSACTIONS_FILE_NAME)) {
            Log.w(LOG_HEADER + ":ON:CR", "MyTransactions found");
            myTransactions = new Transactions();
            myTransactions = (Transactions) Utility.loadObject(this.getActivity(), Transactions.MY_TRANSACTIONS_FILE_NAME);
            if (myTransactions != null) {
                myTransactions.print();
                Log.w(LOG_HEADER + "ON:CR", "MyTransactions found true");
            } else {
                myTransactions = new Transactions();
                Log.w(LOG_HEADER + "ON:CR", "MyTransactions found false");
            }
        } else {
            myTransactions = new Transactions();
            Log.w(LOG_HEADER + "CE:ER", "MyTransactions not found");
        }
        todayFirstClicked =0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnCalendar = (CustomCalendarView) getActivity().findViewById(R.id.calendar);
        btnCreate = (FloatingActionButton) getActivity().findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(this);
        btnCalendar.setOnDateChangeListener(this);
        btnCalendar.setSameSelectedDayChangeListener(this);
        //Used to detect if today was clicked, which is not available in OnDateChangeListener
        drawTransactions();
        Log.w(LOG_HEADER + ":ON:ACR", "MyTransaction list loaded with data.");
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == btnCreate.getId()) {
                ((MainActivity) getActivity()).showTransactionCreateFragment();
            } else if (v.getId() == btnCalendar.getId()) {
                Log.d(LOG_HEADER + ":ON:CLK:CAL", "onClick()");
            } else {
                Log.d(LOG_HEADER + ":ON:CLK:OTH", "onClick()");
            }
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ON:CLK:ER", ex.getMessage());
        }
    }

    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
        Log.w(LOG_HEADER + ":ON:SELECT",new Date(view.getDate()).toString());
       // ((MainActivity) getActivity()).showMyTransactionsFragment(new Date(view.getDate()));
    }

    @Override
    public void onSameSelectedDayChange(CalendarView view, int year, int month, int day) {
        Log.w(LOG_HEADER + ":ON:SAME:SELECT", new Date(view.getDate()).toString());
        //((MainActivity) getActivity()).showMyTransactionsFragment(new Date(view.getDate()));
    }

    private void drawTransactions(){

       /* for (int i = 0; i < myTransactions.size(); i++){
            String text = btnCalendar.getTouchables().get(0).toString();

           Log.i("CAL:VW:BUT", text ) ;
          //  myTransactions.get(i).name;
        }*/
    }
}
