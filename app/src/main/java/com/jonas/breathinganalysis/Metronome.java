package com.jonas.breathinganalysis;

import android.media.SoundPool;
import android.os.Handler;

import static android.os.SystemClock.uptimeMillis;

/**
 * @author Jonas Stein
 */

class Metronome implements Runnable {
    private SoundPool soundPool;
    private int tick, tock;
    private Handler handler;
    private static final int PRE_BUFFER = 500;
    private static final int POST_BUFFER = 500;
    private static final int BUFFER = 12;
    private long[] beatTimestamps;
    private int beatIndex;
    private BreathingAnalysis breathingAnalysis;
    private static final int ONE_MINUTE_IN_MILLISECONDS = 60000;
    private static final int DEFAULT_BPM = 60;
    private static final int DEFAULT_BEATS_PER_BAR = 4;
    private static final int DEFAULT_AMOUNT_OF_BARS = 8;
    private long durationOfOneBeat;
    private int bpm;
    private int beatsPerBar;
    private int amountOfBars;
    private int totalAmountOfBeats;



    /**
     * Standard constructor, constructs a metronome with common time and 60 beats per minute.
     * @param soundPool
     * @param tick
     * @param tock
     * @param handler
     * @param breathingAnalysis
     */
    Metronome(SoundPool soundPool, int tick, int tock, Handler handler, BreathingAnalysis breathingAnalysis) {
        this.bpm = DEFAULT_BPM;
        this.beatsPerBar = DEFAULT_BEATS_PER_BAR;
        this.amountOfBars = DEFAULT_AMOUNT_OF_BARS;
        this.durationOfOneBeat = ONE_MINUTE_IN_MILLISECONDS /bpm;

        this.soundPool = soundPool;
        this.tick = tick;
        this.tock = tock;
        this.handler = handler;
        this.beatIndex = 0;
        this.totalAmountOfBeats = beatsPerBar*amountOfBars;
        this.beatTimestamps = new long[totalAmountOfBeats];
        this.breathingAnalysis = breathingAnalysis;
    }

    /**
     * Supports simple, compound and complex time signatures.
     * @param soundPool
     * @param tick
     * @param tock
     * @param handler
     * @param breathingAnalysis
     * @param bpm
     * @param beatsPerBar
     * @param amountOfBars
     */
    Metronome(SoundPool soundPool, int tick, int tock, Handler handler, BreathingAnalysis breathingAnalysis,
              int bpm, int beatsPerBar, int amountOfBars) {
        this.bpm = bpm;
        this.beatsPerBar = beatsPerBar;
        this.amountOfBars = amountOfBars;
        this.soundPool = soundPool;
        this.tick = tick;
        this.tock = tock;
        this.handler = handler;
        this.beatIndex = 0;
        this.totalAmountOfBeats = beatsPerBar*amountOfBars;
        this.beatTimestamps = new long[totalAmountOfBeats];
        this.breathingAnalysis = breathingAnalysis;

        this.durationOfOneBeat = ONE_MINUTE_IN_MILLISECONDS / bpm;
    }

