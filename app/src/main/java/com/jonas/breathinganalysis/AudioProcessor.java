package com.jonas.breathinganalysis;

import android.widget.Toast;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;


/**
 * Created by Jonas on 13.06.2017.
 */

public class AudioProcessor {

    public static BreathingAnalysis breathingAnalysis;

    AudioProcessor(final BreathingAnalysis breathingAnalysis) {
        this.breathingAnalysis = breathingAnalysis;
        Toast.makeText(breathingAnalysis,
                "started", Toast.LENGTH_LONG).show();
        start();
    }

    void start() {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);


        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();
                AudioProcessor.breathingAnalysis.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("pitch: " + pitchInHz);
                    }
                });

            }
        }));
        new Thread(dispatcher,"Audio Dispatcher").start();

        SilenceDetector silenceDetector = new SilenceDetector(SilenceDetector.DEFAULT_SILENCE_THRESHOLD,false);
        dispatcher.addAudioProcessor(silenceDetector);
        dispatcher.addAudioProcessor(new SoundDetector(silenceDetector));

        System.out.println("Audio Dispatcher started");
    }
}
