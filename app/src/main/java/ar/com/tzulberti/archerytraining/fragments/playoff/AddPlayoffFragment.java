package ar.com.tzulberti.archerytraining.fragments.playoff;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractSerieArrowsFragment;
import ar.com.tzulberti.archerytraining.helper.AppCache;
import ar.com.tzulberti.archerytraining.model.common.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.playoff.ComputerPlayOffConfiguration;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;

/**
 * Created by tzulberti on 6/2/17.
 */
public class AddPlayoffFragment extends BasePlayoffFragment {

    public static final String MIN_SCORE = "ar.com.tzulberti.archerytraining.minscore";
    public static final String MAX_SCORE = "ar.com.tzulberti.archerytraining.maxscore";


    private Map<String, EditText> inputMapping;
    private Spinner tournamentConstrainsSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        this.setObjects();

        View view = inflater.inflate(R.layout.playoff_add_new, container, false);
        MainActivity activity = (MainActivity) getActivity();

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        this.inputMapping = new HashMap<>();
        this.inputMapping.put(MIN_SCORE, (EditText) view.findViewById(R.id.min_socre));
        this.inputMapping.put(MAX_SCORE, (EditText) view.findViewById(R.id.max_socre));

        for (Map.Entry<String, EditText> info : inputMapping.entrySet()) {
            int existingValue = sharedPref.getInt(info.getKey(), -1);
            if (existingValue >= 0) {
                info.getValue().setText(String.valueOf(existingValue));
            }
        }

        this.tournamentConstrainsSpinner = (Spinner) view.findViewById(R.id.tournament_constrains);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this.getContext(),
                android.R.layout.simple_spinner_item,
                AppCache.tournamentTypes
        );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.tournamentConstrainsSpinner.setAdapter(dataAdapter);

        return view;
    }

    @Override
    public void handleClick(View v) {
        Map<String, Integer> constructorKwargs = new HashMap<>();

        MainActivity activity = (MainActivity) getActivity();

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
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
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Bundle arguments = new Bundle();
        arguments.putSerializable(AbstractSerieArrowsFragment.CONTAINER_ARGUMENT_KEY, playoff);
        arguments.putInt("creating", 1);

        ViewPlayoffSeriesFragment viewPlayoffSeriesFragment = new ViewPlayoffSeriesFragment();
        viewPlayoffSeriesFragment.setArguments(arguments);

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, viewPlayoffSeriesFragment)
                .commit();
    }

}

