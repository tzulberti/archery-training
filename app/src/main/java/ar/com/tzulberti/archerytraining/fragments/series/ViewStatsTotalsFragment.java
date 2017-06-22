package ar.com.tzulberti.archerytraining.fragments.series;

import android.graphics.Color;
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
import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.helper.charts.TimeAxisValueFormatter;
import ar.com.tzulberti.archerytraining.model.series.ArrowsPerDayData;
import ar.com.tzulberti.archerytraining.model.series.ArrowsPerTrainingType;
import ar.com.tzulberti.archerytraining.model.series.DistanceTotalData;

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

        List<ArrowsPerTrainingType> arrowsPerTrainingTypes = this.serieDataDAO.getTotalsForTrainingType(
                minDate,
                maxDate
        );



        HorizontalBarChart horizontalBarChart = (HorizontalBarChart) view.findViewById(R.id.series_distance_arrows);
        HorizontalBarChart seriesPerTrainingTypeChart = (HorizontalBarChart) view.findViewById(R.id.series_per_training_type);
        LineChart lineChart = (LineChart) view.findViewById(R.id.series_daily_arrows);

        TextView dailyChartDescription = (TextView) view.findViewById(R.id.series_daily_arrow_text);
        switch (periodType) {
            case DAILY:
                dailyChartDescription.setText(this.getString(R.string.series_line_day_chart_description));
                break;
            case HOURLY:
                dailyChartDescription.setText(this.getString(R.string.series_line_hour_chart_description));
                break;
        }


        this.showArrowsPerDistance(distanceTotalDatas, horizontalBarChart);
        this.showArrowsPerDay(arrowsPerDayDatas, lineChart, periodType);
        this.showArrowsPerTrainingType(arrowsPerTrainingTypes, seriesPerTrainingTypeChart);
        return view;
    }

    private void showArrowsPerDistance(List<DistanceTotalData> distanceTotalDatas, HorizontalBarChart horizontalBarChart) {
        if (distanceTotalDatas == null || distanceTotalDatas.isEmpty()) {
            // there is no information so don't show any chart
            return;
        }
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


    private void showArrowsPerTrainingType(List<ArrowsPerTrainingType> arrowsPerTrainingTypes, HorizontalBarChart horizontalBarChart) {
        if (arrowsPerTrainingTypes == null || arrowsPerTrainingTypes.isEmpty()) {
            // there is no information so don't show any chart
            return;
        }
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
        for (ArrowsPerTrainingType data : arrowsPerTrainingTypes) {
            arrowsCounterSet.add(new BarEntry(index, data.totalArrows));
            SerieInformationConsts.TrainingType trainingType = SerieInformationConsts.TrainingType.getFromValue(data.trainingType);
            int trainingTypeStringKey = 0;
            switch (trainingType) {
                case FREE:
                    trainingTypeStringKey = R.string.training_type_free;
                    break;
                case PLAYOFF:
                    trainingTypeStringKey = R.string.training_type_playoff;
                    break;
                case TOURNAMENT:
                    trainingTypeStringKey = R.string.training_type_tournament;
                    break;
                case RETENTIONS:
                    trainingTypeStringKey = R.string.training_type_retentions;
                    break;
                default:
                    throw new RuntimeException("Unknown training type: " + data.trainingType);
            }
            xAxis.add(this.getString(trainingTypeStringKey));
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
        if (arrowsPerDayDatas == null || arrowsPerDayDatas.isEmpty()) {
            // there is no information so don't show any chart
            return;
        }

        List<Entry> entries = new ArrayList<Entry>();
        List<Entry> accumulatedEntries = new ArrayList<>();

        long minValue = -1;
        long hoursTaken = 0;
        long accumulated = 0;
        for (ArrowsPerDayData data : arrowsPerDayDatas) {
            long epochValue = DatetimeHelper.dateToDatabaseValue(data.day);
            if (minValue == -1) {
                minValue = epochValue;
            }
            hoursTaken = epochValue;
            accumulated += data.totalArrows;
            entries.add(new Entry(epochValue - minValue, data.totalArrows));
            accumulatedEntries.add(new Entry(epochValue - minValue, accumulated));
        }

        LineDataSet dailyDataSet = new LineDataSet(entries, this.getString(R.string.per_time_period_label));
        dailyDataSet.setColors(Color.RED);

        LineDataSet accumulatedDataSet = new LineDataSet(accumulatedEntries, this.getString(R.string.accumulated_label));
        accumulatedDataSet.setColors(Color.BLUE);
        LineData lineData = new LineData(dailyDataSet, accumulatedDataSet);

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
        lineChart.getLegend().setEnabled(true);
        lineChart.setEnabled(trtue);
        lineChart.setTouchEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }

    @Override
    public void handleClick(View v) {

    }

}

