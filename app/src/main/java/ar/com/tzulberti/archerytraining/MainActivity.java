package ar.com.tzulberti.archerytraining;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.helpers.DatabaseHelper;
import ar.com.tzulberti.archerytraining.helpers.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.SerieData;
import ar.com.tzulberti.archerytraining.model.TodaysTotalData;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SerieDataDAO serieDataDAO;

    private EditText distanceText;
    private EditText arrowAmountText;

    private TextView lastDistance;
    private TextView lastArrowsAmount;
    private TextView lastDatetime;
    private TextView todaysTotals;



    private static final int MAX_VALUES_TO_SHOW = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.databaseHelper = new DatabaseHelper(this);

        this.serieDataDAO = new SerieDataDAO(this.databaseHelper);
        this.distanceText = (EditText) this.findViewById(R.id.distance);
        this.arrowAmountText = (EditText) this.findViewById(R.id.arrowsAmount);


        this.lastDistance = (TextView) this.findViewById(R.id.lastDistance);
        this.lastArrowsAmount = (TextView) this.findViewById(R.id.lastArrowsAmount);
        this.lastDatetime = (TextView) this.findViewById(R.id.lastDatetime);
        this.todaysTotals = (TextView) this.findViewById(R.id.todayTotals);

        //this.showExistingData();
        this.showLastSerie();
        this.showTodayArrows();
    }

    /**
     * Called when the user clicks on the button to add the current serie
     * to the database
     *
     * @param view
     */
    public void start(View view) {

        CharSequence distanceValue = this.distanceText.getText().toString();
        if (StringUtils.isBlank(distanceValue)) {
            // TODO put a real error message
            this.distanceText.setError("");
            return ;
        };

        CharSequence arrowsAmount = this.arrowAmountText.getText().toString();
        if (StringUtils.isBlank(arrowsAmount)) {
            this.arrowAmountText.setError("");
            return ;
        }

        System.err.println("Llego hasta aca");
        this.serieDataDAO.addSerieData(
            Integer.valueOf(distanceValue.toString()),
            Integer.valueOf(arrowsAmount.toString())
        );

        // reset the input to notify the user of a change
        this.arrowAmountText.setText("");

        this.showLastSerie();
        this.showTodayArrows();
        //this.showExistingData();
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
            this.lastDatetime.setText(DatetimeHelper.timestampFormatter.format(lastSerie.datetime));
        }
    }

    /**
    private void showExistingData() {
        List<TodaysTotalData> data = this.serieDataDAO.getTodaysTotal();
        if (data == null || data.size() == 0) {
            return ;
        }

        for (TodaysTotalData totalData : data) {
            this.existingData.setText(
                String.format("%s - %s - %s", totalData.distance, DatetimeHelper.timeFormatter.format(totalData.lastUpdate), totalData.totalArrows)
            );
        }
    }
    */
}
