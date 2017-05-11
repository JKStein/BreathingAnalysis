package com.jonas.breathinganalysis;

public class AccelerationLogger implements Runnable {
    AccelerationMeasurement accelerationMeasurement;

    private Thread t;

    AccelerationLogger(AccelerationMeasurement accelerationMeasurement) {
        this.accelerationMeasurement = accelerationMeasurement;
        System.out.println("Creating ");
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        System.out.println("Running ");
        try
        {
            for(int i = 0; i < 100000; i++) {
                System.out.println("i = " + i);
                System.out.println(System.currentTimeMillis() - startTime +
                        ": x = " + accelerationMeasurement.getCurrentXValue() +
                        " | y = " + accelerationMeasurement.getCurrentYValue() +
                        " | z = " + accelerationMeasurement.getCurrentZValue());
                Thread.sleep(10);
            }
        }catch (InterruptedException e) {
            System.out.println("Thread interrupted.");
        }
        System.out.println("Thread exiting.");
    }

    public void start () {
        System.out.println("Starting ");
        if (t == null) {
            t = new Thread (this);
            t.start ();
        }
    }
}