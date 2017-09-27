package com.jonas.breathinganalysis;

/**
 * @author Jonas Stein
 * This interface realizes the ability for the VolumeRecorder to receive the spl
 * computed from of the latest detected signal by the SilenceDetector.
 */
interface OnVolumeDetectedListener {
    /**
     * @return The most recent spl computed by the SilenceDetector.
     */
    double getSPL();
}