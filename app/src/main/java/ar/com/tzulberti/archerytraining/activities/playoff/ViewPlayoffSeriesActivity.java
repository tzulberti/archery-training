package ar.com.tzulberti.archerytraining.activities.playoff;

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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.AbstractSerieArrowsActivity;
import ar.com.tzulberti.archerytraining.activities.common.AbstractTableDataActivity;


import ar.com.tzulberti.archerytraining.activities.common.ContainerStatsActivity;
import ar.com.tzulberti.archerytraining.activities.tournament.ViewSerieInformationActivity;
import ar.com.tzulberti.archerytraining.activities.tournament.ViewTournamentScoreSheetActivity;
import ar.com.tzulberti.archerytraining.helper.PlayoffHelper;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieScore;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerie;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieArrow;


/**
 * Show the detail of one playoff. Ie the table with all the series and it's score
 *
 * Created by tzulberti on 6/5/17.
 */
public class ViewPlayoffSeriesActivity extends AbstractTableDataActivity {

    private Playoff playoff;

    protected void getValueFromIntent() {
        this.playoff = (Playoff) this.getIntent().getSerializableExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY);
    }


    protected boolean enableAddNew() {
        return ! (this.playoff.series.size() >= 5 || this.playoff.opponentPlayoffScore >= 6 || this.playoff.userPlayoffScore >= 6);
    }

    protected List<? extends Serializable> getData() {
        return this.playoff.getSeries();
    }

    protected void renderRow(Serializable data, TableRow tr) {
        PlayoffSerie playoffSerie = (PlayoffSerie) data;
        tr.setPadding(0, 15, 0, 15);

        TextView serieIndexText = new TextView(this);
        serieIndexText.setText(getString(R.string.tournament_serie_current_serie, playoffSerie.index));
        serieIndexText.setGravity(Gravity.START);

        tr.addView(serieIndexText);

        for (PlayoffSerieArrow arrowData : playoffSerie.arrows) {
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
        totalScoreText.setText(String.valueOf(playoffSerie.userTotalScore));
        totalScoreText.setBackgroundResource(R.drawable.rounded);
        totalScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        totalScoreText.setGravity(Gravity.CENTER);

        PlayoffSerieScore playoffSerieScore = PlayoffHelper.getScore(playoffSerie.userTotalScore, playoffSerie.opponentTotalScore);
        TextView serieScore = new TextView(this);
        serieScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        serieScore.setGravity(Gravity.CENTER);
        serieScore.setText(this.getString(R.string.playoff_serie_score, playoffSerieScore.userPoints, playoffSerieScore.opponentPoints));

        TextView opponentScoreText = new TextView(this);
        opponentScoreText.setText(String.valueOf(playoffSerie.opponentTotalScore));
        opponentScoreText.setBackgroundResource(R.drawable.rounded);
        opponentScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        opponentScoreText.setGravity(Gravity.CENTER);

        tr.addView(totalScoreText);
        tr.addView(serieScore);
        tr.addView(opponentScoreText);
        tr.setId(playoffSerie.index);
        tr.setOnClickListener(this);
    }

    protected void addNewValue() {
        PlayoffSerie playoffSerie = this.playoffDAO.createSerie(playoff);

        Intent intent = new Intent(this, ViewPlayoffSerieInformationActivity.class);
        intent.putExtra(ViewSerieInformationActivity.SERIE_ARGUMENT_KEY, playoffSerie);
        startActivity(intent);
    }

    @Override
    protected void addButtonsBeforeData(TableLayout tableLayout) {

    }

    protected void addButtonsAfterData(TableLayout tableLayout) {
        int span = 1;
        if (!this.playoff.series.isEmpty()) {
            // the +4 is because the series index and the total score, the serie score, and the opponent score
            span = 3 + 4;
        }

        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        trParams.span = span;


        boolean buttonsEnabled = !this.playoff.series.isEmpty();

        /*
        TableRow tr2 = new TableRow(context);
        Button viewScoreSheetButton = new Button(context);
        viewScoreSheetButton.setId(Integer.MAX_VALUE - 2);
        viewScoreSheetButton.setText(R.string.tournament_view_score_sheet);
        viewScoreSheetButton.setLayoutParams(trParams);
        viewScoreSheetButton.setOnClickListener(this);
        viewScoreSheetButton.setEnabled(buttonsEnabled);
        tr2.addView(viewScoreSheetButton);
        this.dataContainer.addView(tr2);
        */

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

        if (id == Integer.MAX_VALUE - 1 || id == Integer.MAX_VALUE - 2) {
            Intent intent = null;
            if (id == Integer.MAX_VALUE - 1) {
                intent = new Intent(this, ViewTournamentScoreSheetActivity.class);

            } else if (id == Integer.MAX_VALUE - 2) {
                intent = new Intent(this, ContainerStatsActivity.class);
            }

            intent.putExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY, this.playoff);
            startActivity(intent);

        } else if (id == Integer.MAX_VALUE - 15) {
            // selected option to delete the tournament
            final ViewPlayoffSeriesActivity self = this;
            new AlertDialog.Builder(this)
                    .setTitle(R.string.common_confirmation_dialog_title)
                    .setMessage(R.string.playoff_confirm_delete_playoff)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            playoffDAO.deletePlayoff(playoff.id);

                            Intent intent = new Intent(self, ViewExistingPlayoffActivity.class);
                            startActivity(intent);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        } else {
            PlayoffSerie playoffSerie = this.playoff.series.get(v.getId() - 1);

            Intent intent = new Intent(this, ViewPlayoffSerieInformationActivity.class);
            intent.putExtra(ViewPlayoffSerieInformationActivity.SERIE_ARGUMENT_KEY, playoffSerie);
            startActivity(intent);
        }
    }
}
