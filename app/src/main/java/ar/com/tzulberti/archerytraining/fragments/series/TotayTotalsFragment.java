package ar.com.tzulberti.archerytraining.fragments.series;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.DistanceTotalData;

/**
 * Created by tzulberti on 4/21/17.
 */

public class TotayTotalsFragment extends BaseSeriesFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);

        View view = inflater.inflate(R.layout.series_today_data, container, false);
        this.setObjects();

        List<DistanceTotalData> distanceTotalDatas = this.serieDataDAO.getTotalsForDistance(
                DatetimeHelper.getTodayZeroHours(),
                DatetimeHelper.getTomorrowZeroHours()
        );
        HorizontalBarChart horizontalBarChart = (HorizontalBarChart) view.findViewById(R.id.todayTotals);
        this.showPieChartInformation(horizontalBarChart, distanceTotalDatas);
        return view;
    }

    @Override
    public void handleClick(View v) {

    }

    private void showPieChartInformation(HorizontalBarChart horizontalBarChart, List<DistanceTotalData> distanceTotalDatas) {

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }

        List<BarEntry> arrowsCounterSet = new ArrayList<>();
        List<String> xAxis = new ArrayList<>();

        int index = 0;
        for (DistanceTotalData data : distanceTotalDatas) {
            arrowsCounterSet.add(new BarEntry(index, data.totalArrows));
            xAxis.add(String.valueOf(data.distance));
            index += 1;
        }


        BarDataSet set1 = new BarDataSet(arrowsCounterSet, "");
        set1.setColors(colors);
        BarData data = new BarData();
        data.addDataSet(set1);


        XAxis xl = horizontalBarChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setValueFormatter(new IndexAxisValueFormatter(xAxis));
        xl.setGranularity(1);
        xl.setLabelCount(xAxis.size());

        YAxis yl = horizontalBarChart.getAxisLeft();
        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yl.setDrawGridLines(false);
        yl.setEnabled(false);
        yl.setAxisMinimum(0f);

        YAxis yr = horizontalBarChart.getAxisRight();
        yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        horizontalBarChart.setData(data);
        horizontalBarChart.getLegend().setEnabled(false);
        horizontalBarChart.setEnabled(false);
        horizontalBarChart.setTouchEnabled(false);
        horizontalBarChart.getDescription().setText(getString(R.string.series_today_totals_chart_description));
        horizontalBarChart.invalidate();
    }

}
