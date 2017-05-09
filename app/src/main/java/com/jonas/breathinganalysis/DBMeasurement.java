package com.jonas.breathinganalysis;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

class DBMeasurement implements OnChartValueSelectedListener {

    private BreathingAnalysis breathingAnalysis;

    private float currentDB = 0;

    private String [] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private Thread runner;
    private final Runnable updater = new Runnable(){

        public void run(){
            breathingAnalysis.dbChartManager.updateTv();
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

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    float getCurrentDB() {
        return currentDB;
    }

    void setCurrentDB(float newDB) {
        currentDB = newDB;
    }
}
