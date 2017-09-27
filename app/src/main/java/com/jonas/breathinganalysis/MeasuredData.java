package com.jonas.breathinganalysis;

import java.util.ArrayList;

/**
 * The measured data of all recorders.
 */
class MeasuredData {
    private long bestFittingStartTimestamp;

    private ArrayList<MeasurementSeries> allMeasuredData;

    MeasuredData(ArrayList<MeasurementSeries> allMeasuredData, long bestFittingStartTimestamp) {
        this.allMeasuredData = allMeasuredData;
        this.bestFittingStartTimestamp = bestFittingStartTimestamp;
    }

    long getBestFittingStartTimestamp() {
        return bestFittingStartTimestamp;
    }

    ArrayList<MeasurementSeries> getAllMeasuredData() {
        return allMeasuredData;
    }
}