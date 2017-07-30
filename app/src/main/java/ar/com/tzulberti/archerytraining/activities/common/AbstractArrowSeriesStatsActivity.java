package ar.com.tzulberti.archerytraining.activities.common;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


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
import ar.com.tzulberti.archerytraining.dao.BaseArrowSeriesDAO;
import ar.com.tzulberti.archerytraining.helper.AppCache;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.common.ArrowsPerScore;
import ar.com.tzulberti.archerytraining.model.common.IElementByScore;
import ar.com.tzulberti.archerytraining.model.common.SeriesPerScore;
import ar.com.tzulberti.archerytraining.model.common.TournamentConstraint;


/**
 * Show the stats for the playoffs on the selected date range
 *
 * Created by tzulberti on 6/25/17.
 */

public abstract class AbstractArrowSeriesStatsActivity extends BaseArcheryTrainingActivity implements AdapterView.OnItemSelectedListener {

    protected BaseArrowSeriesDAO baseArrowSeriesDAO;
    protected HorizontalBarChart seriesStatsHorizontalBarChart;
    protected HorizontalBarChart arrowsStatsHorizontalBarChart;
    protected TableLayout tableStats;

    /**
     * Sets the DAO used to get the information from the database
     */
    protected abstract BaseArrowSeriesDAO setBaseArrowSeriesDAO();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.createDAOs();
        setContentView(R.layout.common_view_stats);

