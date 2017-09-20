package com.jonas.breathinganalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class DataHandler implements Runnable {

    private MeasuredData measuredData;
    private ArrayList<FeatureVector> featureVectors;
    private Thread t;
    private OnSavingDoneListener onSavingDoneListener;
    private File csvFile, arffFile;

    DataHandler(final MeasuredData measuredData, final ArrayList<FeatureVector> featureVectors, final File csvFile, final File arffFile) {
        this.measuredData = measuredData;
        this.featureVectors = featureVectors;
        System.out.println("Creating");
        this.csvFile = csvFile;
        this.arffFile = arffFile;
    }

    void setOnSavingDoneListener(OnSavingDoneListener onSavingDoneListener) {
        this.onSavingDoneListener = onSavingDoneListener;
    }

    public void run() {
        System.out.println("Running");
        DataPreprocessor dataPreprocessor = new DataPreprocessor(measuredData);
        List<String> list2 = dataPreprocessor.getFeatures(featureVectors);
        boolean text = DataLogger.writeToFiles(measuredData.getMeasuredDataSequence(), list2, csvFile, arffFile);
        onSavingDoneListener.savingDone(text);
        System.out.println("Thread exiting.");
    }

    void start () {
        System.out.println("Starting");
        if (t == null) {
            t = new Thread (this);
            t.start ();
        }
    }
}
