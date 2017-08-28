package com.jonas.breathinganalysis;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class CustomListViewAndroidExample extends Activity {

    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout_no_scroll);

        ArrayList<ListEntry> listEntries = new ArrayList<>();

        //Add default entries to the list.
        for (int i = 0; i < 11; i++) {
            listEntries.add(new ListEntry("test " + i));
        }

        ListView list = (ListView) findViewById(R.id.non_scrolling_list_view);

        customAdapter = new CustomAdapter(getApplicationContext(), listEntries);

        list.setAdapter(customAdapter);

        test();
    }

    void test() {
        customAdapter.setNewValue(4,"ass");
    }
}