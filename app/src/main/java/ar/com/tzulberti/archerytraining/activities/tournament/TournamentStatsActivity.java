package ar.com.tzulberti.archerytraining.activities.tournament;

import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.ContainerStatsActivity;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

public class TournamentStatsActivity extends ContainerStatsActivity {

    @Override
    protected void renderRawData(TableLayout tableLayout, ISerieContainer container) {
        int roundAccumulatedScore = 0;
        Tournament tournament = (Tournament) container;

        int roundIndex = 0;
        for (TournamentSerie tournamentSerie : tournament.series) {
            int currentRoundIndex = tournament.getTournamentConstraint().getRoundIndex(tournamentSerie.index);
            boolean startingRound = (roundIndex != currentRoundIndex);
            if (startingRound) {
                roundIndex = currentRoundIndex;
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
