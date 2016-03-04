package com.cardea.beatopen.plotutils;

import android.graphics.Color;
import android.util.Log;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import static com.cardea.beatopen.Globals.*;

public class Chart {
    private static final String TAG = "BeatOpen PlotGraph";
    public static void drawGraph(int samplingRate, Integer[] buffer, int lastIndex, XYMultipleSeriesDataset dataSet, XYMultipleSeriesRenderer multiRenderer) {

        Log.d(TAG, "Inside drawGraph+++++++++++");
        // Plotting real graph
        XYSeries dataSeries = new XYSeries("BEAT Plot");
        for (int i = 0; i < lastIndex; i++)
            dataSeries.add(i, buffer[i]);
        XYSeriesRenderer dataRenderer;
        dataRenderer = new XYSeriesRenderer();
        dataRenderer.setColor(Color.GREEN);
        dataRenderer.setLineWidth(3);

        //Adding all the datasets to the main dataSet
        dataSet.addSeries(dataSeries);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        multiRenderer.setShowGrid(true);//show grid lines
        multiRenderer.setGridColor(Color.DKGRAY);

        //set range of the axes
        multiRenderer.setXAxisMin(0);
        multiRenderer.setXAxisMax(samplingRate * 3);
        multiRenderer.setYAxisMin(0);
        multiRenderer.setYAxisMax(255);
        multiRenderer.setShowLabels(false);
        multiRenderer.setShowLegend(false);

        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.BLACK);
        multiRenderer.setShowAxes(false);

        multiRenderer.setZoomEnabled(true, false);
        multiRenderer.setPanEnabled(true, false);
        multiRenderer.setPanEnabled(true, false);
        multiRenderer.setPanLimits(new double[]{0, lastIndex, 0, 0});

        //finally add all the series renderers to the overall graph renderer
        multiRenderer.addSeriesRenderer(dataRenderer);
    }
}
