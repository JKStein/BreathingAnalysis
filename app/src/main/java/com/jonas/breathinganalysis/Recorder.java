package com.jonas.breathinganalysis;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Jonas Stein
 */

abstract class Recorder extends Fragment {

    /**
     * The values collected by the sensor.
     */
    private ArrayList<SensorDate> sensorData;
    /**
     * Only if this attribute is true, the measured values will be stored.
     */
    private static boolean recording;

    /**
     * This default sensor name is used for the ListView in case none is provided.
     */
    static final String DEFAULT_SENSOR_NAME = "Unnamed Sensor";
    /**
     * This default sensor entry name is used for the ListView in case none is provided.
     */
    static final String DEFAULT_ENTRY_NAME = "Unnamed Entry";

    /**
     * The name a sensor will be called if none is supplied.
     */
    private String sensorName;
    /**
     * The names of the sensor entries.
     */
    private String[] entryNames;
    /**
     * The Context of this Fragment.
     */
    private Context context;
    /**
     * The BaseAdapter between the View and the data.
     */
    private CustomAdapter customAdapter;


    /**
     * Instantiates all needed attributes.
     */
    Recorder() {
        this.sensorData = new ArrayList<>();
        recording = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.generalized_sensor_fragment, container, false);

        ArrayList<ListEntry> listEntries = new ArrayList<>();

        ((TextView) view.findViewById(R.id.sensorName)).setText(this.sensorName);

        //Add a default entry to the list.
        for (String entryName : entryNames) {
            listEntries.add(new ListEntry(entryName));
        }

        ListView list = view.findViewById(R.id.non_scrolling_list_view);

        this.customAdapter = new CustomAdapter(this.context, listEntries);

        list.setAdapter(customAdapter);

        return view;
    }


    /**
     * Getter for the {@link java.util.ArrayList} containing the captured sensor data.
     * @return An {@link java.util.ArrayList} containing the captured sensor data.
     */
    ArrayList<SensorDate> getSensorData() {
        return this.sensorData;
    }

    /**
     * Starts the scoring of all measured sensor data.
     */
    static void startRecording() {
        recording = true;
    }

    /**
     * Stop the scoring of all measured sensor data.
     */
    static void stopRecording() {
        recording = false;
    }

    /**
     * Enables storing a new series of measurement by deleting all old data.
     */
    void clearSensorData() {
        sensorData.clear();
    }

    String getSensorName() {
        return this.sensorName;
    }

    void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    void setEntryNames(String[] entryNames) {
        this.entryNames = entryNames;
    }

    String[] getEntryNames() {
        return entryNames;
    }

    private void update(float[] updateValues) {
        String[] update = new String[updateValues.length];

        for(int i = 0; i < updateValues.length; i++) {
            //Float.toString() could not work properly, because the separator (dot or comma) is unknown.
            update[i] = String.format(Locale.US, "%f", updateValues[i]);
        }
        customAdapter.setNewValues(update);
    }

    void update(final long timestamp, final float[] updateValues, boolean runOnUi) {
        if(runOnUi) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    update(updateValues);
                }
            });
        }
        else {
            update(updateValues);
        }

        if(recording) {
            //Add new values to the series of measurement if recording.
            this.sensorData.add(new SensorDate(timestamp, updateValues));
        }
    }
}
