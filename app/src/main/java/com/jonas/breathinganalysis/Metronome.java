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
    private static final int BUFFER = 12;//12;
    private long lastBeatUptime;
    private long[] beatTimestamps;
    private int beatIndex;
    private static final int MAXAMOUNTOFBEATS = 33;
    private BreathingAnalysis breathingAnalysis;
    private static final int[] tickArray = {1, 5, 9, 13, 17, 21, 25, 29};

    Metronome(SoundPool soundPool, int tick, int tock, Handler handler, BreathingAnalysis breathingAnalysis) {
        this.soundPool = soundPool;
        this.tick = tick;
        this.tock = tock;
        this.handler = handler;
        this.lastBeatUptime = 0;
        this.beatIndex = 0;
        this.beatTimestamps = new long[32];
        this.breathingAnalysis = breathingAnalysis;
    }

    @Override
    public void run() {
            if(beatIndex < 0 || beatIndex > 33) {
                handler.removeCallbacks(this);
                breathingAnalysis.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        breathingAnalysis.setBestFittingStartTimestamp(newGetBestFittingStartTimestamp(beatTimestamps));
                    }
                });
            }
            else if(beatIndex == 0) {
                lastBeatUptime = uptimeMillis() - 500;
                handler.postDelayed(this, 500);
            }
            else if(beatIndex == 33) {
                long accumulatedStepWidthError = 0;
                long effectiveStepWidthError = 0;
                long sum = 0;
                double averageStepWidthError = 0;
                long[] stepWidthErrors = new long[beatTimestamps.length-1];
                //System.out.println("beatTimestamps[0]: " + beatTimestamps[0]);
                for (int i = 1; i<beatTimestamps.length; i++) {
                    //System.out.println("beatTimestamps[" + i + "]: " + beatTimestamps[i]);
                    stepWidthErrors[i-1] = (beatTimestamps[i] - beatTimestamps[i-1]) - 1000;
                    sum += stepWidthErrors[i-1];//(beatTimestamps[i] - beatTimestamps[i-1]);
                    accumulatedStepWidthError += Math.abs(stepWidthErrors[i-1]);
                    effectiveStepWidthError += stepWidthErrors[i-1];
                }
                for (int i = 0; i<stepWidthErrors.length; i++) {
                    System.out.println("stepWidthErrors[" + i + "]: " + stepWidthErrors[i]);
                }
                averageStepWidthError = ((double) sum) / 31D;

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




                double sumD = 0;
                for (int i = 0; i < stepWidthErrors.length; i++) {
                    sumD += Math.pow(stepWidthErrors[i] - averageStepWidthError, 2);
                }
                double variance = sumD / 31D;
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

                handler.postDelayed(this, 1000);
            }
            else {
                beatTimestamps[beatIndex-1] = uptimeMillis();
                //long effectiveStepWidthError = 0;
                /*if(beatIndex > 1) {
                    for (int i = 1; i<beatIndex-1; i++) {
                        effectiveStepWidthError += (beatTimestamps[i] - beatTimestamps[i-1]) - 1000;
                    }
                }
                if(Math.abs(effectiveStepWidthError) < 25) {
                    effectiveStepWidthError = 0;
                }*/
                handler.postDelayed(this, 1000 - BUFFER );//- effectiveStepWidthError);
                //long before = uptimeMillis();
                if(contains(tickArray, beatIndex)) {
                    soundPool.play(tick, 1, 1, 1, 0, 1);
                }
                else {
                    soundPool.play(tock, 1, 1, 1, 0, 1);
                }
                //long after = uptimeMillis();
                //lastBeatUptime = before;

                //handler.postDelayed(this, 1000 - (after - before) - ERRORTIME);
            }
        beatIndex++;
    }

    private static boolean contains(int[] array, int value) {
        for (int anArray : array) {
            if (anArray == value) {
                return true;
            }
        }
        return false;
    }

    //Printed run:
    /*
    @Override
    public void run() {
        long before = uptimeMillis();
        System.out.println("\n 'Before' uptimeMillis(): " + before);
        soundPool.play(tick, 1, 1, 1, 0, 1);
        long after = uptimeMillis();
        System.out.println("soundPool.play() took " + (after-before) + "ms.\n");
        System.out.println("Time passed since last call: " + (before-lastBeatUptime) + "ms.\n");
        lastBeatUptime = before;
        handler.postDelayed(this, 1000 - (after - before) - ERRORTIME);
    }
     */

    private static long getBestFittingStartTimestamp(long[] beatTimestamps) {
        double[] offset = new double[199];
        for(int i = 99; i >= 0; i--) {
            offset[99 - i] = calculateOffset(beatTimestamps, beatTimestamps[0]-i);
        }
        for(int i = 0; i < 99; i++) {
            offset[100 + i] = calculateOffset(beatTimestamps, beatTimestamps[0]+1+i);
        }
        for(int i = 0; i < offset.length; i++) {
            System.out.println("the offset for " + i + " is: " + offset[i]);
        }
        return beatTimestamps[0] + (minValueIndex(offset) - 99);
    }

    private static double calculateOffset(long[] beatTimestamps, long startTimestamp) {
        double offset = 0;
        for(int i = 0; i < beatTimestamps.length; i++) {
            offset += Math.abs((double) (beatTimestamps[i] - (startTimestamp + i * 1000)));
            offset += Math.abs((double) (beatTimestamps[i] - (startTimestamp + i * 1000)));
        }
        return offset;
    }

    private static long minValueIndex(double[] offset) {
        double min = offset[0];
        int index = 0;
        for (int i = 1; i < offset.length; i++) {
            if (offset[i] < min) {
                min = offset[i];
                index = i;
            }
            else if(min == offset[i]) {
                if(i <= 99) {
                    min = offset[i];
                    index = i;
                }
            }
        }
        return (long) index;
    }

    void reset() {
        this.lastBeatUptime = 0;
        this.beatIndex = 0;
        this.beatTimestamps = new long[32];
    }

    //based on the method of least squares with a fixed slope
    private static long newGetBestFittingStartTimestamp(long[] beatTimestamps) {
        long result = 0;
        System.out.println("beatTimestamps[0] = " + beatTimestamps[0]);
        long arithmeticSum = 0;
        for (long beatTimestamp : beatTimestamps) {
            arithmeticSum += beatTimestamp;
        }
        long arithmeticMiddle = arithmeticSum/beatTimestamps.length;

        result = arithmeticMiddle + 500 * (1 - beatTimestamps.length);

        System.out.println("The method of least squares calculated " + (result-beatTimestamps[0]) + "ms " +
                "to be the most suitable offset!");

        return result;
    }
}
