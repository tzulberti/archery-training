package ar.com.tzulberti.archerytraining.fragments.series;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.ArrowsPerDayData;
import ar.com.tzulberti.archerytraining.model.DistanceTotalData;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;

/**
 * Created by tzulberti on 6/13/17.
 */

public class ViewCurrentMonthTotalsFragment extends BaseSeriesFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);

        View view = inflater.inflate(R.layout.series_period_data, container, false);
        this.setObjects();

        List<DistanceTotalData> distanceTotalDatas = this.serieDataDAO.getTotalsForDistance(
                DatetimeHelper.getFirstDateOfMonth(),
                DatetimeHelper.getLastDateOfMonth()
        );

        List<ArrowsPerDayData> arrowsPerDayDatas = this.serieDataDAO.getDailyArrowsInformation(
                DatetimeHelper.getFirstDateOfMonth(),
                DatetimeHelper.getLastDateOfMonth()
        );

        HorizontalBarChart horizontalBarChart = (HorizontalBarChart) view.findViewById(R.id.series_distance_arrows);
        LineChart lineChart = (LineChart) view.findViewById(R.id.series_daily_arrows);
        this.showArrowsPerDistance(distanceTotalDatas, horizontalBarChart);
        this.showArrowsPerDay(arrowsPerDayDatas, lineChart);
        return view;
    }

    private void showArrowsPerDistance(List<DistanceTotalData> distanceTotalDatas, HorizontalBarChart horizontalBarChart) {
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

    private void showArrowsPerDay(List<ArrowsPerDayData> arrowsPerDayDatas, LineChart lineChart) {
        List<Entry> entries = new ArrayList<Entry>();

        int index = 0;
        List<String> xAxis = new ArrayList<>();
        for (ArrowsPerDayData data : arrowsPerDayDatas) {
            entries.add(new Entry(index, data.totalArrows));
            xAxis.add(DatetimeHelper.DATE_FORMATTER.format(data.day));
            index += 1;
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        LineData lineData = new LineData(dataSet);

        XAxis xl = lineChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setValueFormatter(new IndexAxisValueFormatter(xAxis));
        xl.setGranularity(1);

        YAxis yl = lineChart.getAxisLeft();
        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yl.setDrawGridLines(true);

        YAxis yr = lineChart.getAxisRight();
        yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yr.setDrawGridLines(false);
        yr.setEnabled(false);

        lineChart.setData(lineData);
        lineChart.getLegend().setEnabled(false);
        lineChart.setEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.getDescription().setText(getString(R.string.tournament_view_stats_series_chart_description));
        lineChart.invalidate();
    }

    @Override
    public void handleClick(View v) {

    }

}
