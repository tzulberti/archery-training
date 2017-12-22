package ar.com.tzulberti.archerytraining.activities.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.edmodo.rangebar.RangeBar;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.tournament.ViewSerieInformationActivity;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.helper.charts.BarAxisValueFormattter;
import ar.com.tzulberti.archerytraining.model.base.AbstractArrow;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;

/**
 * Used to show the stats for one tournamnent/playoff
 *
 * This is different from AbstractContainersStatsActivity, because this is used to
 * show the stats for only one tournament/playoff while AbstractContainersStatsActivity
 * is used to show the stats for a range of values
 *
 * Created by tzulberti on 7/30/17.
 */
public class ContainerStatsActivity extends BaseArcheryTrainingActivity {

    private ImageView targetImageView;
    private RangeBar rangeBar;
    private TextView seriesShowingText;

    private Bitmap imageBitmap;

    private Paint finalImpactPaint;
    private Paint centerPointPaint;
    private Paint impactsAreaPaint;

    private ISerieContainer container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.createDAOs();
        setContentView(R.layout.common_view_container_arrow_stats);


        this.container = (ISerieContainer) this.getIntent().getSerializableExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY);
        this.targetImageView = (ImageView) this.findViewById(R.id.photo_view);
        this.rangeBar = (RangeBar) this.findViewById(R.id.tournament_series_rangebar);
        this.seriesShowingText = (TextView) this.findViewById(R.id.tournament_series_showing);


        this.finalImpactPaint = new Paint();
        this.finalImpactPaint.setAntiAlias(true);
        this.finalImpactPaint.setColor(Color.LTGRAY);

        this.centerPointPaint = new Paint();
        this.centerPointPaint.setAntiAlias(true);
        this.centerPointPaint.setColor(Color.GREEN);


        this.impactsAreaPaint = new Paint();
        this.impactsAreaPaint.setStrokeWidth(3);
        this.impactsAreaPaint.setStyle(Paint.Style.STROKE);
        this.impactsAreaPaint.setColor(Color.GREEN);
        this.impactsAreaPaint.setAlpha(255);
        this.impactsAreaPaint.setAntiAlias(true);


        ViewTreeObserver vto = this.targetImageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                targetImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                initializeValues();
                return true;
            }
        });
    }


    protected void initializeValues() {
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.complete_archery_target, myOptions);

        if (this.container.getSeries().size() > 1) {
            this.rangeBar.setTickCount(this.container.getSeries().size());
            this.rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
                @Override
                public void onIndexChangeListener(RangeBar rangeBar, int start, int end) {
                    showSeries(start +1 , end +1);
                }
            });
        } else {
            this.rangeBar.setVisibility(View.INVISIBLE);
        }

        this.imageBitmap = Bitmap.createBitmap(bitmap);
        this.showSeries(1, this.container.getSeries().size());
    }


    private void showSeries(int minSerie, int maxSerie) {
        List<ISerie> series = new ArrayList<>();

        for (ISerie serie : this.container.getSeries()) {
            if (minSerie <= serie.getIndex() && serie.getIndex() <= maxSerie) {
                series.add(serie);
            }
        }

        this.showTargetImpacts(series);
        this.renderSeriesChart((LineChart) this.findViewById(R.id.tournament_series_chart), series);
        this.renderArrowsChart((HorizontalBarChart) this.findViewById(R.id.tournament_arrows_horizontal_chart), series);
        this.showTableValues((TableLayout) this.findViewById(R.id.tournament_stats_table_data), series);

        this.seriesShowingText.setText(getString(R.string.common_container_stats_showing_series, minSerie, maxSerie));
    }

    private void showTargetImpacts(List<ISerie> series) {
        Bitmap mutableBitmap = this.imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int numberOfArrows = 0;
        int sumX = 0;
        int sumY = 0;


        for (ISerie serie : series) {
            for (AbstractArrow arrow : serie.getArrows()) {
                this.addTargetImpact(arrow.xPosition, arrow.yPosition, mutableBitmap, this.finalImpactPaint, ViewSerieInformationActivity.ARROW_IMPACT_RADIUS);
                sumX += arrow.xPosition;
                sumY += arrow.yPosition;
                numberOfArrows += 1;
            }
        }

        if (numberOfArrows > 0) {
            float maxDistance = 0;
            float centerX = sumX / numberOfArrows;
            float centerY = sumY / numberOfArrows;

            this.addTargetImpact(centerX, centerY, mutableBitmap, this.centerPointPaint, ViewSerieInformationActivity.ARROW_IMPACT_RADIUS);

            for (ISerie serie : series) {
                for (AbstractArrow arrow : serie.getArrows()) {
                    double currentArrowDistance = Math.sqrt(Math.pow(arrow.xPosition - centerX, 2) + Math.pow(arrow.yPosition - centerY, 2));
                    if (currentArrowDistance > maxDistance) {
                        maxDistance = (float) currentArrowDistance;
                    }
                }
            }

            this.addTargetImpact(centerX, centerY, mutableBitmap, this.impactsAreaPaint, maxDistance);
        }
    }


    private void addTargetImpact(float x, float y, Bitmap mutableBitmap, Paint impactPaint, float radiusSize) {
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawCircle(x, y, radiusSize, impactPaint);

        this.targetImageView.setAdjustViewBounds(true);
        this.targetImageView.setImageBitmap(mutableBitmap);
    }


    private void renderSeriesChart(LineChart lineChart, List<ISerie> series) {
        List<Entry> entries = new ArrayList<>();

        for (ISerie serie : series) {
            entries.add(new Entry(serie.getIndex(), serie.getTotalScore()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);

        XAxis xl = lineChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(1);
        xl.setLabelCount(series.size());

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

    private void renderArrowsChart(HorizontalBarChart horizontalBarChart, List<ISerie> series) {
        Map<String, Integer> arrowsCounter = new HashMap<>();
        int totalArrows = 0;
        for (ISerie serie : series) {
            totalArrows += serie.getArrows().size();
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

        List<String> stackLabels = new ArrayList<>();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
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
            stackLabels.add("123123123");
        }


        String[] foobar = Arrays.copyOf(stackLabels.toArray(), stackLabels.size(), String[].class);
        BarDataSet set1 = new BarDataSet(arrowsCounterSet, "");
        set1.setColors(colors);
        set1.setStackLabels(foobar);
        BarData data = new BarData();
        data.addDataSet(set1);
        data.setValueFormatter(new BarAxisValueFormattter(totalArrows));


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
        horizontalBarChart.getDescription().setText(getString(R.string.tournament_view_stats_arrow_chart_description));
        horizontalBarChart.invalidate();
    }


    private void showTableValues(TableLayout tableLayout, List<ISerie> series) {
        List<Integer> allArrowScore = new ArrayList<>();
        List<Integer> allSeriesScores = new ArrayList<>();

        // remove all the rows from the table with the exception of the header
        int count = tableLayout.getChildCount();
        for (int i = 1; i < count; i++) {
            View child = tableLayout.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }

        for (ISerie serie : series) {
            for (AbstractArrow arrow : serie.getArrows()) {
                allArrowScore.add(arrow.score);
            }
            allSeriesScores.add(serie.getTotalScore());
        }

        if (allArrowScore.isEmpty()) {
            return;
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
