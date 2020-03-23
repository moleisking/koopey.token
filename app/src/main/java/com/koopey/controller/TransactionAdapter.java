package com.koopey.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koopey.R;
import com.koopey.model.Transaction;
import com.koopey.model.Transactions;

/**
 * Created by Scott on 16/02/2017.
 */
public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private final String LOG_HEADER = "TRANSACTION:ADAPTER";
    private ImageView imgState;
    private TextView txtName, txtTotal, txtCurrency;
    private Transaction transaction;

    public TransactionAdapter(Context context, Transactions transactions) {
        super(context, 0, transactions.getList());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            // Get the data item for this position
            this.transaction = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_transaction, parent, false);
            }
            // Lookup view for data population
            this.imgState = (ImageView) convertView.findViewById(R.id.imgState);
            this.txtName = (TextView) convertView.findViewById(R.id.txtName);
            this.txtTotal = (TextView) convertView.findViewById(R.id.txtTotal);
            this.txtCurrency = (TextView) convertView.findViewById(R.id.txtCurrency);
            // Populate the data into the template view using the data object
            this.populateTransaction();
            // Return the completed view to render on screen
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":ER", ex.getMessage());
        }
        return convertView;
    }

    private void populateTransaction() {
        this.txtName.setText(transaction.name);
        this.txtTotal.setText(Double.toString(transaction.totalValue));
        this.txtCurrency.setText(transaction.currency);
        if (this.transaction.isReceipt()) {
            Drawable imgPaid = getContext().getDrawable(R.drawable.ic_done_all_black_24dp);
            imgPaid.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            this.imgState.setImageDrawable(imgPaid);
        } else if (this.transaction.isQuote()) {
            Drawable imgUnpaid = getContext().getDrawable(R.drawable.ic_clear_black_24dp);
            imgUnpaid.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            this.imgState.setImageDrawable(imgUnpaid);
        } else if (this.transaction.isInvoice()) {
            Drawable imgUnpaid = getContext().getDrawable(R.drawable.ic_done_black_24dp);
            imgUnpaid.setColorFilter(Color.rgb(255, 128,0), PorterDuff.Mode.SRC_IN);//getContext().getColor(R.color.color_orange)
            this.imgState.setImageDrawable(imgUnpaid);
        }
    }
}
