package ar.com.tzulberti.archerytraining.activities.tournament;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.TypedValue;
import android.view.Gravity;
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
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;

/**
 * For one tournament shows all the series and the options to view the
 * tournament stats
 *
 * Created by tzulberti on 5/19/17.
 */

public class ViewTournamentSeriesActivity extends AbstractTableDataActivity implements View.OnClickListener {

    public Tournament tournament;

    @Override
    protected boolean shouldShowHelp() {
        return true;
    }

    @Override
    protected String helpText() {
        return this.getString(R.string.tournament_show_existing_help);
    }

    protected void getValueFromIntent() {
        this.tournament = (Tournament) this.getIntent().getSerializableExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY);
    }


    protected boolean enableAddNew() {
        return this.tournament.series.size() < this.tournament.tournamentConstraint.getMaxSeries();
    }

    protected List<? extends Serializable> getData() {
        return this.tournament.getSeries();
    }

    protected void renderRow(Serializable data, TableRow tr) {
        tr.setPadding(0, 15, 0, 15);

        TextView serieIndexText = new TextView(this);
        TournamentSerie tournamentSerie = (TournamentSerie) data;
        serieIndexText.setText(getString(R.string.tournament_serie_current_serie, tournamentSerie.index));
        serieIndexText.setGravity(Gravity.START);

        tr.addView(serieIndexText);

        for (TournamentSerieArrow arrowData : tournamentSerie.arrows) {
            TextView arrowScoreText = new TextView(this);
            arrowScoreText.setText(TournamentHelper.getUserScore(arrowData.score, arrowData.isX));
            arrowScoreText.setTextColor(TournamentHelper.getFontColor(arrowData.score));
            arrowScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            arrowScoreText.setBackgroundResource(R.drawable.rounded);

            arrowScoreText.getBackground().setColorFilter(new PorterDuffColorFilter(TournamentHelper.getBackground(arrowData.score), PorterDuff.Mode.SRC_IN));
            arrowScoreText.setPadding(10, 0, 10, 0);
            arrowScoreText.setGravity(Gravity.CENTER);
            tr.addView(arrowScoreText);
        }

        TextView totalScoreText = new TextView(this);
        totalScoreText.setText(String.valueOf(tournamentSerie.totalScore));
        totalScoreText.setBackgroundResource(R.drawable.rounded);
        totalScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        totalScoreText.setGravity(Gravity.CENTER);

        tr.addView(totalScoreText);
        tr.setId(tournamentSerie.index);
        tr.setOnClickListener(this);
    }

    protected void addNewValue() {
        // check if the last serie has some arrows, and if that is the case then create a new
        // serie, but if it is empty in arrows, then edit that serie instead of creating one
        TournamentSerie tournamentSerie = null;
        if (this.tournament.getSeries().size() > 0) {
            TournamentSerie lastSerie = (TournamentSerie) this.tournament.getSeries().get(this.tournament.getSeries().size() - 1);
            if (lastSerie.arrows.size() == 0) {
                tournamentSerie = lastSerie;
            }
        }

        if (tournamentSerie == null) {
            tournamentSerie = this.tournamentDAO.createNewSerie(this.tournament);
        }

        Intent intent = new Intent(this, ViewSerieInformationActivity.class);
        intent.putExtra(ViewSerieInformationActivity.SERIE_ARGUMENT_KEY, tournamentSerie);
        startActivity(intent);
    }



    protected void addButtonsBeforeData(TableLayout tableLayout) {
        TableLayout tournamentTableLayout = (TableLayout) View.inflate(
                this,
                R.layout.tournament_tournament_information,
                null
        );

        ((TextView) tournamentTableLayout.findViewById(R.id.tournament_name)).setText(this.tournament.name);
        ((TextView) tournamentTableLayout.findViewById(R.id.datetime)).setText(DatetimeHelper.DATE_FORMATTER.format(this.tournament.datetime));
        ((TextView) tournamentTableLayout.findViewById(R.id.total_tournament_score)).setText(String.valueOf(this.tournament.totalScore) + "/" + this.tournament.getTournamentConstraint().getMaxPossibleScore());
        ((TextView) tournamentTableLayout.findViewById(R.id.tournament_constraint)).setText(this.tournament.getTournamentConstraint().translatedName);

        ImageView imageView = (ImageView) tournamentTableLayout.findViewById(R.id.tournament_type);
        if (this.tournament.isTournament) {
            imageView.setImageResource(R.drawable.ic_trophy);
        } else {
            imageView.setImageResource(R.drawable.ic_bow);
        }

        tableLayout.addView(tournamentTableLayout);
    }


    protected void addButtonsAfterData(TableLayout tableLayout) {
        int span = 1;
        if (! this.tournament.series.isEmpty()) {
            // the +2 is because the series index and the total score
            span = this.tournament.tournamentConstraint.getConstraintForSerie(1).arrowsPerSeries + 2;
        }

        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        trParams.span = span;


        boolean buttonsEnabled = ! this.tournament.series.isEmpty();

        TableRow tr3 = new TableRow(this);
        Button viewTournamentArrowStats = new Button(this);
        viewTournamentArrowStats.setId(Integer.MAX_VALUE - 2);
        viewTournamentArrowStats.setText(R.string.tournament_view_arrow_stats);
        viewTournamentArrowStats.setLayoutParams(trParams);
        viewTournamentArrowStats.setOnClickListener(this);
        viewTournamentArrowStats.setEnabled(buttonsEnabled);
        tr3.addView(viewTournamentArrowStats);
        tableLayout.addView(tr3);

        TableRow trN = new TableRow(this);
        Button deleteButton = new Button(this);
        deleteButton.setId(Integer.MAX_VALUE - 15);
        deleteButton.setText(R.string.tournament_view_tournament_delete);
        deleteButton.setLayoutParams(trParams);
        deleteButton.setOnClickListener(this);
        deleteButton.getBackground().setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
        trN.addView(deleteButton);
        tableLayout.addView(trN);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();


        if (id == Integer.MAX_VALUE - 2) {
            Intent intent = new Intent(this, TournamentStatsActivity.class);
            intent.putExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY, this.tournament);
            startActivity(intent);

        } else if (id == Integer.MAX_VALUE - 15) {
            // selected option to delete the tournament
            final ViewTournamentSeriesActivity self = this;

            new AlertDialog.Builder(this)
                    .setTitle(R.string.common_confirmation_dialog_title)
                    .setMessage(R.string.tournament_confirm_delete_tournament)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            tournamentDAO.deleteTournament(tournament.id);

                            Intent intent = new Intent(self, ViewExistingTournamentsActivity.class);
                            startActivity(intent);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        } else {
            TournamentSerie tournamentSerie = this.tournament.series.get(v.getId() - 1);
            Intent intent = new Intent(this, ViewSerieInformationActivity.class);
            intent.putExtra(ViewSerieInformationActivity.SERIE_ARGUMENT_KEY, tournamentSerie);
            startActivity(intent);
        }
    }
}
