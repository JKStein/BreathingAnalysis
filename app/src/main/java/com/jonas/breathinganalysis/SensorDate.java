package com.jonas.breathinganalysis;

import java.util.Arrays;

/**
 * The result of a measurement of a sensor at a certain point in time.
 * @author Jonas Stein
 */
class SensorDate {
    private long timestamp;
    private float[] values;

    SensorDate(long timestamp, float[] values) {
        this.timestamp = timestamp;
        this.values = values;
    }

    long getTimestamp() {
        return timestamp;
    }

    float[] getValues() {
        return values;
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return timestamp + Arrays.toString(values);
    }

    /**
     * Converts the float array values to a String array.
     * @return A String array containing all sensor values of this object.
     */
    String[] valuesToString() {
        String[] result = new String[this.values.length];
        for(int i = 0; i < this.values.length; i++) {
            result[i] = Float.toString(this.values[i]);
        }
        return result;
    }
}