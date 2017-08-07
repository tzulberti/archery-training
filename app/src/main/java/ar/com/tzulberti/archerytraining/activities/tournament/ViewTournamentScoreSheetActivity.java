package ar.com.tzulberti.archerytraining.activities.tournament;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.AbstractSerieArrowsActivity;
import ar.com.tzulberti.archerytraining.activities.common.BaseArcheryTrainingActivity;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.constrains.RoundConstraint;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * View the tournament information as a score sheet.
 *
 * Created by tzulberti on 5/26/17.
 */
public class ViewTournamentScoreSheetActivity extends BaseArcheryTrainingActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.createDAOs();
        setContentView(R.layout.tournament_view_tournament_score_sheet);

        Tournament tournament = (Tournament) this.getIntent().getSerializableExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY);
        this.renderScoreSheet((TableLayout) this.findViewById(R.id.tournament_score_sheet_table), tournament);
    }


    private void renderScoreSheet(TableLayout tableLayout, Tournament tournament) {
        int roundAccumulatedScore = 0;

        RoundConstraint roundConstraint = null;
        for (TournamentSerie tournamentSerie : tournament.series) {
            RoundConstraint currentConstraint = tournament.getTournamentConstraint().getConstraintForSerie(1);
            boolean startingRound = (roundConstraint == null || roundConstraint != currentConstraint);
            if (startingRound) {
                roundConstraint = currentConstraint;
                int roundIndex = (tournamentSerie.index == 1) ? 1 : 2;
                TableRow tr = new TableRow(this);
                tr.setPadding(0, 25, 0, 10);
                TextView roundIndexText = new TextView(this);
                roundIndexText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                roundIndexText.setText(getString(R.string.tournament_serie_current_round, roundIndex));
                tr.addView(roundIndexText);
                tableLayout.addView(tr);
                roundAccumulatedScore = 0;
            }

            TableRow serieTableRow = new TableRow(this);
            TextView serieIndexText = new TextView(this);
            serieIndexText.setText(getString(R.string.tournament_serie_current_serie, tournamentSerie.index));
            serieTableRow.addView(serieIndexText);

            for (TournamentSerieArrow arrowData : tournamentSerie.arrows) {
                TextView arrowScoreText = new TextView(this);
                arrowScoreText.setText(TournamentHelper.getUserScore(arrowData.score, arrowData.isX));
                serieTableRow.addView(arrowScoreText);
            }
            TextView totalSerieText = new TextView(this);
            totalSerieText.setText(String.valueOf(tournamentSerie.totalScore));
            serieTableRow.addView(totalSerieText);


            TextView accumulatedRoundScoreText = new TextView(this);
            if (startingRound) {
                accumulatedRoundScoreText.setText("-");
            } else {
                accumulatedRoundScoreText.setText(String.valueOf(tournamentSerie.totalScore + roundAccumulatedScore));
            }
            roundAccumulatedScore += tournamentSerie.totalScore;
            serieTableRow.addView(accumulatedRoundScoreText);
            tableLayout.addView(serieTableRow);
        }
    }
}
