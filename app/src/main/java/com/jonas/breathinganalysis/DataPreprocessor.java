package com.jonas.breathinganalysis;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

class DataPreprocessor {

    DataPreprocessor(MeasuredData measuredData) {
        ArrayList<MeasurementSeries> seriesOfMeasurements = measuredData.getAllMeasuredData();
        removeRedundancies(seriesOfMeasurements);
        normalizeTimestamps(seriesOfMeasurements, measuredData.getBestFittingStartTimestamp());
        measuredData.setBiggestEndTimestamp(getBiggestEndTimestamp(seriesOfMeasurements));
        measuredData.setSmallestStartTimestamp(getSmallestStartTimestamp(seriesOfMeasurements));
        instantiateMeasuredDataSequenceNew(measuredData);
    }

    /**
     * Deletes all sensor data entries of one SensorDate with redundant timestamps.
     * This usually has no effect at all, because the sample rate is at maximum 2ms.
     * In the case of more than one sensor update happening during a millisecond,
     * this method deletes all 'redundant' (jet different)
     * data entries but the first of each millisecond
     * @param seriesOfMeasurements An ArrayList containing each MeasurementSeries.
     */
    private void removeRedundancies(ArrayList<MeasurementSeries> seriesOfMeasurements) {
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
    private void normalizeTimestamps(ArrayList<MeasurementSeries> seriesOfMeasurements, long startTimestamp) {
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
    private long getSmallestStartTimestamp(ArrayList<MeasurementSeries> seriesOfMeasurements) {
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
    private long getBiggestEndTimestamp(ArrayList<MeasurementSeries> seriesOfMeasurements) {
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
     * For .csv file.
     * @param measuredData
     */
    private static void instantiateMeasuredDataSequenceNew(MeasuredData measuredData) {
        List<String[]> measuredDataSequence = new ArrayList<>();

        String[] columnHeadings = {"Timestamp"};

        for (MeasurementSeries measurementSeries : measuredData.getAllMeasuredData()) {
            columnHeadings = ArrayUtils.addAll(columnHeadings,measurementSeries.getValues());
        }

        measuredDataSequence.add(columnHeadings);

        for(long i = measuredData.getSmallestStartTimestamp(); i < measuredData.getBiggestEndTimestamp(); i++) {

            String[] measurementAtASpecificTimestamp = {String.valueOf(i)};

            for (MeasurementSeries measurementSeries : measuredData.getAllMeasuredData()) {
                measurementAtASpecificTimestamp = ArrayUtils.addAll(measurementAtASpecificTimestamp,measurementSeries.contains(i));
            }

            measuredDataSequence.add(measurementAtASpecificTimestamp);
        }

        measuredData.setMeasuredDataSequence(measuredDataSequence);
    }

    /**
     * For .arff file.
     * @param featureVectors the supplied features as an arraylist of feature vectors
     * @return arff file content
     */
    List<String> getFeatures(ArrayList<FeatureVector> featureVectors) {
        List<String> lines = new ArrayList<>();

        lines.add("@relation breathingAnalysis");


        lines.add("@attribute " + featureVectors.get(0).getFeatureName() + " {'" + featureVectors.get(0).getFeatureValue() + "'}");

        String exerciseIds = "";
        for (int i = 0; i < BreathingAnalysis.EXERCISE_IDS.length; i++) {
            if(i != 0) {
                exerciseIds += ",'" + BreathingAnalysis.EXERCISE_IDS[i] + "'";
            }
            else {
                exerciseIds = "'" + BreathingAnalysis.EXERCISE_IDS[i] + "'";
            }
        }

        lines.add("@attribute " + featureVectors.get(1).getFeatureName() + " " +
                "{" + exerciseIds + "}");

        for (int i = 2; i < featureVectors.size(); i++) {
            lines.add("@attribute " + featureVectors.get(i).getFeatureName() + " numeric");
        }




        lines.add("@data");

        String values = "'" + featureVectors.get(0).getFeatureValue() + "'," +
                "'" + featureVectors.get(1).getFeatureValue() + "'";
        for (int i = 2; i < featureVectors.size(); i++) {
            values += "," + featureVectors.get(i).getFeatureValue();
        }

        lines.add(values);

        for (int i = 0; i < lines.size() - 1; i++) {
            lines.set(i, lines.get(i) + "\n");
        }

        return lines;
    }
}