    @Override
    public void run() {
        if(beatIndex >= 1 && beatIndex <= totalAmountOfBeats) {
            beatTimestamps[beatIndex-1] = uptimeMillis();
            handler.postDelayed(this, durationOfOneBeat - BUFFER);
            if((beatIndex % beatsPerBar) == 1) {
                soundPool.play(tick, 1, 1, 1, 0, 1);
            }
            else {
                soundPool.play(tock, 1, 1, 1, 0, 1);
            }
        }
        else if(beatIndex == 0) {
            handler.postDelayed(this, PRE_BUFFER);
        }
        else if(beatIndex == totalAmountOfBeats + 1) {
            printStatisticalAnalysis(beatTimestamps, durationOfOneBeat);
            handler.postDelayed(this, durationOfOneBeat + POST_BUFFER);
        }
        else {
            handler.removeCallbacks(this);
            breathingAnalysis.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    breathingAnalysis.setStuffForDataHandler(newGetBestFittingStartTimestamp(beatTimestamps, durationOfOneBeat), totalAmountOfBeats * durationOfOneBeat);
                }
            });
        }
        beatIndex++;
    }

    private static void printStatisticalAnalysis(long[] beatTimestamps, long durationOfOneBeat) {
        long accumulatedStepWidthError = 0;
        long effectiveStepWidthError = 0;
        long[] stepWidthErrors = new long[beatTimestamps.length-1];
        for (int i = 1; i<beatTimestamps.length; i++) {
            stepWidthErrors[i-1] = (beatTimestamps[i] - beatTimestamps[i-1]) - durationOfOneBeat;
            accumulatedStepWidthError += Math.abs(stepWidthErrors[i-1]);
            effectiveStepWidthError += stepWidthErrors[i-1];
        }
        for (int i = 0; i < stepWidthErrors.length; i++) {
            System.out.println("stepWidthErrors[" + i + "]: " + stepWidthErrors[i]);
        }
        double averageStepWidthError = ((double) effectiveStepWidthError) / ((double) stepWidthErrors.length);

        if(effectiveStepWidthError > 0) {
            System.out.println("Overall the metronome got " + effectiveStepWidthError + "ms slower.");
        }
        else if(effectiveStepWidthError == 0) {
            System.out.println("Overall the metronome kept beating exactly correct.");
        }
        else {
            System.out.println("Overall the metronome got " + (-effectiveStepWidthError) + "ms faster.");
        }

        System.out.println("The overall accumulated step width error is " + accumulatedStepWidthError + "ms.");

        System.out.println("The average step width error is " + averageStepWidthError + "ms.");




        double variance = 0;
        for (long stepWidthError : stepWidthErrors) {
            variance += Math.pow(stepWidthError - averageStepWidthError, 2);
        }
        variance = variance / ((double) stepWidthErrors.length);
        System.out.println("Variance of step width error: " + variance);
        double standardDeviation = Math.sqrt(variance);
        System.out.println("Standard deviation of step widths: " + standardDeviation);


        long minValue = stepWidthErrors[0];
        long maxValue = stepWidthErrors[0];
        long max = stepWidthErrors[0];
        long min = stepWidthErrors[0];
        for (int i = 1; i < stepWidthErrors.length; i++) {
            if (Math.abs(stepWidthErrors[i]) > max) {
                max = Math.abs(stepWidthErrors[i]);
                maxValue = stepWidthErrors[i];
            }
            if (Math.abs(stepWidthErrors[i]) < min) {
                min = Math.abs(stepWidthErrors[i]);
                minValue = stepWidthErrors[i];
            }
        }
        System.out.println("Minimal deviation: " + minValue);
        System.out.println("Maximal deviation: " + maxValue);
    }

    void reset() {
        this.bpm = DEFAULT_BPM;
        this.beatsPerBar = DEFAULT_BEATS_PER_BAR;
        this.amountOfBars = DEFAULT_AMOUNT_OF_BARS;
        this.durationOfOneBeat = ONE_MINUTE_IN_MILLISECONDS /bpm;
        this.beatIndex = 0;
        this.totalAmountOfBeats = beatsPerBar*amountOfBars;
        this.beatTimestamps = new long[totalAmountOfBeats];
    }

    //based on the method of least squares with a fixed slope
    private static long newGetBestFittingStartTimestamp(long[] beatTimestamps, long durationOfOneBeat) {
        System.out.println("beatTimestamps[0] = " + beatTimestamps[0]);
        long arithmeticSum = 0;
        for (long beatTimestamp : beatTimestamps) {
            arithmeticSum += beatTimestamp;
        }
        long arithmeticMiddle = arithmeticSum/beatTimestamps.length;

        long result = arithmeticMiddle + durationOfOneBeat * (1 - beatTimestamps.length) / 2;

        System.out.println("The method of least squares calculated " + (result-beatTimestamps[0]) + "ms " +
                "to be the most suitable offset!");

        return result;
    }
}
