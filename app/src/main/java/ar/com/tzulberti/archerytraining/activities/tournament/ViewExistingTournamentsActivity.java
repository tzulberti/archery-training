package ar.com.tzulberti.archerytraining.activities.tournament;

import android.content.Intent;


import android.os.Bundle;
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
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;

/**
 * View used to list all the existing tournaments
 *
 * Created by tzulberti on 5/17/17.
 */
public class ViewExistingTournamentsActivity extends AbstractTableDataActivity {


    @Override
    protected void addButtonsBeforeData(TableLayout tableLayout) {
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        trParams.span = 4;

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

    protected boolean renderDataUsingRows() { return false; }

    @Override
    protected void renderInformation(Serializable data, TableLayout mainTableLayout) {

        TableLayout tableLayout = (TableLayout) View.inflate(
                this,
                R.layout.tournament_tournament_information,
                null
        );

        Tournament tournament = (Tournament) data;


        ((TextView) tableLayout.findViewById(R.id.tournament_name)).setText(tournament.name);
        ((TextView) tableLayout.findViewById(R.id.datetime)).setText(DatetimeHelper.DATE_FORMATTER.format(tournament.datetime));
        ((TextView) tableLayout.findViewById(R.id.total_tournament_score)).setText(String.valueOf(tournament.totalScore) + "/" + tournament.getTournamentConstraint().getMaxPossibleScore());
        ((TextView) tableLayout.findViewById(R.id.tournament_constraint)).setText(tournament.getTournamentConstraint().translatedName);

        ImageView imageView = (ImageView) tableLayout.findViewById(R.id.tournament_type);
        if (tournament.isTournament) {
            imageView.setImageResource(R.drawable.ic_trophy);
        } else {
            imageView.setImageResource(R.drawable.ic_bow);
        }

        tableLayout.setId((int) tournament.id);
        tableLayout.setOnClickListener(this);
        mainTableLayout.addView(tableLayout);
    }

    @Override
    protected List<? extends Serializable> getData() {
        return this.tournamentDAO.getExistingTournaments();
    }

    @Override
    protected void addNewValue() {
        Intent intent = new Intent(this, AddTournamentActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        int tournamentId = v.getId();

        Intent intent;
        if (tournamentId == (Integer.MAX_VALUE - 1)) {
            // the user selected to view the playoff stats
            intent = new Intent(this, ViewTournamentsStatsActivity.class);

        } else {
            Tournament tournament = this.tournamentDAO.getTournamentInformation(tournamentId);
            this.tournamentDAO.getTournamentSeriesInformation(tournament);


            intent = new Intent(this, ViewTournamentSeriesActivity.class);
            intent.putExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY, tournament);
        }

        startActivity(intent);
    }
}
