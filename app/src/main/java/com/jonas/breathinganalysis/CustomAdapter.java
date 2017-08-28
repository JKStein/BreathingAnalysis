package com.jonas.breathinganalysis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


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


    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sensor_entry, viewGroup, false);
        }

        TextView sensorEntryName = (TextView) convertView.findViewById(R.id.sensorEntryName);
        TextView sensorEntryValue = (TextView) convertView.findViewById(R.id.sensorEntryValue);

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

    void setNewValue(int position, String value) {
        listEntries.get(position).setSensorValue(value);
        notifyDataSetChanged();
    }
}