package com.jonas.breathinganalysis;

import android.graphics.Color;
import android.media.MediaRecorder;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


class DBChartManager {
    private BreathingAnalysis breathingAnalysis;

    private LineChart dBChart;

    private MediaRecorder mRecorder;

    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;



    DBChartManager(BreathingAnalysis breathingAnalysis) {
        this.breathingAnalysis = breathingAnalysis;
        this.dBChart = (LineChart) breathingAnalysis.findViewById(R.id.dBChartDisplay);
    }


    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
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

    void initializeDBChart() {
        //float value = Float.parseFloat(Double.toString((getAmplitudeEMA())));

        ArrayList<Entry> yAxesDB = new ArrayList<>();
        float xEntry = Float.parseFloat("0");
        yAxesDB.add(new Entry(xEntry,0f));

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet1 = new LineDataSet(yAxesDB,"y-Axes-dB");
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSets.add(lineDataSet1);
        dBChart.setData(new LineData(lineDataSets));
        dBChart.setVisibleXRangeMaximum(65f);
        dBChart.invalidate();
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

    private void addDBEntry() {
        LineData data = dBChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), breathingAnalysis.getCurrentDB()), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            dBChart.notifyDataSetChanged();

            // limit the number of visible entries
            dBChart.setVisibleXRangeMaximum(120);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            dBChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    void updateTv(){
        //System.out.println(Double.toString((getAmplitudeEMA())));
        breathingAnalysis.setCurrentDB(Float.parseFloat(Double.toString((getAmplitudeEMA()))));
        addDBEntry();
    }
}
