package ar.com.tzulberti.archerytraining.activities.retentions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.BaseArcheryTrainingActivity;

/**
 * Created by tzulberti on 4/24/17.
 */

public class ConfigureRetentionActivity extends BaseArcheryTrainingActivity {

    public static final String SERIES_AMOUNT = "ar.com.tzulberti.archerytraining.seriesAmount";
    public static final String SERIES_SLEEP_TIME = "ar.com.tzulberti.archerytraining.seriesSleepTime";
    public static final String REPETITIONS_AMOUNT = "ar.com.tzulberti.archerytraining.repetitionsAmount";
    public static final String REPETITIONS_DURATION = "ar.com.tzulberti.archerytraining.repetitionsDuration";
    public static final String START_IN = "ar.com.tzulberti.archerytraining.startIn";


    private EditText seriesAmountEditText;
    private EditText seriesSleepTimeEditText;
    private EditText repetitionsAmountEditText;
    private EditText repetitionsDurationEditText;
    private EditText startInEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retention_configure);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        int seriesAmount = sharedPref.getInt(SERIES_AMOUNT, -1);
        int seriesSleepTime = sharedPref.getInt(SERIES_SLEEP_TIME, -1);
        int repetitionsAmount = sharedPref.getInt(REPETITIONS_AMOUNT, -1);
        int repetitionsDuration = sharedPref.getInt(REPETITIONS_DURATION, -1);
        int startIn = sharedPref.getInt(START_IN, -1);

        this.seriesAmountEditText = (EditText) this.findViewById(R.id.seriesAmount);
        this.seriesSleepTimeEditText = (EditText) this.findViewById(R.id.seriesSleepTime);
        this.repetitionsAmountEditText = (EditText) this.findViewById(R.id.repetitionsAmount);
        this.repetitionsDurationEditText = (EditText) this.findViewById(R.id.repetitionsDuration);
        this.startInEditText = (EditText) this.findViewById(R.id.startIn);


        // TODO maybe create a map with this values to prevent doing copy&paste?
        if (seriesAmount >= 0) {
            this.seriesAmountEditText.setText(Integer.toString(seriesAmount));
        }

        if (seriesSleepTime >= 0) {
            this.seriesSleepTimeEditText.setText(Integer.toString(seriesSleepTime));
        }

        if (repetitionsAmount >= 0) {
            this.repetitionsAmountEditText.setText(Integer.toString(repetitionsAmount));
        }

        if (repetitionsDuration >= 0) {
            this.repetitionsDurationEditText.setText(Integer.toString(repetitionsDuration));
        }

        if (startIn >= 0) {
            this.startInEditText.setText(Integer.toString(startIn));
        }

    }

    public void startRetentions(View v) {
        Map<String, EditText> inputMapping = new HashMap<>();
        inputMapping.put(SERIES_AMOUNT, this.seriesAmountEditText);
        inputMapping.put(REPETITIONS_AMOUNT, this.repetitionsAmountEditText);
        inputMapping.put(SERIES_SLEEP_TIME, this.seriesSleepTimeEditText);
        inputMapping.put(REPETITIONS_DURATION, this.repetitionsDurationEditText);
        inputMapping.put(START_IN, this.startInEditText);


        Intent intent = new Intent(this, RetentionExerciseActivity.class);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        boolean foundError = false;
        String requiredValueError = getResources().getString(R.string.commonRequiredValidationError);
        for (Map.Entry<String, EditText> info : inputMapping.entrySet()) {
            String inputValue = info.getValue().getText().toString();
            if (StringUtils.isBlank(inputValue)) {
                info.getValue().setError(requiredValueError);
                foundError = true;
                continue;
            }

            int value = Integer.parseInt(inputValue);
            if (value <= 0) {
                info.getValue().setError(requiredValueError);
                foundError = true;
                continue;
            }

            editor.putInt(info.getKey(), value);
            intent.putExtra(info.getKey(), value);
        }

        if (foundError) {
            editor.clear();
            return;
        }

        editor.commit();
        startActivity(intent);
    }
}
