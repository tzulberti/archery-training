package ar.com.tzulberti.archerytraining.activities.tournament;

import android.content.Intent;

import android.util.Log;
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

    @Override
    protected void renderRow(Serializable data, TableRow tr) {
        Tournament tournament = (Tournament) data;
        ImageView imageView = new ImageView(this);
        TextView nameText = new TextView(this);
        TextView datetimeText = new TextView(this);
        TextView totalScoreText = new TextView(this);


        if (tournament.isTournament) {
            imageView.setImageResource(R.drawable.ic_trophy);
        } else {
            imageView.setImageResource(R.drawable.ic_bow);
        }
        nameText.setText(tournament.name);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);


        totalScoreText.setText(String.valueOf(tournament.totalScore) + "/" + tournament.getMaxPossibleScore());
        totalScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        datetimeText.setText(DatetimeHelper.DATE_FORMATTER.format(tournament.datetime));


        tr.addView(imageView);
        tr.addView(nameText);
        tr.addView(totalScoreText);
        tr.addView(datetimeText);
        tr.setId((int) tournament.id);
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

        Intent intent = null;
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
