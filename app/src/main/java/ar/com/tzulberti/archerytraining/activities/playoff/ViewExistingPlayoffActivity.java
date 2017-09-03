package ar.com.tzulberti.archerytraining.activities.playoff;

import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.AbstractSerieArrowsActivity;
import ar.com.tzulberti.archerytraining.activities.common.AbstractTableDataActivity;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;

/**
 * List all the existing playoff
 *
 * Created by tzulberti on 6/4/17.
 */

public class ViewExistingPlayoffActivity extends AbstractTableDataActivity {

    @Override
    protected boolean shouldShowHelp() {
        return true;
    }

    @Override
    protected String helpText() {
        return this.getString(R.string.playoff_existing_help);
    }

    @Override
    protected void addButtonsBeforeData(TableLayout tableLayout) {

        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        Button viewPlayoffStatsButton = new Button(this);
        viewPlayoffStatsButton.setText(this.getString(R.string.stats_view));
        viewPlayoffStatsButton.setId(Integer.MAX_VALUE - 1);
        viewPlayoffStatsButton.setOnClickListener(this);
        viewPlayoffStatsButton.setLayoutParams(trParams);

        TableRow tr1 = new TableRow(this);
        tr1.addView(viewPlayoffStatsButton);
        tableLayout.addView(tr1);
    }

    @Override
    protected void addButtonsAfterData(TableLayout tableLayout) {

    }

    @Override
    protected boolean renderDataUsingRows() { return false; }

    @Override
    protected void renderInformation(Serializable data, TableLayout mainTableLayout) {
        Playoff playoff = (Playoff) data;

        TableLayout tableLayout = (TableLayout) View.inflate(
                this,
                R.layout.playoff_playoff_information,
                null
        );

        ImageView imageView = (ImageView) tableLayout.findViewById(R.id.playoff_type);
        if (playoff.computerPlayOffConfiguration == null) {
            imageView.setImageResource(R.drawable.ic_standing_man);
        } else {
            imageView.setImageResource(R.drawable.ic_computer);
        }

        ((TextView) tableLayout.findViewById(R.id.opponent_name)).setText(playoff.name);
        ((TextView) tableLayout.findViewById(R.id.datetime)).setText(DatetimeHelper.DATE_FORMATTER.format(playoff.datetime));
        ((TextView) tableLayout.findViewById(R.id.tournament_constraint)).setText(playoff.getTournamentConstraint().translatedName);
        if (playoff.computerPlayOffConfiguration != null) {
            ((TextView) tableLayout.findViewById(R.id.score_configuration)).setText(String.valueOf(playoff.computerPlayOffConfiguration.minScore) + " - " + String.valueOf(playoff.computerPlayOffConfiguration.maxScore));
        }

        TextView totalScoreText = ((TextView) tableLayout.findViewById(R.id.total_score));
        totalScoreText.setText(String.valueOf(playoff.userPlayoffScore) + " - " + String.valueOf(playoff.opponentPlayoffScore));
        if (playoff.userPlayoffScore > playoff.opponentPlayoffScore) {
            totalScoreText.setTextColor(this.getResources().getColor(R.color.colorGreen));
        } else if (playoff.userPlayoffScore < playoff.opponentPlayoffScore) {
            totalScoreText.setTextColor(this.getResources().getColor(R.color.colorRed));
        }

        tableLayout.setId((int) playoff.id);
        tableLayout.setOnClickListener(this);
        mainTableLayout.addView(tableLayout);
    }



    @Override
    protected List<? extends Serializable> getData() {
        return this.playoffDAO.getPlayoffs();
    }

    @Override
    protected void addNewValue() {
        Intent intent = new Intent(this, AddPlayoffActivity.class);
        startActivity(intent);
    }



    @Override
    public void onClick(View v) {
        int playoffId = v.getId();

        Intent intent;
        if (playoffId == (Integer.MAX_VALUE - 1)) {
            // the user selected to view the playoff stats
            intent = new Intent(this, ViewPlayoffStatsActivity.class);
        } else {

            // the user selected to view one playoff
            Playoff playoff = this.playoffDAO.getCompletePlayoffData(playoffId);

            intent = new Intent(this, ViewPlayoffSeriesActivity.class);
            intent.putExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY, playoff);
        }

        startActivity(intent);
    }
}
