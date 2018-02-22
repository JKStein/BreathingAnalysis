package com.jonas.breathinganalysis;

/**
 * @author Jonas Stein
 * This interface realizes the ability to get notified, when the Metronome is done.
 */
interface OnMetronomeDoneListener {
    /**
     * Gets called when the Metronome played the last intended beat.
     * @param bestFittingStartTimestamp A timestamp in milliseconds suggesting the official
     *                                  timestamp of the first beat.
     * @param overallDuration The destined time in milliseconds from the first to the end of
     *                        the last beat of the Metronome.
     */
    void onMetronomeDone(long bestFittingStartTimestamp, long overallDuration);
}