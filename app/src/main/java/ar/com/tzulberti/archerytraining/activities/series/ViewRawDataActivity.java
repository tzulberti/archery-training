package ar.com.tzulberti.archerytraining.activities.series;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.activities.common.AbstractTableDataActivity;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.series.SerieData;

/**
 * Created by tzulberti on 4/21/17.
 */

public class ViewRawDataActivity extends AbstractTableDataActivity {

    private static final int MAX_VALUES_TO_SHOW = 20;

    @Override
    protected boolean shouldShowHelp() {
        return true;
    }

    @Override
    protected String helpText() {
        return this.getString(R.string.series_help);
    }

    @Override
    protected void addButtonsBeforeData(TableLayout tableLayout) {
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        trParams.span = 4;

        Button viewTodaysTotals = new Button(this);
        viewTodaysTotals.setText(this.getString(R.string.series_get_today_totals));
        viewTodaysTotals.setId(Integer.MAX_VALUE - 1);
        viewTodaysTotals.setOnClickListener(this);
        viewTodaysTotals.setLayoutParams(trParams);

        Button viewCurrentWeekTotals = new Button(this);
        viewCurrentWeekTotals.setText(this.getString(R.string.series_get_current_week_totals));
        viewCurrentWeekTotals.setId(Integer.MAX_VALUE - 2);
        viewCurrentWeekTotals.setOnClickListener(this);
        viewCurrentWeekTotals.setLayoutParams(trParams);

        Button viewCurrentMonthTotals = new Button(this);
        viewCurrentMonthTotals.setText(this.getString(R.string.series_get_current_month_totals));
        viewCurrentMonthTotals.setId(Integer.MAX_VALUE - 3);
        viewCurrentMonthTotals.setOnClickListener(this);
        viewCurrentMonthTotals.setLayoutParams(trParams);

        TableRow tr1 = new TableRow(this);
        tr1.addView(viewTodaysTotals);
        tableLayout.addView(tr1);

        TableRow tr2 = new TableRow(this);
        tr2.addView(viewCurrentWeekTotals);
        tableLayout.addView(tr2);

        TableRow tr3 = new TableRow(this);
        tr3.addView(viewCurrentMonthTotals);
        tableLayout.addView(tr3);
    }

    @Override
    protected void addButtonsAfterData(TableLayout tableLayout) {

    }

    @Override
    protected void renderRow(Serializable data, TableRow tr) {
        SerieData serieData = (SerieData) data;
        TextView distanceTextView = new TextView(this);
        TextView arrowsAmountText = new TextView(this);
        TextView datetimeText = new TextView(this);
        Button removeButton = new Button(this);

        distanceTextView.setText(String.valueOf(serieData.distance));
        arrowsAmountText.setText(String.valueOf(serieData.arrowsAmount));
        datetimeText.setText(DatetimeHelper.DATETIME_FORMATTER.format(serieData.datetime));

        removeButton.setText(getText(R.string.commonDelete));
        removeButton.setId(serieData.id);
        removeButton.setOnClickListener(this);

        tr.addView(distanceTextView);
        tr.addView(arrowsAmountText);
        tr.addView(datetimeText);
        tr.addView(removeButton);
    }

    @Override
    protected List<? extends Serializable> getData() {
        return this.serieDataDAO.getLastValues(MAX_VALUES_TO_SHOW);
    }

    @Override
    protected void addNewValue() {
        Intent intent = new Intent(this, AddSerieActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (Integer.MAX_VALUE - 3 <= id) {
            // the user selected one of the rows to view the existing data
            Intent intent = new Intent(this, ViewStatsTotalsActivity.class);

            switch (id) {
                case Integer.MAX_VALUE -1:
                    intent.putExtra(ViewStatsTotalsActivity.MIN_DATE_KEY, DatetimeHelper.getTodayZeroHours());
                    intent.putExtra(ViewStatsTotalsActivity.MAX_DATE_KEY, DatetimeHelper.getTodayLastSecond());
                    intent.putExtra(ViewStatsTotalsActivity.PERIOD_TO_GROUP_BY_KEY, SerieDataDAO.GroupByType.NONE);
                    break;
                case Integer.MAX_VALUE -2:
                    intent.putExtra(ViewStatsTotalsActivity.MIN_DATE_KEY, DatetimeHelper.getLastWeeDate());
                    intent.putExtra(ViewStatsTotalsActivity.MAX_DATE_KEY, DatetimeHelper.getTodayLastSecond());
                    intent.putExtra(ViewStatsTotalsActivity.PERIOD_TO_GROUP_BY_KEY, SerieDataDAO.GroupByType.DAILY);
                    break;
                case Integer.MAX_VALUE -3:
                    intent.putExtra(ViewStatsTotalsActivity.MIN_DATE_KEY, DatetimeHelper.getFirstDateOfMonth());
                    intent.putExtra(ViewStatsTotalsActivity.MAX_DATE_KEY, DatetimeHelper.getLastDateOfMonth());
                    intent.putExtra(ViewStatsTotalsActivity.PERIOD_TO_GROUP_BY_KEY, SerieDataDAO.GroupByType.DAILY);
                    break;
                default:
                    throw new RuntimeException("Missing handler for " + id);
            }
            startActivity(intent);

        } else {
            // the user selected one of the rows to delete the existing data
            this.serieDataDAO.deleteSerieId(v.getId());
            TableRow tr = (TableRow) v.getParent();
            TableLayout tableLayout = (TableLayout) tr.getParent();
            tableLayout.removeView(tr);
        }
    }

    /**
     * Since there is no details to show when a rows is clicked then don't make them clicable
     * @return
     */
    protected boolean areRowsClickable() {
        return false;
    }

}
