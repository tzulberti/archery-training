package ar.com.tzulberti.archerytraining.activities.playoff;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.util.TypedValue;
import android.view.Gravity;

import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.AbstractSerieArrowsActivity;
import ar.com.tzulberti.archerytraining.activities.common.BaseArcheryTrainingActivity;

import ar.com.tzulberti.archerytraining.helper.PlayoffHelper;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieScore;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerie;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieArrow;

/**
 * Show the detail of one playoff
 *
 * Created by tzulberti on 6/5/17.
 */
public class ViewPlayoffSeriesActivity extends BaseArcheryTrainingActivity implements View.OnClickListener {

    private Playoff playoff;
    private TableLayout dataContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.createDAOs();
        setContentView(R.layout.tournament_view_series);


        this.playoff = (Playoff) this.getIntent().getSerializableExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY);

        final ViewPlayoffSeriesActivity self = this;


        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        if (this.playoff.series.size() < 5 || this.playoff.opponentPlayoffScore >= 6 || this.playoff.userPlayoffScore >= 6) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // make sure that the user can add another serie to this tournament
                    PlayoffSerie playoffSerie = self.playoffDAO.createSerie(self.playoff);

                    Intent intent = new Intent(self, ViewPlayoffSerieInformationActivity.class);
                    intent.putExtra(ViewPlayoffSerieInformationActivity.SERIE_ARGUMENT_KEY, playoffSerie);
                    startActivity(intent);
                }
            });
        }

        this.dataContainer = (TableLayout) this.findViewById(R.id.tournament_series_list);
        this.showExistingSeries();
    }

    public void showExistingSeries() {
        if (this.getIntent().hasExtra("creating")) {
            this.getIntent().removeExtra("creating");
            PlayoffSerie playoffSerie = this.playoffDAO.createSerie(playoff);

            Intent intent = new Intent(this, ViewPlayoffSerieInformationActivity.class);
            intent.putExtra(ViewPlayoffSerieInformationActivity.SERIE_ARGUMENT_KEY, playoffSerie);
            startActivity(intent);

            return;
        }


        for (PlayoffSerie data : this.playoff.series) {
            TableRow tr = new TableRow(this);
            tr.setPadding(0, 15, 0, 15);

            TextView serieIndexText = new TextView(this);
            serieIndexText.setText(getString(R.string.tournament_serie_current_serie, data.index));
            serieIndexText.setGravity(Gravity.START);

            tr.addView(serieIndexText);

            for (PlayoffSerieArrow arrowData : data.arrows) {
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
            totalScoreText.setText(String.valueOf(data.userTotalScore));
            totalScoreText.setBackgroundResource(R.drawable.rounded);
            totalScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            totalScoreText.setGravity(Gravity.CENTER);

            PlayoffSerieScore playoffSerieScore = PlayoffHelper.getScore(data.userTotalScore, data.opponentTotalScore);
            TextView serieScore = new TextView(this);
            serieScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            serieScore.setGravity(Gravity.CENTER);
            serieScore.setText(this.getString(R.string.playoff_serie_score, playoffSerieScore.userPoints, playoffSerieScore.opponentPoints));

            TextView opponentScoreText = new TextView(this);
            opponentScoreText.setText(String.valueOf(data.opponentTotalScore));
            opponentScoreText.setBackgroundResource(R.drawable.rounded);
            opponentScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            opponentScoreText.setGravity(Gravity.CENTER);

            tr.addView(totalScoreText);
            tr.addView(serieScore);
            tr.addView(opponentScoreText);
            tr.setId(data.index);
            tr.setOnClickListener(this);

            this.dataContainer.addView(tr);
        }

        int span = 1;
        if (! this.playoff.series.isEmpty()) {
            // the +2 is because the series index and the total score
            span = this.playoff.tournamentConstraint.arrowsPerSeries + 2;
        }

        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        trParams.span = span;

        /*

        boolean buttonsEnabled = ! this.tournament.series.isEmpty();
        // add the buttons to delete/view charts for the tournament
        TableRow tr1 = new TableRow(context);
        Button viewChartsButton = new Button(context);
        viewChartsButton.setId(Integer.MAX_VALUE - 1);
        viewChartsButton.setText(R.string.tournament_view_all_impacts);
        viewChartsButton.setLayoutParams(trParams);
        viewChartsButton.setOnClickListener(this);
        viewChartsButton.setEnabled(buttonsEnabled);
        tr1.addView(viewChartsButton);
        this.dataContainer.addView(tr1);

        TableRow tr2 = new TableRow(context);
        Button viewScoreSheetButton = new Button(context);
        viewScoreSheetButton.setId(Integer.MAX_VALUE - 2);
        viewScoreSheetButton.setText(R.string.tournament_view_score_sheet);
        viewScoreSheetButton.setLayoutParams(trParams);
        viewScoreSheetButton.setOnClickListener(this);
        viewScoreSheetButton.setEnabled(buttonsEnabled);
        tr2.addView(viewScoreSheetButton);
        this.dataContainer.addView(tr2);


        TableRow tr3 = new TableRow(context);
        Button viewTournamentArrowStats = new Button(context);
        viewTournamentArrowStats.setId(Integer.MAX_VALUE - 3);
        viewTournamentArrowStats.setText(R.string.tournament_view_arrow_stats);
        viewTournamentArrowStats.setLayoutParams(trParams);
        viewTournamentArrowStats.setOnClickListener(this);
        viewTournamentArrowStats.setEnabled(buttonsEnabled);
        tr3.addView(viewTournamentArrowStats);
        this.dataContainer.addView(tr3);

        */
        TableRow trN = new TableRow(this);
        Button deleteButton = new Button(this);
        deleteButton.setId(Integer.MAX_VALUE - 15);
        deleteButton.setText(R.string.tournament_view_tournament_delete);
        deleteButton.setLayoutParams(trParams);
        deleteButton.setOnClickListener(this);
        deleteButton.getBackground().setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
        trN.addView(deleteButton);
        this.dataContainer.addView(trN);


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == Integer.MAX_VALUE - 1 || id == Integer.MAX_VALUE - 2 || id == Integer.MAX_VALUE -3 ) {


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
