package com.jonas.breathinganalysis;

class DataHandler implements Runnable {

    private MeasuredData measuredData;
    private Thread t;

    DataHandler(final MeasuredData measuredData) {
        this.measuredData = measuredData;
        System.out.println("Creating");
    }

    public void run() {
        System.out.println("Running");
        new DataPreprocessor(measuredData);
        new DataLogger(measuredData);
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
