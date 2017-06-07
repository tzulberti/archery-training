package ar.com.tzulberti.archerytraining.fragments.tournament;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Created by tzulberti on 5/30/17.
 */

public class ViewTournamentArrowStatsFragment extends BaseTournamentFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        View view = inflater.inflate(R.layout.tournament_view_tournament_arrow_stats, container, false);
        this.setObjects();

        Tournament tournament = this.getTournamentArgument();

        this.renderSeriesChart((LineChart) view.findViewById(R.id.tournament_series_chart), tournament);
        this.renderArrowsChart((HorizontalBarChart) view.findViewById(R.id.tournament_arrows_horizontal_chart), tournament);
        this.showTableValues((TableLayout) view.findViewById(R.id.tournament_stats_table_data), tournament, getContext());
        return view;
    }


    private void renderSeriesChart(LineChart lineChart, Tournament tournament) {
        List<Entry> entries = new ArrayList<Entry>();

        for (TournamentSerie serie : tournament.series) {
            entries.add(new Entry(serie.index, serie.totalScore));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);


        XAxis xl = lineChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(1);
        xl.setLabelCount(tournament.series.size());

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

    private void renderArrowsChart(HorizontalBarChart horizontalBarChart, Tournament tournament) {
        Map<String, Integer> arrowsCounter = new HashMap<>();
        for (TournamentSerie serie : tournament.series) {
            for (TournamentSerieArrow arrow : serie.arrows) {
                String score = TournamentHelper.getUserScore(arrow.score, arrow.isX);
                Integer existingCounts = arrowsCounter.getOrDefault(score, 0);
                arrowsCounter.put(score, existingCounts + 1);
            }
        }

        List<BarEntry> arrowsCounterSet = new ArrayList<>();
        List<String> xAxis = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int maxCounter = 0;
        for (int i = 0; i < 12; i++) {
            String score = null;
            Integer color = null;
            if (i == 11) {
                score = TournamentHelper.getUserScore(10, true);
                color = TournamentHelper.getBackground(10);
            } else {
                score = TournamentHelper.getUserScore(i, false);
                color = TournamentHelper.getBackground(i);
            }

            // only add it to the chart if it has data
            Integer counter = arrowsCounter.getOrDefault(score, 0);
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


    private void showTableValues(TableLayout tableLayout, Tournament tournament, Context context) {
        List<Integer> allArrowScore = new ArrayList<>();
        List<Integer> allSeriesScores = new ArrayList<>();
        for (TournamentSerie serie : tournament.series) {
            for (TournamentSerieArrow arrow : serie.arrows) {
                allArrowScore.add(arrow.score);
            }
            allSeriesScores.add(serie.totalScore);
        }

        TableRow trArrows = new TableRow(context);
        TextView arrowsText = new TextView(context);
        arrowsText.setText(getText(R.string.tournament_view_stats_arrow));
        trArrows.addView(arrowsText);
        this.populateTableValues(allArrowScore, trArrows, context);

        TableRow trSeries = new TableRow(context);
        TextView serieText = new TextView(context);
        serieText.setText(getText(R.string.tournament_view_stats_serie));
        trSeries.addView(serieText);
        this.populateTableValues(allSeriesScores, trSeries, context);

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

    @Override
    public void handleClick(View v) {
    }
}
