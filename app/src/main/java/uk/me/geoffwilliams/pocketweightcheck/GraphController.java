/*
 * pocketweightcheck -- simple android app to track of your weight
 * Copyright (C) 2014 Geoff Williams <geoff@geoffwilliams.me.uk>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package uk.me.geoffwilliams.pocketweightcheck;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.text.format.DateFormat;
import java.util.List;
import uk.me.geoffwilliams.pocketweightcheck.dao.Weight;

/**
 *
 * @author geoff
 */
public class GraphController {
    
    private java.text.DateFormat df;
    private GraphicalView mChart;
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private TimeSeries recordedWeights;
    private TimeSeries trendWeights;
   
    private XYSeriesRenderer recordedWeightsRenderer = new XYSeriesRenderer();
    private XYSeriesRenderer trendWeightsRenderer = new XYSeriesRenderer();
    private String TAG = "pocketweightcheck.GraphController";

    public int getDataPointCount() {
        int trendCount = (trendWeights == null) ? 0 : trendWeights.getItemCount();
        int recordedCount = (recordedWeights == null) ? 0 : recordedWeights.getItemCount();
        
        if (trendCount != recordedCount) {
            throw new RuntimeException("recorded and trend weights lists are different sizes!");
        }
        return recordedCount;
    }
    /**
     * Update graph with new data and calculate trend:
     * http://www.fourmilab.ch/hackdiet/e4/pencilpaper.html#PencilTrend
     *
     */
    public void updateGraph(List<Weight> weights, Context context) {

        

        Double[] reading;

        // overall data statistics - used to ensure line graph isn't rendered
        // offscreen - eg if all measurements are the same
        double min = 999;
        double max = 0;


        for (Weight weight: weights) {
            min = Math.min(min, weight.getWeight());
            max = Math.max(max, weight.getWeight());

            addData(weight);

        }

        Log.d(TAG, "trendWeights size" + trendWeights.getItemCount());
        Log.d(TAG, "recordedWeights size" + recordedWeights.getItemCount());

        // 10 % of effective range... - use to pad graph
        double range = (max - min) * 0.1;

        //     mRenderer.setYAxisMin(min - range);
        //     mRenderer.setYAxisMax(max + range);


        mChart.repaint();
    }

    private void addData(Weight weight) {
        recordedWeights.add(weight.getSampleTime(), weight.getWeight());

        // short date string for x axis label
        String dateFormatted = df.format(weight.getSampleTime());

        mRenderer.addXTextLabel(weight.getSampleTime().getTime(), dateFormatted);


        trendWeights.add(weight.getSampleTime(), weight.getTrend());
    }

    public void setupGraph(Context context) {
        df = DateFormat.getDateFormat(context);
        
        recordedWeights = new TimeSeries("Recorded weights");
        trendWeights = new TimeSeries("Trend weights");

        mDataset.addSeries(recordedWeights);
        mDataset.addSeries(trendWeights);

        recordedWeightsRenderer.setColor(Color.RED);
        recordedWeightsRenderer.setFillPoints(true);
        recordedWeightsRenderer.setPointStyle(PointStyle.CIRCLE);

        trendWeightsRenderer.setColor(Color.GREEN);

        mRenderer.addSeriesRenderer(recordedWeightsRenderer);
        mRenderer.addSeriesRenderer(trendWeightsRenderer);
        
        // text size
        int FONT_SIZE = 18;
        mRenderer.setAxisTitleTextSize(FONT_SIZE);
        mRenderer.setChartTitleTextSize(FONT_SIZE);
        mRenderer.setLabelsTextSize(FONT_SIZE);
        mRenderer.setLegendTextSize(FONT_SIZE);
        mRenderer.setBarSpacing(250);
        mRenderer.setXLabels(0);
        mRenderer.setXLabelsAngle(45);
        mRenderer.setXLabelsAlign(Paint.Align.LEFT);
        mChart = ChartFactory.getCubeLineChartView(context, mDataset, mRenderer, 0.1f);
        // example of how to use the time-series formatter - but you lose the smooth
        // rendering and the dateformatting doesn't do what I want...
        //mChart = ChartFactory.getTimeChartView(context, mDataset, mRenderer, "YYYY-mm-dd");//0.3f);

    }

    public GraphicalView getChart() {
        return mChart;
    }

    /**
     * Add a new reading - no need to rescan all data, just merge in the new
     * observation and render it...
     */
    public void addNewReading(Weight weight) {
        addData(weight);   
        mChart.repaint();
    }
}
