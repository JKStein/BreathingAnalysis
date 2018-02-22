package com.jonas.breathinganalysis;

import android.app.Fragment;
import android.content.Context;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import java.util.ArrayList;

/**
 * @author Jonas Stein
 * Plays a specific amount of beats in the pattern and speed passed.
 * If nothing gets passed, default settings get applied.
 */
public class Metronome extends Fragment implements Runnable {
    private static final int ONE_MINUTE_IN_MILLISECONDS = 60000;
    private static final int DEFAULT_BPM = 60;
    private static final int DEFAULT_BEATS_PER_BAR = 4;
    private static final int DEFAULT_AMOUNT_OF_BARS = 8;
    private static final int PRE_BUFFER = 800;
    private static final int POST_BUFFER = 500;
    private static final int BUFFER = 12;

    private SoundPool soundPool;
    private int tick, tock;
    private Handler handler;

    private long[] beatTimestamps;
    private int beatIndex;
    private long durationOfOneBeat;
    private int bpm, beatsPerBar, amountOfBars, totalAmountOfBeats;

    private Context context;

    private ArrayList<SensorDate> sensorData;

    /**
     * Creates a new instance of a metronome.
     * @param bpm The beats per minute.
     * @param beatsPerBar The amount of beats per bar.
     * @param amountOfBars The total amount of bars.
     * @return A new Metronome
     */
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * Instantiates all variables.
     * If an instance was created its parameters will be used for instantiation.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains
     *                           the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        this.totalAmountOfBeats = beatsPerBar * amountOfBars;
        this.beatTimestamps = new long[totalAmountOfBeats];

        this.durationOfOneBeat = ONE_MINUTE_IN_MILLISECONDS / bpm;

        sensorData = new ArrayList<>();
    }

    /**
     * Plays the sounds and reports to the OnMetronomeDoneListener when all beats have been played.
     */
    @Override
    public void run() {
        if(beatIndex >= 1 && beatIndex <= totalAmountOfBeats) {
            beatTimestamps[beatIndex-1] = SystemClock.elapsedRealtime();
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
            for (long timestamp : beatTimestamps) {
                this.sensorData.add(new SensorDate(timestamp, new float[]{1}));
            }
            handler.postDelayed(this, durationOfOneBeat + POST_BUFFER);
        }
        else {
            handler.removeCallbacks(this);
            ((OnMetronomeDoneListener) context).
                    onMetronomeDone(newGetBestFittingStartTimestamp(beatTimestamps, durationOfOneBeat), totalAmountOfBeats * durationOfOneBeat);
        }
        beatIndex++;
    }

    /**
     * Starts the metronome.
     */
    void begin() {
        handler.post(this);
    }

    /**
     * Interrupts the metronome.
     */
    void interrupt() {
        handler.removeCallbacks(this);
    }

    /**
     * Resets all parameters to their defaults.
     */
    void reset() {
        this.bpm = DEFAULT_BPM;
        this.beatsPerBar = DEFAULT_BEATS_PER_BAR;
        this.amountOfBars = DEFAULT_AMOUNT_OF_BARS;
        this.durationOfOneBeat = ONE_MINUTE_IN_MILLISECONDS / bpm;
        this.beatIndex = 0;
        this.totalAmountOfBeats = beatsPerBar * amountOfBars;
        this.beatTimestamps = new long[totalAmountOfBeats];
    }

    /**
     * Computes a timestamp in milliseconds for official timestamp of the first beat
     * based on the method of least squares with a fixed slope.
     * @param beatTimestamps The timestamps of all beats played.
     * @param durationOfOneBeat The duration of a beat in milliseconds.
     * @return A suggestion for the official timestamp of the first beat.
     */
    private static long newGetBestFittingStartTimestamp(long[] beatTimestamps, long durationOfOneBeat) {
        long arithmeticSum = 0;
        for (long beatTimestamp : beatTimestamps) {
            arithmeticSum += beatTimestamp;
        }
        long arithmeticMiddle = arithmeticSum/beatTimestamps.length;

        return arithmeticMiddle + durationOfOneBeat * (1 - beatTimestamps.length) / 2;
    }

    ArrayList<SensorDate> getSensorData() {
        return this.sensorData;
    }

    long getDurationOfOneBeat() {
        return durationOfOneBeat;
    }

    int getBeatsPerBar() {
        return beatsPerBar;
    }

    int getBpm() {
        return bpm;
    }
}