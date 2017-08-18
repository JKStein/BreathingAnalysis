package com.jonas.breathinganalysis;

import android.widget.TextView;

import java.util.Locale;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import static android.os.SystemClock.uptimeMillis;
import static com.jonas.breathinganalysis.Normalizer.getMidiNote;

class AudioHandler {
    private TextView pitch, sp, probability, deviation, note, percussionEvent;
    private double currentSP;

    private static int percussionEventCounter = 0;

    AudioHandler(final BreathingAnalysis breathingAnalysis) {
        initializeViews(breathingAnalysis);
        startListening(breathingAnalysis);
    }

    private void startListening(final BreathingAnalysis breathingAnalysis) {
        final int SAMPLERATE = 22050;
        final int BUFFER = 1024;
        final int OVERLAP = 0;
        final double SENSITIVITY = 65;
        final double THRESHOLD = PercussionOnsetDetector.DEFAULT_THRESHOLD * 1.5;

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLERATE,BUFFER,OVERLAP);

        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLERATE, BUFFER, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();
                final float probability = pitchDetectionResult.getProbability();
                breathingAnalysis.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayCurrentValues(pitchInHz, probability);
                        breathingAnalysis.soundEventValues.add(new Sound(uptimeMillis(), pitchInHz, probability,
                                currentSP, getMidiNote(pitchInHz, Normalizer.DEFAULT_TUNING),
                                Normalizer.getPitchDeviation(pitchInHz, Normalizer.DEFAULT_TUNING)));
                    }
                });
            }
        }));

        dispatcher.addAudioProcessor(new PercussionOnsetDetector(SAMPLERATE, BUFFER, new OnsetHandler() {

             @Override
             public void handleOnset(double time, double salience) {
                 breathingAnalysis.runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         displayPercussionEvent();
                         breathingAnalysis.percussionEventValues.add(uptimeMillis());
                     }
                 });
             }
        }, SENSITIVITY,THRESHOLD));

        SilenceDetector silenceDetector = new SilenceDetector(SoundDetector.THRESHOLD,false);
        dispatcher.addAudioProcessor(silenceDetector);
        dispatcher.addAudioProcessor(new SoundDetector(silenceDetector, this));

        new Thread(dispatcher,"Audio Dispatcher").start();
    }
    private void displayCurrentValues(float pitch, float probability) {
        this.pitch.setText(String.format(Locale.US, "%f", pitch));
        this.probability.setText(String.format(Locale.US, "%f", probability));
        this.sp.setText(String.format(Locale.US, "%f", currentSP));
        this.note.setText(String.format(Locale.US, "%s", Normalizer.midiNoteToString(Normalizer.getMidiNote(pitch, Normalizer.DEFAULT_TUNING))));
        this.deviation.setText(String.format(Locale.US, "%f", Normalizer.getPitchDeviation(pitch, Normalizer.DEFAULT_TUNING)));
    }

    private void displayPercussionEvent() {
        percussionEventCounter++;
        this.percussionEvent.setText(String.format(Locale.US, "%d", percussionEventCounter));
    }

    private void initializeViews(BreathingAnalysis breathingAnalysis) {
        pitch = (TextView) breathingAnalysis.findViewById(R.id.currentPitch);
        probability = (TextView) breathingAnalysis.findViewById(R.id.currentProbability);
        sp = (TextView) breathingAnalysis.findViewById(R.id.currentSoundPressure);
        percussionEvent = (TextView) breathingAnalysis.findViewById(R.id.percussionEvent);
        note = (TextView) breathingAnalysis.findViewById(R.id.note);
        deviation = (TextView) breathingAnalysis.findViewById(R.id.deviation);
    }

    void setCurrentSP(double currentSP) {
        this.currentSP = currentSP;
    }
}
