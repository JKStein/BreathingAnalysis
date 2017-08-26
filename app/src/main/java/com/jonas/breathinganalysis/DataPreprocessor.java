package com.jonas.breathinganalysis;

import java.util.ArrayList;
import java.util.Arrays;

class DataPreprocessor {

    DataPreprocessor(MeasuredData measuredData) {
        removeRedundancies(measuredData);
        normalizeLengthNew(measuredData);
        measuredData.setBiggestEndTimestamp(getBiggestEndTimestamp(measuredData));
        measuredData.setSmallestStartTimestamp(getSmallestStartTimestamp(measuredData));
        Normalizer.instantiateMeasuredDataSequenceNew(measuredData);
    }

    private void removeRedundancies(MeasuredData measuredData) {
        for (MeasurementSeries measurementSeries : measuredData.getAllMeasuredData()) {
            System.out.println(Arrays.toString(measurementSeries.getValues()));
            Normalizer.removeSensorDataRedundancies(measurementSeries.getSensorData());
        }
    }


    private void normalizeLengthNew(MeasuredData measuredData) {
        long startTimestamp = getBiggestStartTimestampNew(measuredData);

        for (MeasurementSeries measurementSeries : measuredData.getAllMeasuredData()) {
            for (SensorDate sensorDate : measurementSeries.getSensorData()) {
                sensorDate.setTimestamp(sensorDate.getTimestamp() - startTimestamp);
            }
        }
    }

    private long getBiggestStartTimestampNew(MeasuredData measuredData) {
        //TODO catch bad data
        /*if(measuredData.isIncomplete()) {
            return 0D;
        }*/

        long biggest = Long.MIN_VALUE;

        for (MeasurementSeries measurementSeries: measuredData.getAllMeasuredData()) {
            if(measurementSeries.getSensorData().size() > 0) {
                long timestamp = measurementSeries.getSensorData().get(0).getTimestamp();
                if(biggest < timestamp) {
                    biggest = timestamp;
                }
            }
        }

        if(measuredData.getBestFittingStartTimestamp() > biggest) {
            biggest = measuredData.getBestFittingStartTimestamp();
        }

        System.out.println("Biggest Start Timestamp: " + biggest);
        return biggest;
    }

    private long getSmallestStartTimestamp(MeasuredData measuredData) {
        //TODO catch bad data
        /*if(measuredData.isIncomplete()) {
            return 0D;
        }*/

        long smallest = Long.MAX_VALUE;

        for (MeasurementSeries measurementSeries: measuredData.getAllMeasuredData()) {
            if(measurementSeries.getSensorData().size() > 0) {
                long timestamp = measurementSeries.getSensorData().get(0).getTimestamp();
                System.out.println("timestamp: " + timestamp);
                if(smallest > timestamp) {
                    smallest = timestamp;
                }
            }
        }

        System.out.println("Smallest start timestamp: " + smallest);
        return smallest;
    }

    private long getBiggestEndTimestamp(MeasuredData measuredData) {
        //TODO catch bad data
        /*if(measuredData.isIncomplete()) {
            return 0D;
        }*/

        long biggest = Long.MIN_VALUE;

        for (MeasurementSeries measurementSeries: measuredData.getAllMeasuredData()) {
            if(measurementSeries.getSensorData().size() > 0) {
                ArrayList<SensorDate> sensorData = measurementSeries.getSensorData();
                long timestamp = sensorData.get(sensorData.size() - 1).getTimestamp();
                if (biggest < timestamp) {
                    biggest = timestamp;
                }
            }
        }

        System.out.println("Biggest end timestamp: " + biggest);
        return biggest;
    }
}
