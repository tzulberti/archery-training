package ar.com.tzulberti.archerytraining.fragments.playoff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractSerieArrowsActivity;
import ar.com.tzulberti.archerytraining.fragments.common.BaseArcheryTrainingActivity;
import ar.com.tzulberti.archerytraining.helper.AppCache;
import ar.com.tzulberti.archerytraining.model.common.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.playoff.ComputerPlayOffConfiguration;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;

/**
 * Created by tzulberti on 6/2/17.
 */
public class AddPlayoffActivity extends BaseArcheryTrainingActivity {

    public static final String MIN_SCORE = "ar.com.tzulberti.archerytraining.minscore";
    public static final String MAX_SCORE = "ar.com.tzulberti.archerytraining.maxscore";


    private Map<String, EditText> inputMapping;
    private Spinner tournamentConstrainsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.createDAOs();
        setContentView(R.layout.playoff_add_new);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        this.inputMapping = new HashMap<>();
        this.inputMapping.put(MIN_SCORE, (EditText) this.findViewById(R.id.min_socre));
        this.inputMapping.put(MAX_SCORE, (EditText) this.findViewById(R.id.max_socre));

        for (Map.Entry<String, EditText> info : inputMapping.entrySet()) {
            int existingValue = sharedPref.getInt(info.getKey(), -1);
            if (existingValue >= 0) {
                info.getValue().setText(String.valueOf(existingValue));
            }
        }

        this.tournamentConstrainsSpinner = (Spinner) this.findViewById(R.id.tournament_constrains);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                AppCache.tournamentTypes
        );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.tournamentConstrainsSpinner.setAdapter(dataAdapter);

    }



    public void addPlayoff(View v) {
        Map<String, Integer> constructorKwargs = new HashMap<>();


        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Bundle bundle = new Bundle();

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

            if (info.getKey().equals(MIN_SCORE) || info.getKey().equals(MAX_SCORE)) {
                if (value > 30) {
                    info.getValue().setError(this.getString(R.string.playoff_error_creating_max_value_30));
                    foundError = true;
                    continue;
                }
            }

            editor.putInt(info.getKey(), value);
            constructorKwargs.put(info.getKey(), value);
            bundle.putInt(info.getKey(), value);
        }

        if (bundle.getInt(MAX_SCORE) < bundle.getInt(MIN_SCORE)) {
            inputMapping.get(MIN_SCORE).setError(this.getString(R.string.playoff_error_creating_min_greater_max));
            foundError = true;
        }

        if (foundError) {
            editor.clear();
            return;
        }

        editor.commit();

        ComputerPlayOffConfiguration computerPlayOffConfiguration = new ComputerPlayOffConfiguration();
        computerPlayOffConfiguration.maxScore = bundle.getInt(MAX_SCORE);
        computerPlayOffConfiguration.minScore = bundle.getInt(MIN_SCORE);

        // Create the playoff
        TournamentConstraint tournamentConstraint = AppCache.tournamentConstraintSpinnerMap.get(this.tournamentConstrainsSpinner.getSelectedItem());
        Playoff playoff = this.playoffDAO.createPlayoff(
                this.getString(R.string.playoff_computer_name),
                computerPlayOffConfiguration,
                tournamentConstraint
                );


        // Hide the keyboard when starting a new playoff
        this.hideKeyboard();

        Intent intent = new Intent(this, ViewPlayoffSeriesActivity.class);
        intent.putExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY, playoff);
        startActivity(intent);
    }

}

