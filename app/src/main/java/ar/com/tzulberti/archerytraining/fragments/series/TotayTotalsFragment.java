package ar.com.tzulberti.archerytraining.fragments.series;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.TodaysTotalData;

/**
 * Created by tzulberti on 4/21/17.
 */

public class TotayTotalsFragment extends BaseSeriesFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.today_total_data, container, false);
        this.setObjects();

        List<TodaysTotalData> todaysTotalDatas = this.serieDataDAO.getTodaysTotal();

        HashMap<Integer, Integer> distanceToColor = this.showPieChartInformation(view, todaysTotalDatas);

        this.showTextInformation(view, todaysTotalDatas, distanceToColor);
        return view;
    }

    @Override
    public void handleClick(View v) {

    }

    private HashMap<Integer, Integer> showPieChartInformation(View view, List<TodaysTotalData> todaysTotalDatas) {
        PieChart pieChart = (PieChart) view.findViewById(R.id.todayTotalsPieChart);
        long totalsArrows = 0;

        HashMap<Integer, Integer> distanceToColor = new HashMap<>();
        List<PieEntry> entries = new ArrayList<PieEntry>();
        for (TodaysTotalData data : todaysTotalDatas) {
            entries.add(new PieEntry(data.totalArrows, String.valueOf(data.distance) + "m"));
            totalsArrows += data.totalArrows;
        }


        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        for (int i = 0; i < todaysTotalDatas.size(); i++) {
            distanceToColor.put(todaysTotalDatas.get(i).distance, colors.get(i));
        }


        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);

        data.setValueFormatter(new DefaultValueFormatter(0));
        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.setDrawCenterText(true);
        pieChart.getLegend().setEnabled(false);
        pieChart.setCenterText(String.valueOf(totalsArrows));

        pieChart.invalidate();
        return distanceToColor;
    }

    /**
     * Show to the user the arrows he/she shoot at the different distances
     */
    private void showTextInformation(View view, List<TodaysTotalData> todaysTotalDatas, HashMap<Integer, Integer> distanceToColor) {

        TableLayout rawDataTable = (TableLayout) view.findViewById(R.id.todayTotalsTextData);
        Context context = getContext();
        for (TodaysTotalData data : todaysTotalDatas) {
            TableRow tr = new TableRow(context);
            tr.setBackgroundColor(distanceToColor.get(data.distance));

            TextView distanceTextView = new TextView(context);
            TextView arrowsAmountText = new TextView(context);
            TextView lastDatetimeText = new TextView(context);
            TextView seriesAmountText = new TextView(context);

            distanceTextView.setText(String.valueOf(data.distance));
            arrowsAmountText.setText(String.valueOf(data.totalArrows));
            lastDatetimeText.setText(DatetimeHelper.TIME_FORMATTER.format(data.lastUpdate));
            seriesAmountText.setText(String.valueOf(data.seriesAmount));

            tr.addView(distanceTextView);
            tr.addView(arrowsAmountText);
            tr.addView(lastDatetimeText);
            tr.addView(seriesAmountText);
            rawDataTable.addView(tr);
        }
    }
}
