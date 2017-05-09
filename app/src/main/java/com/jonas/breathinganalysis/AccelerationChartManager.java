package com.jonas.breathinganalysis;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class AccelerationChartManager {

    BreathingAnalysis breathingAnalysis;

    LineChart accelerationChart;

    public AccelerationChartManager(BreathingAnalysis breathingAnalysis, LineChart accelerationChart) {
        this.accelerationChart = accelerationChart;
        this.breathingAnalysis = breathingAnalysis;
    }

    public void initializeAccelerationChart() {
        ArrayList<Entry> yAxesYAcceleration = new ArrayList<>();
        float xEntry = Float.parseFloat("0");
        yAxesYAcceleration.add(new Entry(xEntry,0f));
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet = new LineDataSet(yAxesYAcceleration,"y-Axes-Acceleration");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(Color.BLUE);
        lineDataSets.add(lineDataSet);
        accelerationChart.setData(new LineData(lineDataSets));
        accelerationChart.setVisibleXRangeMaximum(65f);
        accelerationChart.invalidate();
    }

    public void addAccelerationEntry() {
        LineData data = accelerationChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), breathingAnalysis.getCurrentYValue()), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            accelerationChart.notifyDataSetChanged();

            // limit the number of visible entries
            accelerationChart.setVisibleXRangeMaximum(120);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            accelerationChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
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
}
