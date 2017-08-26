package com.jonas.breathinganalysis;

import java.util.ArrayList;

/**
 * @author Jonas Stein
 */
class MeasurementSeries {
    private ArrayList<SensorDate> sensorData;
    private String[] values;


    MeasurementSeries(ArrayList<SensorDate> sensorData, String[] values) throws IllegalArgumentException{
        this.sensorData = sensorData;


        if(sensorData.size() == 0 || sensorData.get(0) != null && sensorData.get(0).getValues().length == values.length) {
            this.values = values;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    ArrayList<SensorDate> getSensorData() {
        return sensorData;
    }

    String[] getValues() {
        return values;
    }


    String[] contains(long timestamp) {
        for(int i = 0; i < this.sensorData.size(); i++) {
            SensorDate sensorDate = this.sensorData.get(i);
            long foundTimestamp = sensorDate.getTimestamp();
            if(foundTimestamp == timestamp) {
                String[] result = sensorDate.valuesToString().clone();
                this.sensorData.remove(sensorDate);
                return result;
            }
            else if(foundTimestamp > timestamp) {
                return new String[values.length];
            }
        }
        return new String[values.length];
    }
}