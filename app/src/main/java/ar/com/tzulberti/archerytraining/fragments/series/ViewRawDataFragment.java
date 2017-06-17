package ar.com.tzulberti.archerytraining.fragments.series;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractTableDataFragment;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.SerieData;

/**
 * Created by tzulberti on 4/21/17.
 */

public class ViewRawDataFragment extends AbstractTableDataFragment {

    private static final int MAX_VALUES_TO_SHOW = 20;

    private SerieDataDAO serieDataDAO;

    @Override
    protected void setViewObjects() {
        MainActivity activity = (MainActivity) getActivity();
        this.serieDataDAO = activity.getSerieDAO();
    }

    @Override
    protected void addButtonsBeforeData(TableLayout tableLayout, Context context) {
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        trParams.span = 4;

        Button viewTodaysTotals = new Button(context);
        viewTodaysTotals.setText(this.getString(R.string.series_get_today_totals));
        viewTodaysTotals.setId(Integer.MAX_VALUE - 1);
        viewTodaysTotals.setOnClickListener(this);
        viewTodaysTotals.setLayoutParams(trParams);

        Button viewCurrentWeekTotals = new Button(context);
        viewCurrentWeekTotals.setText(this.getString(R.string.series_get_current_week_totals));
        viewCurrentWeekTotals.setId(Integer.MAX_VALUE - 2);
        viewCurrentWeekTotals.setOnClickListener(this);
        viewCurrentWeekTotals.setLayoutParams(trParams);

        Button viewCurrentMonthTotals = new Button(context);
        viewCurrentMonthTotals.setText(this.getString(R.string.series_get_current_month_totals));
        viewCurrentMonthTotals.setId(Integer.MAX_VALUE - 3);
        viewCurrentMonthTotals.setOnClickListener(this);
        viewCurrentMonthTotals.setLayoutParams(trParams);

        TableRow tr1 = new TableRow(context);
        tr1.addView(viewTodaysTotals);
        tableLayout.addView(tr1);

        TableRow tr2 = new TableRow(context);
        tr2.addView(viewCurrentWeekTotals);
        tableLayout.addView(tr2);

        TableRow tr3 = new TableRow(context);
        tr3.addView(viewCurrentMonthTotals);
        tableLayout.addView(tr3);
    }

    @Override
    protected void addButtonsAfterData(TableLayout tableLayout, Context context) {

    }

    @Override
    protected void renderRow(Serializable data, TableRow tr, Context context) {
        SerieData serieData = (SerieData) data;
        TextView distanceTextView = new TextView(context);
        TextView arrowsAmountText = new TextView(context);
        TextView datetimeText = new TextView(context);
        Button removeButton = new Button(context);

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
        AddSerieFragment addSerieFragment = new AddSerieFragment();
        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .replace(R.id.container, addSerieFragment);

        fragmentTransaction.commit();
    }


    @Override
    public void handleClick(View v) {
        int id = v.getId();
        if (Integer.MAX_VALUE - 3 <= id) {
            // the user selected one of the rows to view the existing data
            ViewStatsTotalsFragment fragment = new ViewStatsTotalsFragment();
            Bundle arguments = new Bundle();
            switch (id) {
                case Integer.MAX_VALUE -1:
                    arguments.putLong(ViewStatsTotalsFragment.MIN_DATE_KEY, DatetimeHelper.getTodayZeroHours());
                    arguments.putLong(ViewStatsTotalsFragment.MAX_DATE_KEY, DatetimeHelper.getTomorrowZeroHours());
                    arguments.putSerializable(ViewStatsTotalsFragment.PERIOD_TO_GROUP_BY_KEY, SerieDataDAO.GroupByType.HOURLY);
                    break;
                case Integer.MAX_VALUE -2:
                    arguments.putLong(ViewStatsTotalsFragment.MIN_DATE_KEY, DatetimeHelper.getLastWeeDate());
                    arguments.putLong(ViewStatsTotalsFragment.MAX_DATE_KEY, DatetimeHelper.getTomorrowZeroHours());
                    arguments.putSerializable(ViewStatsTotalsFragment.PERIOD_TO_GROUP_BY_KEY, SerieDataDAO.GroupByType.DAILY);
                    break;
                case Integer.MAX_VALUE -3:
                    arguments.putLong(ViewStatsTotalsFragment.MIN_DATE_KEY, DatetimeHelper.getFirstDateOfMonth());
                    arguments.putLong(ViewStatsTotalsFragment.MAX_DATE_KEY, DatetimeHelper.getLastDateOfMonth());
                    arguments.putSerializable(ViewStatsTotalsFragment.PERIOD_TO_GROUP_BY_KEY, SerieDataDAO.GroupByType.DAILY);
                    break;
                default:
                    throw new RuntimeException("Missing handler for %s");
            }

            fragment.setArguments(arguments);

            FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment);

            fragmentTransaction.commit();

        } else {
            // the user selected one of the rows to delete the existing data
            this.serieDataDAO.deleteSerieId(v.getId());
            TableRow tr = (TableRow) v.getParent();
            TableLayout tableLayout = (TableLayout) tr.getParent();
            tableLayout.removeView(tr);
        }
    }

}
