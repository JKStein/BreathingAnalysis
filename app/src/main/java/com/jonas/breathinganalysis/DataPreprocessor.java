package com.jonas.breathinganalysis;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Normalizes and merges all recorded data into the appropriate file structure.
 */
class DataPreprocessor {
    /**
     * Deletes all sensor data entries of one SensorDate with redundant timestamps.
     * This usually has no effect at all, because the sample rate is at maximum 2ms.
     * In the case of more than one sensor update happening during a millisecond,
     * this method deletes all 'redundant' (jet different)
     * data entries but the first of each millisecond
     * @param seriesOfMeasurements An ArrayList containing each MeasurementSeries.
     */
    static void removeRedundancies(ArrayList<MeasurementSeries> seriesOfMeasurements) {
        for (MeasurementSeries measurementSeries : seriesOfMeasurements) {
            ArrayList<SensorDate> list = measurementSeries.getSensorData();
            if(list != null) {
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0 && list.get(i - 1).getTimestamp() >= list.get(i).getTimestamp()) {
                        list.remove(i);
                        i--;
                        //without the i-- we would miss every list entry right after a redundant one.
                        Log.d(TAG, "A sensor data redundancy has been eliminated! (Position in the List: " + i + ")");
                    }
                }
            }
        }
    }

    /**
     * Synchronizes the timestamps of each measurement to the beats of the metronome.
     * The first beat will happen at 0ms, the last one at measuredData.getOverallDuration().
     * @param seriesOfMeasurements An ArrayList containing each MeasurementSeries.
     * @param startTimestamp The calculated start timestamp of the metronome producing the least
     *                       time shift errors for the metronome.
     */
    static void normalizeTimestamps(ArrayList<MeasurementSeries> seriesOfMeasurements, long startTimestamp) {
        for (MeasurementSeries measurementSeries : seriesOfMeasurements) {
            for (SensorDate sensorDate : measurementSeries.getSensorData()) {
                sensorDate.setTimestamp(sensorDate.getTimestamp() - startTimestamp);
            }
        }
    }

    /**
     * Calculates the timestamp of the earliest measurement.
     * @param seriesOfMeasurements Each Sensors measurements.
     * @return The smallest start timestamp of all captured sensor data.
     *          Returns Long.MAX_VALUE if empty data has been passed.
     */
    static long getSmallestStartTimestamp(ArrayList<MeasurementSeries> seriesOfMeasurements) {
        long smallest = Long.MAX_VALUE;

        for (MeasurementSeries measurementSeries : seriesOfMeasurements) {
            if(measurementSeries.getSensorData().size() > 0) {
                long timestamp = measurementSeries.getSensorData().get(0).getTimestamp();
                if(smallest > timestamp) {
                    smallest = timestamp;
                }
            }
        }
        return smallest;
    }

    /**
     * Calculates the timestamp of the latest measurement.
     * @param seriesOfMeasurements Each Sensors measurements.
     * @return The biggest start timestamp of all captured sensor data.
     *          Returns Long.MIN_VALUE if empty data has been passed.
     */
    static long getBiggestEndTimestamp(ArrayList<MeasurementSeries> seriesOfMeasurements) {
        long biggest = Long.MIN_VALUE;

        for (MeasurementSeries measurementSeries : seriesOfMeasurements) {
            if(measurementSeries.getSensorData().size() > 0) {
                ArrayList<SensorDate> sensorData = measurementSeries.getSensorData();
                long timestamp = sensorData.get(sensorData.size() - 1).getTimestamp();
                if (biggest < timestamp) {
                    biggest = timestamp;
                }
            }
        }
        return biggest;
    }

    /**
     * Produces the content for the CSV file.
     * @param seriesOfMeasurements An ArrayList of all recorded measurements.
     * @param smallestStartTimestamp The timestamp of the earliest measurement by a recorder.
     * @param biggestEndTimestamp  The timestamp of the last measurement by a recorder.
     * @return The CSV file content.
     * */
    static List<String[]> getMeasuredDataSequence(ArrayList<MeasurementSeries> seriesOfMeasurements,
                                                  long smallestStartTimestamp,
                                                  long biggestEndTimestamp) {
        List<String[]> measuredDataSequence = new ArrayList<>();

        String[] columnHeadings = {"timestamp"};

        for (MeasurementSeries measurementSeries : seriesOfMeasurements) {
            columnHeadings = ArrayUtils.addAll(columnHeadings,measurementSeries.getSensorEntryNames());
        }

        measuredDataSequence.add(columnHeadings);

        for(long i = smallestStartTimestamp; i < biggestEndTimestamp; i++) {

            String[] measurementAtASpecificTimestamp = {String.valueOf(i)};

            for (MeasurementSeries measurementSeries : seriesOfMeasurements) {
                measurementAtASpecificTimestamp = ArrayUtils.addAll(measurementAtASpecificTimestamp,measurementSeries.contains(i));
            }

            measuredDataSequence.add(measurementAtASpecificTimestamp);
        }

        return measuredDataSequence;
    }

    /**
     * Produces the content for the ARFF file.
     * @param features The supplied features as an ArrayList of feature vectors.
     * @return The ARFF file content.
     */
    static List<String> getFeatures(ArrayList<Feature> features) {
        List<String> lines = new ArrayList<>();

        lines.add("@relation breathing-analysis\n");

        for (Feature feature : features) {
            lines.add("@attribute " + feature.getFeatureName() + " " + feature.getFeatureType() + "\n");
        }

        lines.add("@data\n");

        String values = "";

        for (Feature feature : features) {
            values += feature.getFeatureValue() + ",";
        }

        values = values.substring(0, values.length() - 1);

        lines.add(values + "\n");

        return lines;
    }
}
