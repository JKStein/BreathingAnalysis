package com.jonas.breathinganalysis;

import be.tarsos.dsp.onsets.OnsetHandler;

interface PercussionDetectionHandler extends OnsetHandler {
    @Override
    void handleOnset(double v, double v1);
}
