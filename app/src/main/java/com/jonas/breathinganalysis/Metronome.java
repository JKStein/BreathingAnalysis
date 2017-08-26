package com.jonas.breathinganalysis;

import android.app.Fragment;
import android.content.Context;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;

import static android.os.SystemClock.uptimeMillis;

/**
 * @author Jonas Stein
 */

public class Metronome extends Fragment implements Runnable {
    private SoundPool soundPool;
    private int tick, tock;
    private Handler handler;
    private static final int PRE_BUFFER = 500;
    private static final int POST_BUFFER = 500;
    private static final int BUFFER = 12;
    private long[] beatTimestamps;
    private int beatIndex;
    private static final int ONE_MINUTE_IN_MILLISECONDS = 60000;
    private static final int DEFAULT_BPM = 60;
    private static final int DEFAULT_BEATS_PER_BAR = 4;
    private static final int DEFAULT_AMOUNT_OF_BARS = 8;
    private long durationOfOneBeat;
    private int bpm;
    private int beatsPerBar;
    private int amountOfBars;
    private int totalAmountOfBeats;

    private Context context;

    @SuppressWarnings("unused")
    static Metronome newInstance(int bpm, int beatsPerBar, int amountOfBars) {
        Metronome metronome = new Metronome();
        Bundle bundle = new Bundle();
        bundle.putInt("bpm", bpm);
        bundle.putInt("beatsPerBar", beatsPerBar);
        bundle.putInt("amountOfBars", amountOfBars);
        metronome.setArguments(bundle);
        return metronome;
    }

    static Metronome newInstance() {
        return new Metronome();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        soundPool = new SoundPool.Builder().build();
        tick = soundPool.load(context, R.raw.tick,1);
        tock = soundPool.load(context, R.raw.tock,1);
        handler = new Handler();

        if(getArguments() != null) {
            this.bpm = getArguments().getInt("bpm", DEFAULT_BPM);
            this.beatsPerBar = getArguments().getInt("beatsPerBar", DEFAULT_BEATS_PER_BAR);
            this.amountOfBars = getArguments().getInt("amountOfBars", DEFAULT_AMOUNT_OF_BARS);
        }
        else {
            this.bpm = DEFAULT_BPM;
            this.beatsPerBar = DEFAULT_BEATS_PER_BAR;
            this.amountOfBars = DEFAULT_AMOUNT_OF_BARS;
        }

        this.beatIndex = 0;
        this.totalAmountOfBeats = beatsPerBar*amountOfBars;
        this.beatTimestamps = new long[totalAmountOfBeats];

        this.durationOfOneBeat = ONE_MINUTE_IN_MILLISECONDS /bpm;
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
            ((OnMetronomeDoneListener) context).
                    onMetronomeDone(newGetBestFittingStartTimestamp(beatTimestamps, durationOfOneBeat), totalAmountOfBeats * durationOfOneBeat);
        }
        beatIndex++;
    }

    void begin() {
        handler.post(this);
    }

    void interrupt() {
        handler.removeCallbacks(this);
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