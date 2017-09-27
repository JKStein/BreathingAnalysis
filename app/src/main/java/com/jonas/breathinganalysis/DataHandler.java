package com.jonas.breathinganalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates the processing of the measured data.
 */
class DataHandler implements Runnable {
    private MeasuredData measuredData;
    private ArrayList<Feature> features;
    private OnSavingDoneListener onSavingDoneListener;
    private File csvFile, arffFile;

    DataHandler(final MeasuredData measuredData, final ArrayList<Feature> features,
                final File csvFile, final File arffFile,
                final OnSavingDoneListener onSavingDoneListener) {
        this.measuredData = measuredData;
        this.features = features;
        this.csvFile = csvFile;
        this.arffFile = arffFile;
        this.onSavingDoneListener = onSavingDoneListener;
        (new Thread(this)).start();
    }

    /**
     * Instructs preprocessing and storing of the the measured data and its features.
     */
    public void run() {
        ArrayList<MeasurementSeries> seriesOfMeasurements = measuredData.getAllMeasuredData();
        DataPreprocessor.removeRedundancies(seriesOfMeasurements);
        DataPreprocessor.normalizeTimestamps(seriesOfMeasurements, measuredData.getBestFittingStartTimestamp());
        long smallestStartTimestamp = DataPreprocessor.getSmallestStartTimestamp(seriesOfMeasurements);
        long biggestEndTimestamp = DataPreprocessor.getBiggestEndTimestamp(seriesOfMeasurements);
        List<String[]> csvList = DataPreprocessor.getMeasuredDataSequence(seriesOfMeasurements, smallestStartTimestamp, biggestEndTimestamp);
        List<String> arffList = DataPreprocessor.getFeatures(features);
        boolean text = DataLogger.writeToFiles(csvList, arffList, csvFile, arffFile);
        onSavingDoneListener.savingDone(text);
    }
}
