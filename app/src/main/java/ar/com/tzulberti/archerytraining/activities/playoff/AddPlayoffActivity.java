package ar.com.tzulberti.archerytraining.activities.playoff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.AbstractSerieArrowsActivity;
import ar.com.tzulberti.archerytraining.activities.common.AbstractTableDataActivity;
import ar.com.tzulberti.archerytraining.activities.common.BaseArcheryTrainingActivity;
import ar.com.tzulberti.archerytraining.helper.AppCache;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.playoff.ComputerPlayOffConfiguration;
import ar.com.tzulberti.archerytraining.model.playoff.HumanPlayoffConfiguration;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;

/**
 * Used to create a new playoff
 *
 * Created by tzulberti on 6/2/17.
 */
public class AddPlayoffActivity extends BaseArcheryTrainingActivity {

    public static final String MIN_SCORE = "ar.com.tzulberti.archerytraining.minscore";
    public static final String MAX_SCORE = "ar.com.tzulberti.archerytraining.maxscore";

    private static final int COMPUTER_PLAYOFF_TYPE = 0;

    private Map<String, EditText> inputMapping;
    private Spinner tournamentConstrainsSpinner;
    private Spinner playoffTypeSpinner;

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
            if (sharedPref.contains(info.getKey())) {
                int existingValue = sharedPref.getInt(info.getKey(), 0);
                if (existingValue != 0) {
                    info.getValue().setText(String.valueOf(existingValue));
                }
            }
        }

        this.tournamentConstrainsSpinner = (Spinner) this.findViewById(R.id.tournament_constrains);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                AppCache.tournamentTypes
        );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.tournamentConstrainsSpinner.setAdapter(dataAdapter);


        this.playoffTypeSpinner = (Spinner) this.findViewById(R.id.opponent_type);
        ArrayAdapter<String> opponentTypeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[] {
                    this.getResources().getString(R.string.playoff_opponent_type_computer),
                    this.getResources().getString(R.string.playoff_opponent_type_human),
                }
        );
        opponentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.playoffTypeSpinner.setAdapter(opponentTypeAdapter);
        this.playoffTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AddPlayoffActivity.this.findViewById(R.id.opponent_name_row).setVisibility(View.GONE);
                AddPlayoffActivity.this.findViewById(R.id.min_score_row).setVisibility(View.GONE);
                AddPlayoffActivity.this.findViewById(R.id.max_score_row).setVisibility(View.GONE);

                if (position == COMPUTER_PLAYOFF_TYPE) {
                    AddPlayoffActivity.this.findViewById(R.id.min_score_row).setVisibility(View.VISIBLE);
                    AddPlayoffActivity.this.findViewById(R.id.max_score_row).setVisibility(View.VISIBLE);
                } else {
                    AddPlayoffActivity.this.findViewById(R.id.opponent_name_row).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    public void addPlayoff(View v) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Bundle bundle = new Bundle();

        boolean foundError = false;
        String requiredValueError = getResources().getString(R.string.commonRequiredValidationError);
        int playoffType = this.playoffTypeSpinner.getSelectedItemPosition();
        HumanPlayoffConfiguration humanPlayoffConfiguration = null;
        ComputerPlayOffConfiguration computerPlayOffConfiguration = null;
        String playoffName = null;

        if (playoffType == COMPUTER_PLAYOFF_TYPE) {
            playoffName = this.getString(R.string.playoff_computer_name);

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
                bundle.putInt(info.getKey(), value);
            }

            if (bundle.getInt(MAX_SCORE) < bundle.getInt(MIN_SCORE)) {
                inputMapping.get(MIN_SCORE).setError(this.getString(R.string.playoff_error_creating_min_greater_max));
                foundError = true;
            }

            computerPlayOffConfiguration = new ComputerPlayOffConfiguration();
            computerPlayOffConfiguration.maxScore = bundle.getInt(MAX_SCORE);
            computerPlayOffConfiguration.minScore = bundle.getInt(MIN_SCORE);


        } else {
            EditText opponentNameEditText = (EditText) this.findViewById(R.id.opponent_name);
            String opponentName = opponentNameEditText.getText().toString();
            if (opponentName.isEmpty()) {
                opponentNameEditText.setError(requiredValueError);
                foundError = true;
            }
            humanPlayoffConfiguration = new HumanPlayoffConfiguration();
            humanPlayoffConfiguration.opponentName = opponentName;

            playoffName = opponentName;
        }


        if (foundError) {
            editor.clear();
            return;
        }

        editor.apply();


        // Create the playoff
        TournamentConstraint tournamentConstraint = AppCache.tournamentConstraintSpinnerMap.get(this.tournamentConstrainsSpinner.getSelectedItem());
        Playoff playoff = this.playoffDAO.createPlayoff(
                playoffName,
                computerPlayOffConfiguration,
                tournamentConstraint,
                humanPlayoffConfiguration
                );

        // Hide the keyboard when starting a new playoff
        this.hideKeyboard();

        Intent intent = new Intent(this, ViewPlayoffSeriesActivity.class);
        intent.putExtra(AbstractTableDataActivity.CREATING_INTENT_KEY, 1);
        intent.putExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY, playoff);
        startActivity(intent);
    }

}