        Spinner tournamentConstrainsSpinner = (Spinner) this.findViewById(R.id.tournament_constrains);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                AppCache.tournamentTypes
        );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tournamentConstrainsSpinner.setAdapter(dataAdapter);
        tournamentConstrainsSpinner.setOnItemSelectedListener(this);

        this.baseArrowSeriesDAO = this.setBaseArrowSeriesDAO();
        this.tableStats = (TableLayout) this.findViewById(R.id.playoff_stats_table_data);
        this.arrowsStatsHorizontalBarChart = (HorizontalBarChart) this.findViewById(R.id.playoff_stats_arrow_stats);
        this.seriesStatsHorizontalBarChart = (HorizontalBarChart) this.findViewById(R.id.playoff_stats_series_stats);
    }


    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        TournamentConstraint tournamentConstraint = AppCache.tournamentConstraintSpinnerMap.get(AppCache.tournamentTypes.get(pos));
        this.showCharts(tournamentConstraint);
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void showCharts(TournamentConstraint tournamentConstraint) {
        List<SeriesPerScore> seriesPerScoreList = this.baseArrowSeriesDAO.getSeriesPerScore(tournamentConstraint);
        this.showSeriesPerScore(seriesPerScoreList, this.seriesStatsHorizontalBarChart);

        List<ArrowsPerScore> arrowsPerScores = this.baseArrowSeriesDAO.getArrowsPerScore(tournamentConstraint);
        this.showArrowsPerScore(arrowsPerScores, this.arrowsStatsHorizontalBarChart);

        this.showTableStats(arrowsPerScores, seriesPerScoreList, this.tableStats );
    }


    private void showSeriesPerScore(List<SeriesPerScore> seriesPerScores, HorizontalBarChart horizontalBarChart) {
        if (seriesPerScores == null || seriesPerScores.isEmpty()) {
            horizontalBarChart.clear();
            return;
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }

        List<BarEntry> seriesCounterSet = new ArrayList<>();
        List<String> xAxis = new ArrayList<>();

        int index = 0;
        for (SeriesPerScore data : seriesPerScores) {
            seriesCounterSet.add(new BarEntry(index, data.seriesAmount));
            xAxis.add(String.valueOf(data.serieScore));
            index += 1;
        }

        BarDataSet set1 = new BarDataSet(seriesCounterSet, "");
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


    private void showArrowsPerScore(List<ArrowsPerScore> arrowsPerScores, HorizontalBarChart horizontalBarChart) {
        if (arrowsPerScores == null || arrowsPerScores.isEmpty()) {
            horizontalBarChart.clear();
            return;
        }
        List<BarEntry> arrowsCounterSet = new ArrayList<>();
        List<String> xAxis = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int index = 0;
        for (ArrowsPerScore arrowsPerScore : arrowsPerScores) {
            String score = TournamentHelper.getUserScore(arrowsPerScore.score, arrowsPerScore.isX);
            Integer color = TournamentHelper.getBackground(arrowsPerScore.score);
            arrowsCounterSet.add(new BarEntry(index, arrowsPerScore.arrowsAmount));
            xAxis.add(score);
            colors.add(color);
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
        horizontalBarChart.getDescription().setEnabled(false);
        horizontalBarChart.invalidate();
    }


    private void showTableStats(List<ArrowsPerScore> arrowsPerScores, List<SeriesPerScore> seriesPerScores, TableLayout tableLayout) {
        // remove all the rows all the previous data if there is any
        int count = tableLayout.getChildCount();
        for (int i = 1; i < count; i++) {
            View child = tableLayout.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }

        if (arrowsPerScores == null || arrowsPerScores.isEmpty() || seriesPerScores == null || seriesPerScores.isEmpty()) {
            return;
        }

        TableRow arrowsStats = new TableRow(this);
        TextView arrowsStatsLabel = new TextView(this);
        arrowsStatsLabel.setText(R.string.stats_arrow_table_data);
        arrowsStats.addView(arrowsStatsLabel);
        this.populateRowData(arrowsPerScores, arrowsStats, this);

        TableRow seriesStats = new TableRow(this);
        TextView seriesStatsLable = new TextView(this);
        seriesStatsLable.setText(R.string.stats_series_table_data);
        seriesStats.addView(seriesStatsLable);
        this.populateRowData(seriesPerScores, seriesStats, this);

        tableLayout.addView(arrowsStats);
        tableLayout.addView(seriesStats);
    }

    private void populateRowData(List<? extends IElementByScore> scoreData, TableRow tr, Context context) {
        int min = -1;
        int max = -1;
        int count = 0;
        float sum = 0;

        for (IElementByScore elementByScore : scoreData) {
            int currentCount = elementByScore.getAmount();
            if (currentCount == 0) {
                // the value might just be included in the list just
                // to complete missing values
                continue;
            }

            int currentScore = elementByScore.getScore();
            count += currentCount;
            sum += currentScore * currentCount;
            if (min == -1 && count > 0) {
                min = currentScore;
                max = currentScore;
            }

            if (min > currentScore) {
                min = currentScore;
            }

            if (max < currentScore) {
                max = currentScore;
            }
        }

        double median = 0;
        int currentCount = 0;
        int countIndex = count / 2;
        int previousValue = 0;
        for (IElementByScore elementByScore : scoreData) {
            if (currentCount < countIndex && countIndex < currentCount + elementByScore.getAmount()) {
                // if the value is in the current set, then the median is the current value
                median = elementByScore.getScore();
                break;
            } else if (count % 2 == 1 && (currentCount == countIndex || currentCount + elementByScore.getAmount() == countIndex)) {
                median = elementByScore.getScore();
                break;
            } else if (count % 2 == 0 && currentCount == countIndex) {
                median = (previousValue + elementByScore.getScore()) / 2.0;
                break;
            } else if (count % 2 == 0 && currentCount + elementByScore.getAmount()  == countIndex) {
                previousValue = elementByScore.getScore();
            }
            currentCount += elementByScore.getAmount();
        }

        if (count > 0) {
            TextView minTextView = new TextView(context);
            minTextView.setText(String.valueOf(min));

            TextView avgTextView = new TextView(context);
            avgTextView.setText(String.valueOf(sum / count));

            TextView medianTextView = new TextView(context);
            medianTextView.setText(String.valueOf(median));

            TextView maxTextView = new TextView(context);
            maxTextView.setText(String.valueOf(max));

            tr.addView(minTextView);
            tr.addView(avgTextView);
            tr.addView(medianTextView);
            tr.addView(maxTextView);
        }

    }
}
