package ar.com.tzulberti.archerytraining.fragments.retentions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;

/**
 * Created by tzulberti on 4/24/17.
 */

public class ConfigureRetention extends BaseClickableFragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        View view = inflater.inflate(R.layout.configure_retention, container, false);
        MainActivity activity = (MainActivity) getActivity();

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        int seriesAmount = sharedPref.getInt(SERIES_AMOUNT, -1);
        int seriesSleepTime = sharedPref.getInt(SERIES_SLEEP_TIME, -1);
        int repetitionsAmount = sharedPref.getInt(REPETITIONS_AMOUNT, -1);
        int repetitionsDuration = sharedPref.getInt(REPETITIONS_DURATION, -1);
        int startIn = sharedPref.getInt(START_IN, -1);

        this.seriesAmountEditText = (EditText) view.findViewById(R.id.seriesAmount);
        this.seriesSleepTimeEditText = (EditText) view.findViewById(R.id.seriesSleepTime);
        this.repetitionsAmountEditText = (EditText) view.findViewById(R.id.repetitionsAmount);
        this.repetitionsDurationEditText = (EditText) view.findViewById(R.id.repetitionsDuration);
        this.startInEditText = (EditText) view.findViewById(R.id.startIn);


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

        return view;
    }

    @Override
    public void handleClick(View v) {
        Map<String, EditText> inputMapping = new HashMap<>();
        Bundle bundle = new Bundle();
        inputMapping.put(SERIES_AMOUNT, this.seriesAmountEditText);
        inputMapping.put(REPETITIONS_AMOUNT, this.repetitionsAmountEditText);
        inputMapping.put(SERIES_SLEEP_TIME, this.seriesSleepTimeEditText);
        inputMapping.put(REPETITIONS_DURATION, this.repetitionsDurationEditText);
        inputMapping.put(START_IN, this.startInEditText);

        Map<String, Integer> constructorKwargs = new HashMap<>();

        MainActivity activity = (MainActivity) getActivity();

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
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
            bundle.putInt(info.getKey(), value);
        }

        if (foundError) {
            editor.clear();
            return;
        }

        editor.commit();
        RetentionExercise retentionExercise = new RetentionExercise();
        retentionExercise.setArguments(bundle);

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, retentionExercise)
                .commit();
    }
}
