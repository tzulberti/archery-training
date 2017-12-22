package ar.com.tzulberti.archerytraining.helper.charts;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.List;

/**
 * Used to set common values on the Horizontal bar charts so all of them seem to be the same,
 * so this is used to prevent copy paste
 * Created by tzulberti on 12/21/17.
 */
public class HorizontalBarChartHelper {

    public static void configureHorizonalBarChart(HorizontalBarChart horizontalBarChart, List<String> xAxis, BarData data) {
        XAxis xl = horizontalBarChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setValueFormatter(new IndexAxisValueFormatter(xAxis));
        xl.setGranularity(1);
        xl.setLabelCount(xAxis.size());

        // this is the upper Axis in this case, but we don't want to show
        // any label on the top
        YAxis yl = horizontalBarChart.getAxisLeft();
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
        yl.setEnabled(false);
        yl.setAxisMinimum(0f);

        // this the lower axis, that is the one being shown
        YAxis yr = horizontalBarChart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        horizontalBarChart.setData(data);
        horizontalBarChart.getLegend().setEnabled(false);
        horizontalBarChart.setEnabled(false);
        horizontalBarChart.setTouchEnabled(false);
    }
}
