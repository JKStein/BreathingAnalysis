package com.jonas.breathinganalysis;

import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

class DBMeasurement {

    private BreathingAnalysis breathingAnalysis;
    private MediaRecorder mRecorder;

    private float currentDB = 0;
    private String [] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;

    private Thread runner;
    private final Runnable updater = new Runnable(){

        public void run(){
            updateTv();
        }
    };
    private final Handler mHandler = new Handler();

    DBMeasurement(BreathingAnalysis breathingAnalysis) {
        this.breathingAnalysis = breathingAnalysis;
    }

    void initializeDBMeasurement() {
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            breathingAnalysis.requestPermissions(permissions, requestCode);
        }

        if (runner == null)
        {
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try{
                            Thread.sleep(100);
                            Log.i("Noise", "Tock");
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }

    }

    float getCurrentDB() {
        return currentDB;
    }

    void startRecorder(){
        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try
            {
                mRecorder.prepare();
            }catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " +
                        android.util.Log.getStackTraceString(ioe));

            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try
            {
                mRecorder.start();
            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }

            //mEMA = 0.0;
        }

    }

    private double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }

    private double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    private void updateTv(){
        currentDB = Float.parseFloat(Double.toString((getAmplitudeEMA())));
        breathingAnalysis.dbChartManager.addDBEntry();
    }
}
