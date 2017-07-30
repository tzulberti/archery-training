package ar.com.tzulberti.archerytraining.activities.common;

import android.content.Context;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;

/**
 * Used to show the stats for one tournamnent/playoff
 *
 * Created by tzulberti on 7/30/17.
 */
public class ContainerStatsActivity extends BaseArcheryTrainingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.createDAOs();
        setContentView(R.layout.common_view_container_arrow_stats);


        ISerieContainer container = (ISerieContainer) this.getIntent().getSerializableExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY);

        this.renderSeriesChart((LineChart) this.findViewById(R.id.tournament_series_chart), container);
        this.renderArrowsChart((HorizontalBarChart) this.findViewById(R.id.tournament_arrows_horizontal_chart), container);
        this.showTableValues((TableLayout) this.findViewById(R.id.tournament_stats_table_data), container);
    }


    private void renderSeriesChart(LineChart lineChart, ISerieContainer container) {
        List<Entry> entries = new ArrayList<>();

        for (ISerie serie : container.getSeries()) {
            entries.add(new Entry(serie.getIndex(), serie.getTotalScore()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);


        XAxis xl = lineChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(1);
        xl.setLabelCount(container.getSeries().size());

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

    private void renderArrowsChart(HorizontalBarChart horizontalBarChart, ISerieContainer container) {
        Map<String, Integer> arrowsCounter = new HashMap<>();
        for (ISerie serie : container.getSeries()) {
            for (AbstractArrow arrow : serie.getArrows()) {
                String score = TournamentHelper.getUserScore(arrow.score, arrow.isX);
                Integer existingCounts = arrowsCounter.get(score);
                if (existingCounts == null) {
                    existingCounts = 0;
                }
                arrowsCounter.put(score, existingCounts + 1);
            }
        }

        List<BarEntry> arrowsCounterSet = new ArrayList<>();
        List<String> xAxis = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int maxCounter = 0;
        for (int i = 0; i < 12; i++) {
            String score;
            Integer color;
            if (i == 11) {
                score = TournamentHelper.getUserScore(10, true);
                color = TournamentHelper.getBackground(10);
            } else {
                score = TournamentHelper.getUserScore(i, false);
                color = TournamentHelper.getBackground(i);
            }

            // only add it to the chart if it has data
            Integer counter = arrowsCounter.get(score);
            if (counter == null) {
                counter = 0;
            }
            if (counter > maxCounter) {
                maxCounter = counter;
            }
            arrowsCounterSet.add(new BarEntry(i, counter));
            xAxis.add(score);
            colors.add(color);
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
        horizontalBarChart.getDescription().setText(getString(R.string.tournament_view_stats_arrow_chart_description));
        horizontalBarChart.invalidate();
    }


    private void showTableValues(TableLayout tableLayout, ISerieContainer container) {
        List<Integer> allArrowScore = new ArrayList<>();
        List<Integer> allSeriesScores = new ArrayList<>();
        for (ISerie serie : container.getSeries()) {
            for (AbstractArrow arrow : serie.getArrows()) {
                allArrowScore.add(arrow.score);
            }
            allSeriesScores.add(serie.getTotalScore());
        }

        TableRow trArrows = new TableRow(this);
        TextView arrowsText = new TextView(this);
        arrowsText.setText(getText(R.string.tournament_view_stats_arrow));
        trArrows.addView(arrowsText);
        this.populateTableValues(allArrowScore, trArrows, this);

        TableRow trSeries = new TableRow(this);
        TextView serieText = new TextView(this);
        serieText.setText(getText(R.string.tournament_view_stats_serie));
        trSeries.addView(serieText);
        this.populateTableValues(allSeriesScores, trSeries, this);

        tableLayout.addView(trArrows);
        tableLayout.addView(trSeries);
    }

    private void populateTableValues(List<Integer> values, TableRow tr, Context context) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        Collections.sort(values);
        Integer[] valuesArray = new Integer[values.size()];
        valuesArray = values.toArray(valuesArray);

        float sum  = 0;
        for (Integer score : valuesArray) {
            sum += score;
        }

        TextView minTextView = new TextView(context);
        minTextView.setText(String.valueOf(valuesArray[0]));

        TextView avgTextView = new TextView(context);
        avgTextView.setText(df.format(sum / valuesArray.length));

        TextView meanTextView = new TextView(context);
        if (values.size() % 2 == 1) {
            meanTextView.setText(df.format(valuesArray[valuesArray.length / 2]));
        } else {
            int midIndex = values.size() / 2;
            meanTextView.setText(df.format((valuesArray[midIndex - 1] + valuesArray[midIndex]) / 2.0f));
        }

        TextView maxTextView = new TextView(context);
        maxTextView.setText(String.valueOf(valuesArray[valuesArray.length - 1]));

        tr.addView(minTextView);
        tr.addView(avgTextView);
        tr.addView(meanTextView);
        tr.addView(maxTextView);
    }
}
