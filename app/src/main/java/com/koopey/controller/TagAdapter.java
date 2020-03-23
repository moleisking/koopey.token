package com.koopey.controller;

import android.app.LauncherActivity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.koopey.R;
import com.koopey.model.Tag;
import com.koopey.model.Tags;
import com.koopey.model.User;

/**
 * Created by Scott on 13/10/2016.
 */
public class TagAdapter extends BaseAdapter implements Filterable {
    private final String LOG_HEADER = "TAG:ADAPTER";
    private Context context;
    private String language = "en";
    private LayoutInflater inflater;
    private ArrayList<Tag> availableItems = new ArrayList<>();
    private ArrayList<Tag> selectedItems = new ArrayList<>();
    private ArrayList<Tag> suggestedItems = new ArrayList<>();
    private Filter filter = new CustomFilter();

    public TagAdapter(Context context, Tags availableItems, Tags selectedItems, String language) {
        this.context = context;
        this.language = language;
        if (availableItems != null) {
            this.availableItems = new ArrayList<Tag>(availableItems.getArrayList());
        } else {
            this.availableItems = new ArrayList<Tag>();
        }
        if (selectedItems != null) {
            this.selectedItems = new ArrayList<Tag>(selectedItems.getArrayList());
        } else {
            this.selectedItems = new ArrayList<Tag>();
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public TagAdapter(Context context, Tags availableItems, String language) {
        this.context = context;
        this.language = language;
        if (availableItems != null) {
            this.availableItems = new ArrayList<Tag>(availableItems.getArrayList());
        } else {
            this.availableItems = new ArrayList<Tag>();
        }
        this.selectedItems = new ArrayList<Tag>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return suggestedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return suggestedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return suggestedItems.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Builds the views that form the suggested tags
        View v = convertView;
        if (convertView == null) {
            v = inflater.inflate(R.layout.row_tag, null);
        }
        TextView title = (TextView) v.findViewById(R.id.txtTag);
        title.setText(suggestedItems.get(position).getText(this.language));

        return v;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void setSelectedTags(Tags tags) {
        for (Tag t : tags.getList()) {
            this.selectedItems.add(t);
        }
    }

    private class CustomFilter extends Filter {

        @Override
        public String convertResultToString(Object resultValue) {
            return ((Tag) resultValue).getText(language);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //Clear all previous results, start again
            suggestedItems.clear();
            //Find matching tags
            if (availableItems != null && constraint != null) {
                for (int i = 0; i < availableItems.size(); i++) {
                    if (availableItems.get(i).getText(language).toLowerCase().contains(constraint.toString())) {
                        //Note* Object is ArrayList not Tags so needs custom contains method
                        if (!isDuplicate(availableItems.get(i))) {
                            suggestedItems.add(availableItems.get(i));
                        }
                    }
                }
            }
            //Return results
            FilterResults results = new FilterResults();
            results.values = suggestedItems;
            results.count = suggestedItems.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        public boolean isDuplicate(Tag item) {
            boolean result = false;
            for (int i = 0; i < selectedItems.size(); i++) {
                if (selectedItems.get(i).id.equals(item.id)) {
                    return true;
                }
            }
            return result;
        }
    }
}
