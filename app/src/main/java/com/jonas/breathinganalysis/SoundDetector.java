package com.jonas.breathinganalysis;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;

class SoundDetector implements AudioProcessor {
    static final double THRESHOLD = -80.0D;
    private SilenceDetector silenceDetector;
    private AudioHandler audioProcessor;


    SoundDetector(SilenceDetector silenceDetector, AudioHandler audioHandler) {
        this.audioProcessor = audioHandler;
        this.silenceDetector = silenceDetector;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        handleSound();
        return true;
    }

    private void handleSound(){
        if(silenceDetector.currentSPL() > THRESHOLD){
            audioProcessor.setCurrentSP(silenceDetector.currentSPL());
        }
        else {
            audioProcessor.setCurrentSP(THRESHOLD);
        }
    }
    @Override
    public void processingFinished() {

    }
}