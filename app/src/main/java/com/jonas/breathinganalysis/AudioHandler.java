package com.jonas.breathinganalysis;

import android.widget.TextView;

import java.util.Locale;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

class AudioHandler {
    private TextView pitch, sp, probability;
    private double currentSP;

    AudioHandler(final BreathingAnalysis breathingAnalysis) {
        initializeViews(breathingAnalysis);
        startListening(breathingAnalysis);
    }

    private void startListening(final BreathingAnalysis breathingAnalysis) {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();
                final float probability = pitchDetectionResult.getProbability();
                breathingAnalysis.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayCurrentValues(pitchInHz, probability);
                        breathingAnalysis.soundList.add(new Sound(System.currentTimeMillis(), pitchInHz, probability, currentSP));
                    }
                });

            }
        }));
        new Thread(dispatcher,"Audio Dispatcher").start();

        SilenceDetector silenceDetector = new SilenceDetector(SoundDetector.THRESHOLD,false);
        dispatcher.addAudioProcessor(silenceDetector);
        dispatcher.addAudioProcessor(new SoundDetector(silenceDetector, this));
        System.out.println("Audio Dispatcher started");
    }
    private void displayCurrentValues(float pitch, float probability) {
        this.pitch.setText(String.format(Locale.US, "%f", pitch));
        this.probability.setText(String.format(Locale.US, "%f", probability));
        this.sp.setText(String.format(Locale.US, "%f", currentSP));
    }

    private void initializeViews(BreathingAnalysis breathingAnalysis) {
        pitch = (TextView) breathingAnalysis.findViewById(R.id.currentPitch);
        probability = (TextView) breathingAnalysis.findViewById(R.id.currentProbability);
        sp = (TextView) breathingAnalysis.findViewById(R.id.currentSoundPressure);
    }

    void setCurrentSP(double currentSP) {
        this.currentSP = currentSP;
    }
}
