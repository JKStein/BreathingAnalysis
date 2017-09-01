package com.jonas.breathinganalysis;

import java.util.ArrayList;
import java.util.List;

class DataHandler implements Runnable {

    private MeasuredData measuredData;
    private ArrayList<FeatureVector> featureVectors;
    private Thread t;
    private OnSavingDoneListener onSavingDoneListener;

    DataHandler(final MeasuredData measuredData, final ArrayList<FeatureVector> featureVectors) {
        this.measuredData = measuredData;
        this.featureVectors = featureVectors;
        System.out.println("Creating");
    }

    void setOnSavingDoneListener(OnSavingDoneListener onSavingDoneListener) {
        this.onSavingDoneListener = onSavingDoneListener;
    }

    public void run() {
        System.out.println("Running");
        DataPreprocessor dataPreprocessor = new DataPreprocessor(measuredData);
        List<String> list2 = dataPreprocessor.getFeatures(featureVectors);
        new DataLogger(measuredData.getMeasuredDataSequence(), list2);
        onSavingDoneListener.savingDone();
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
