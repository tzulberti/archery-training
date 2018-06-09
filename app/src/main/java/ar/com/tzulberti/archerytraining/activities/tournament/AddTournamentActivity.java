package ar.com.tzulberti.archerytraining.activities.tournament;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.commons.lang3.StringUtils;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.AbstractSerieArrowsActivity;
import ar.com.tzulberti.archerytraining.activities.common.AbstractTableDataActivity;
import ar.com.tzulberti.archerytraining.activities.common.BaseArcheryTrainingActivity;
import ar.com.tzulberti.archerytraining.helper.AppCache;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;

/**
 * Used to create a tournament based on the user information
 * Created by tzulberti on 5/17/17.
 */

public class AddTournamentActivity extends BaseArcheryTrainingActivity {


    private static final int[] REQUIRED_VALUES = {
            R.id.name,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tournament_add_new);

        Spinner tournamentConstrainsSpinner = (Spinner) this.findViewById(R.id.tournament_constrains);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                AppCache.tournamentTypes
        );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tournamentConstrainsSpinner.setAdapter(dataAdapter);

    }


    public void createTournament(View clickButton) {
        // validate the values to make sure everything is ok
        for (int elementId : REQUIRED_VALUES) {
            EditText element = (EditText) this.findViewById(elementId);
            String value = element.getText().toString();

            if (StringUtils.isBlank(value)) {
                element.setError(getText(R.string.commonRequiredValidationError));
                return ;
            }
        }

        Spinner tournamentTypes = (Spinner) this.findViewById(R.id.tournament_constrains);
        TournamentConstraint tournamentConstraint = AppCache.tournamentConstraintSpinnerMap.get(tournamentTypes.getSelectedItem());

        Tournament tournament = this.tournamentDAO.createTournament(
                ((EditText) this.findViewById(R.id.name)).getText().toString(),
                ((CheckBox) this.findViewById(R.id.is_tournament)).isChecked(),
                tournamentConstraint
        );


        this.hideKeyboard();

        Intent intent = new Intent(this, ViewTournamentSeriesActivity.class);
        intent.putExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY, tournament);
        intent.putExtra(AbstractTableDataActivity.CREATING_INTENT_KEY, 1);

        startActivity(intent);


    }
}
