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
 */

public class Metronome extends Fragment implements Runnable {
    private SoundPool soundPool;
    private int tick, tock;
    private Handler handler;
    private static final int PRE_BUFFER = 800;
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

    private ArrayList<SensorDate> sensorData;

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

        sensorData = new ArrayList<>();
    }

    @Override
    public void run() {
        if(beatIndex >= 1 && beatIndex <= totalAmountOfBeats) {
            beatTimestamps[beatIndex-1] = SystemClock.elapsedRealtime();
            handler.postDelayed(this, durationOfOneBeat - BUFFER);
            System.out.println("hoi1");
            if((beatIndex % beatsPerBar) == 1) {
                soundPool.play(tick, 1, 1, 1, 0, 1);
            }
            else {
                soundPool.play(tock, 1, 1, 1, 0, 1);
            }
            System.out.println("hoi2");
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

    void begin() {
        handler.post(this);
    }

    void interrupt() {
        handler.removeCallbacks(this);
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