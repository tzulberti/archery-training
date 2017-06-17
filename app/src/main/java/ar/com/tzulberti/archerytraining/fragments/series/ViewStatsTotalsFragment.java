package ar.com.tzulberti.archerytraining.fragments.series;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.helper.charts.TimeAxisValueFormatter;
import ar.com.tzulberti.archerytraining.model.ArrowsPerDayData;
import ar.com.tzulberti.archerytraining.model.DistanceTotalData;

/**
 * Created by tzulberti on 6/13/17.
 */

public class ViewStatsTotalsFragment extends BaseSeriesFragment {

    public static final String PERIOD_TO_GROUP_BY_KEY = "period";
    public static final String MIN_DATE_KEY = "min_date";
    public static final String MAX_DATE_KEY = "max_date";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);

        View view = inflater.inflate(R.layout.series_period_data, container, false);
        this.setObjects();

        Bundle arguments = this.getArguments();
        long minDate = arguments.getLong(MIN_DATE_KEY);
        long maxDate = arguments.getLong(MAX_DATE_KEY);
        SerieDataDAO.GroupByType periodType = (SerieDataDAO.GroupByType) arguments.getSerializable(PERIOD_TO_GROUP_BY_KEY);

        List<DistanceTotalData> distanceTotalDatas = this.serieDataDAO.getTotalsForDistance(
                minDate,
                maxDate
        );

        List<ArrowsPerDayData> arrowsPerDayDatas = this.serieDataDAO.getDailyArrowsInformation(
                minDate,
                maxDate,
                periodType
        );

        HorizontalBarChart horizontalBarChart = (HorizontalBarChart) view.findViewById(R.id.series_distance_arrows);
        LineChart lineChart = (LineChart) view.findViewById(R.id.series_daily_arrows);

        TextView dailyChartDescription = (TextView) view.findViewById(R.id.series_daily_arrow_text);
        SimpleDateFormat dateFormat = null;
        switch (periodType) {
            case DAILY:
                dailyChartDescription.setText(this.getString(R.string.series_line_day_chart_description));
                dateFormat = DatetimeHelper.DATE_FORMATTER;
                break;
            case HOURLY:
                dailyChartDescription.setText(this.getString(R.string.series_line_hour_chart_description));
                dateFormat = DatetimeHelper.TIME_FORMATTER;
                break;
        }


        this.showArrowsPerDistance(distanceTotalDatas, horizontalBarChart);
        this.showArrowsPerDay(arrowsPerDayDatas, lineChart, periodType);
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
        horizontalBarChart.setMinimumHeight(xAxis.size() * 90);
        horizontalBarChart.getDescription().setEnabled(false);
        horizontalBarChart.invalidate();
    }

    private void showArrowsPerDay(List<ArrowsPerDayData> arrowsPerDayDatas, LineChart lineChart, SerieDataDAO.GroupByType periodType) {
        List<Entry> entries = new ArrayList<Entry>();

        int index = 0;
        List<String> xAxis = new ArrayList<>();
        long minValue = -1;
        long hoursTaken = 0;
        for (ArrowsPerDayData data : arrowsPerDayDatas) {
            long epochValue = DatetimeHelper.dateToDatabaseValue(data.day);
            if (minValue == -1) {
                minValue = epochValue;
            }
            hoursTaken = epochValue;
            entries.add(new Entry(epochValue - minValue, data.totalArrows));
            index += 1;
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        LineData lineData = new LineData(dataSet);

        DateFormat dateFormat = null;
        int periodSize = 0;
        switch (periodType) {
            case DAILY:
                dateFormat = DatetimeHelper.DATE_FORMATTER;
                periodSize = 86400;
                break;
            case HOURLY:
                dateFormat = DatetimeHelper.TIME_FORMATTER;
                periodSize = 3600;
                break;
        }

        XAxis xl = lineChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setValueFormatter(new TimeAxisValueFormatter(minValue, dateFormat));
        xl.setGranularity(1);
        xl.setLabelCount((int)(hoursTaken - minValue) / periodSize);

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
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }

    @Override
    public void handleClick(View v) {

    }

}
