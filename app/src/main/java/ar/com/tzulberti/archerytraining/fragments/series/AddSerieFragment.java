package ar.com.tzulberti.archerytraining.fragments.series;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.SerieData;

/**
 * Created by tzulberti on 4/20/17.
 */

public class AddSerieFragment extends BaseSeriesFragment {

    private static final int MAX_VALUES_TO_SHOW = 3;
    private EditText distanceText;
    private EditText arrowAmountText;
    private TextView lastDistance;
    private TextView lastArrowsAmount;
    private TextView lastDatetime;
    private TextView todaysTotals;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        View view = inflater.inflate(R.layout.add_serie, container, false);
        this.setObjects();

        this.distanceText = (EditText) view.findViewById(R.id.distance);
        this.arrowAmountText = (EditText) view.findViewById(R.id.arrowsAmount);

        this.lastDistance = (TextView) view.findViewById(R.id.lastDistance);
        this.lastArrowsAmount = (TextView) view.findViewById(R.id.lastArrowsAmount);
        this.lastDatetime = (TextView) view.findViewById(R.id.lastDatetime);
        this.todaysTotals = (TextView) view.findViewById(R.id.todayTotals);

        this.showLastSerie();
        this.showTodayArrows();

        return view;
    }


    /**
     * When the user select the option to add, so it will add the new serie to the
     * database
     *
     * @param view
     */
    @Override
    public void handleClick(View v) {
        CharSequence distanceValue = this.distanceText.getText().toString();
        if (StringUtils.isBlank(distanceValue)) {
            this.distanceText.setError(getText(R.string.commonRequiredValidationError));
            return;
        }

        CharSequence arrowsAmount = this.arrowAmountText.getText().toString();
        if (StringUtils.isBlank(arrowsAmount)) {
            this.arrowAmountText.setError(getText(R.string.commonRequiredValidationError));
            return;
        }

        this.serieDataDAO.addSerieData(
                Integer.valueOf(distanceValue.toString()),
                Integer.valueOf(arrowsAmount.toString())
        );

        // reset the input to notify the user of a change and hide the keyboard because
        // if not the snackbar isn't shown
        this.arrowAmountText.setText("");
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Snackbar.make(
                this.getView(),
                this.getString(R.string.addSerieAdded, Integer.valueOf(arrowsAmount.toString())),
                Snackbar.LENGTH_SHORT
        )
        .setAction("Action", null).show();

        this.showLastSerie();
        this.showTodayArrows();
    }

    private void showTodayArrows() {
        long todaysTotals = this.serieDataDAO.getTodayArrows();
        this.todaysTotals.setText(String.valueOf(todaysTotals));
    }


    private void showLastSerie() {
        List<SerieData> data = this.serieDataDAO.getLastValues(1);
        if (data == null || data.size() == 0) {
            this.lastDistance.setText("-");
            this.lastArrowsAmount.setText("-");
            this.lastDatetime.setText("-");

        } else {
            SerieData lastSerie = data.get(0);
            this.lastDistance.setText(String.valueOf(lastSerie.distance));
            this.lastArrowsAmount.setText(String.valueOf(lastSerie.arrowsAmount));
            this.lastDatetime.setText(DatetimeHelper.DATETIME_FORMATTER.format(lastSerie.datetime));
        }
    }
}
