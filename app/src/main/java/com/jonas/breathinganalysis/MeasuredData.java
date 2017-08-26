package com.jonas.breathinganalysis;

import java.util.ArrayList;
import java.util.List;

class MeasuredData {

    private List<String[]> measuredDataSequence;

    private long bestFittingStartTimestamp;
    private long overallDuration;

    private long smallestStartTimestamp;
    private long biggestEndTimestamp;

    private ArrayList<MeasurementSeries> allMeasuredData;

    MeasuredData(ArrayList<MeasurementSeries> allMeasuredData, long bestFittingStartTimestamp, long overallDuration) {
        this.allMeasuredData = allMeasuredData;

        for (MeasurementSeries measurementSeries : allMeasuredData) {
            System.out.println("measurementSeries.getSensorData().size(): " + measurementSeries.getSensorData().size());
        }


        this.bestFittingStartTimestamp = bestFittingStartTimestamp;
        this.overallDuration = overallDuration;
        this.smallestStartTimestamp = 0;
        this.biggestEndTimestamp = 0;
    }

    void setMeasuredDataSequence(List<String[]> measuredDataSequence) {
        this.measuredDataSequence = measuredDataSequence;
    }

    List<String[]> getMeasuredDataSequence() {
        return measuredDataSequence;
    }

    long getBestFittingStartTimestamp() {
        return bestFittingStartTimestamp;
    }


    long getSmallestStartTimestamp() {
        return smallestStartTimestamp;
    }

    void setSmallestStartTimestamp(long smallestStartTimestamp) {
        this.smallestStartTimestamp = smallestStartTimestamp;
    }

    long getBiggestEndTimestamp() {
        return biggestEndTimestamp;
    }

    void setBiggestEndTimestamp(long biggestEndTimestamp) {
        this.biggestEndTimestamp = biggestEndTimestamp;
    }

    ArrayList<MeasurementSeries> getAllMeasuredData() {
        return allMeasuredData;
    }

    long getOverallDuration() {
        return overallDuration;
    }
}