package com.jonas.breathinganalysis;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;

public class SoundDetector implements AudioProcessor {

    double threshold;
    SilenceDetector silenceDetector;


    public SoundDetector(SilenceDetector silenceDetector) {
        this.threshold = SilenceDetector.DEFAULT_SILENCE_THRESHOLD;
        this.silenceDetector = silenceDetector;
        silenceDetector = new SilenceDetector(threshold,false);
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        handleSound();
        return true;
    }

    private void handleSound(){
        if(silenceDetector.currentSPL() > threshold){
            System.out.println("Sound detected at:" + System.currentTimeMillis() + ", " + (int)(silenceDetector.currentSPL()) + "dB SPL\n");
        }
    }
    @Override
    public void processingFinished() {

    }
}