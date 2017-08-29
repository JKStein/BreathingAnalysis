package com.jonas.breathinganalysis;

class DataHandler implements Runnable {

    private MeasuredData measuredData;
    private Thread t;
    private int percussionPosition;
    private OnSavingDoneListener onSavingDoneListener;

    DataHandler(final MeasuredData measuredData, final int percussionPosition) {
        this.measuredData = measuredData;
        this.percussionPosition = percussionPosition;
        System.out.println("Creating");
    }

    void setOnSavingDoneListener(OnSavingDoneListener onSavingDoneListener) {
        this.onSavingDoneListener = onSavingDoneListener;
    }

    public void run() {
        System.out.println("Running");
        new DataPreprocessor(measuredData, percussionPosition);
        new DataLogger(measuredData.getMeasuredDataSequence());
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
