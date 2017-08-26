package com.jonas.breathinganalysis;

/**
 * @author Jonas Stein
 */

interface OnMetronomeDoneListener {
    void onMetronomeDone(long bestFittingStartTimestamp, long overallDuration);
}