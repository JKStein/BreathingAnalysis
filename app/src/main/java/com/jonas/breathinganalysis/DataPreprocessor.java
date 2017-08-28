package com.jonas.breathinganalysis;

import java.util.ArrayList;

class DataPreprocessor {

    DataPreprocessor(MeasuredData measuredData) {
        ArrayList<MeasurementSeries> seriesOfMeasurements = measuredData.getAllMeasuredData();
        removeRedundancies(seriesOfMeasurements);
        normalizeTimestamps(seriesOfMeasurements, getBiggestStartTimestampNew(seriesOfMeasurements, measuredData.getBestFittingStartTimestamp()));
        measuredData.setBiggestEndTimestamp(getBiggestEndTimestamp(seriesOfMeasurements));
        measuredData.setSmallestStartTimestamp(getSmallestStartTimestamp(seriesOfMeasurements));
        Normalizer.instantiateMeasuredDataSequenceNew(measuredData);
    }

    /**
     * Deletes all sensor data entries of one SensorDate with redundant timestamps.
     * This usually has no effect at all, because the sample rate is at maximum 2ms.
     * @param seriesOfMeasurements An ArrayList containing each MeasurementSeries.
     */
    private void removeRedundancies(ArrayList<MeasurementSeries> seriesOfMeasurements) {
        for (MeasurementSeries measurementSeries : seriesOfMeasurements) {
            Normalizer.removeSensorDataRedundancies(measurementSeries.getSensorData());
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
    private long getBiggestStartTimestampNew(ArrayList<MeasurementSeries> seriesOfMeasurements, long bestFittingStartTimeStamp) {
        long biggest = Long.MIN_VALUE;

        for (MeasurementSeries measurementSeries: seriesOfMeasurements) {
            if(measurementSeries.getSensorData().size() > 0) {
                long timestamp = measurementSeries.getSensorData().get(0).getTimestamp();
                if(biggest < timestamp) {
                    biggest = timestamp;
                }
            }
        }

        if(bestFittingStartTimeStamp > biggest) {
            biggest = bestFittingStartTimeStamp;
        }
        return biggest;
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
}
