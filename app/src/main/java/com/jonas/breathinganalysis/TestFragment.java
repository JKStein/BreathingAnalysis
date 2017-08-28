package com.jonas.breathinganalysis;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Jonas Stein
 */

public class TestFragment extends Fragment {

    TextView textView1, textView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this sensor_fragment
        final View view = inflater.inflate(R.layout.test_layout_no_scroll, container, false);
        String[] array = {"hello1", "hello2"};

        NonScrollListView non_scroll_list = (NonScrollListView) view.findViewById(R.id.non_scrolling_list_view);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.sensor_entry, array);
        textView1 = (TextView) view.findViewById(R.id.sensorEntryName);
        textView2 = (TextView) view.findViewById(R.id.sensorEntryValue);

        non_scroll_list.setAdapter(arrayAdapter);

        array[0] = "a";
        array[1] = "b";
        arrayAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
