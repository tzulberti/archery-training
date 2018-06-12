package ar.com.tzulberti.archerytraining.activities.series;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import ar.com.tzulberti.archerytraining.R;

import ar.com.tzulberti.archerytraining.activities.common.BaseArcheryTrainingActivity;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.series.SerieData;

/**
 * Created by tzulberti on 4/20/17.
 */

public class AddSerieActivity extends BaseArcheryTrainingActivity {

    private static final int MAX_VALUES_TO_SHOW = 3;
    private EditText distanceText;
    private EditText arrowAmountText;
    private TextView lastDistance;
    private TextView lastArrowsAmount;
    private TextView lastDatetime;
    private TextView todaysTotals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.series_add);

        this.distanceText = (EditText) this.findViewById(R.id.distance);
        this.arrowAmountText = (EditText) this.findViewById(R.id.arrowsAmount);

        this.lastDistance = (TextView) this.findViewById(R.id.lastDistance);
        this.lastArrowsAmount = (TextView) this.findViewById(R.id.lastArrowsAmount);
        this.lastDatetime = (TextView) this.findViewById(R.id.lastDatetime);
        this.todaysTotals = (TextView) this.findViewById(R.id.todayTotals);

        final Button addButton = (Button) this.findViewById(R.id.addNewSerie);

        arrowAmountText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addButton.performClick();
                    return true;
                }
                return false;
            }
        });

        this.showLastSerie();
        this.showTodayArrows();

    }


    /**
     * When the user select the option to add, so it will add the new serie to the
     * database
     *
     */
    public void addNewSerie(View v) {
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
                Integer.valueOf(arrowsAmount.toString()),
                SerieData.TrainingType.FREE
        );

        // reset the input to notify the user of a change and hide the keyboard because
        // if not the snackbar isn't shown
        this.arrowAmountText.setText("");
        this.hideKeyboard();

        Snackbar.make(
                this.findViewById(R.id.serie_add_table_layout),
                this.getString(R.string.addSerieAdded, Integer.valueOf(arrowsAmount.toString())),
                Snackbar.LENGTH_SHORT
        )
        .setAction("Action", null).show();

        this.showLastSerie();
        this.showTodayArrows();
    }

    private void showTodayArrows() {
        long todaysTotals = this.serieDataDAO.getTotalArrowsForDate(
                DatetimeHelper.getTodayZeroHours(),
                DatetimeHelper.getTomorrowZeroHours()
        );
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
