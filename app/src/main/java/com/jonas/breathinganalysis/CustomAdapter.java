package com.jonas.breathinganalysis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Connection between the list view and the content of its list.
 * @author Jonas Stein
 */
class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ListEntry> listEntries;

    CustomAdapter(Context context, ArrayList<ListEntry> listEntries) {
        this.context = context;
        this.listEntries = listEntries;
    }

    @Override
    public int getCount() {
        return listEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return listEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * Get a View that displays the data at the specified position in the data set.
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param viewGroup The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sensor_entry, viewGroup, false);
        }

        TextView sensorEntryName = convertView.findViewById(R.id.sensorEntryName);
        TextView sensorEntryValue = convertView.findViewById(R.id.sensorEntryValue);

        if(listEntries.size() <= 0) {
            sensorEntryName.setText(R.string.noData);
            sensorEntryValue.setText(R.string.noData);
        }
        else {
            ListEntry listEntry = listEntries.get(position);
            sensorEntryName.setText(listEntry.getSensorEntryName());
            sensorEntryValue.setText(listEntry.getSensorValue());
        }
        return convertView;
    }

    /**
     * Updates the values of all list elements with the supplied string array.
     * @param values The new values.
     */
    void setNewValues(String[] values) {
        if(values.length == listEntries.size()) {
            for (int i = 0; i < values.length; i++) {
                listEntries.get(i).setSensorValue(values[i]);
            }
        }
        notifyDataSetChanged();
    }
}