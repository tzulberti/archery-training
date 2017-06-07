package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractSerieArrowsFragment;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Created by tzulberti on 5/26/17.
 */

public class ViewTournamentScoreSheetFragment extends BaseTournamentFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        View view = inflater.inflate(R.layout.tournament_view_tournament_score_sheet, container, false);
        this.setObjects();

        Tournament tournament = this.getTournamentArgument();
        this.renderScoreSheet((TableLayout) view.findViewById(R.id.tournament_score_sheet_table), tournament);
        return view;
    }


    private void renderScoreSheet(TableLayout tableLayout, Tournament tournament) {
        Context context = this.getContext();
        int roundAccumulatedScore = 0;
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        for (TournamentSerie tournamentSerie : tournament.series) {
            if (tournamentSerie.index == 1 || tournamentSerie.index == 7) {
                int roundIndex = (tournamentSerie.index == 1) ? 1 : 2;
                TableRow tr = new TableRow(context);
                tr.setPadding(0, 25, 0, 10);
                TextView roundIndexText = new TextView(context);
                roundIndexText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                roundIndexText.setText(getString(R.string.tournament_serie_current_round, roundIndex));
                tr.addView(roundIndexText);
                tableLayout.addView(tr);
                roundAccumulatedScore = 0;
            }

            TableRow serieTableRow = new TableRow(context);
            TextView serieIndexText = new TextView(context);
            serieIndexText.setText(getString(R.string.tournament_serie_current_serie, tournamentSerie.index));
            serieTableRow.addView(serieIndexText);

            for (TournamentSerieArrow arrowData : tournamentSerie.arrows) {
                TextView arrowScoreText = new TextView(context);
                arrowScoreText.setText(TournamentHelper.getUserScore(arrowData.score, arrowData.isX));
                serieTableRow.addView(arrowScoreText);
            }
            TextView totalSerieText = new TextView(context);
            totalSerieText.setText(String.valueOf(tournamentSerie.totalScore));
            serieTableRow.addView(totalSerieText);


            TextView accumulatedRoundScoreText = new TextView(context);
            if (tournamentSerie.index == 1 || tournamentSerie.index == 7) {
                accumulatedRoundScoreText.setText("-");
            } else {
                accumulatedRoundScoreText.setText(String.valueOf(tournamentSerie.totalScore + roundAccumulatedScore));
            }
            roundAccumulatedScore += tournamentSerie.totalScore;
            serieTableRow.addView(accumulatedRoundScoreText);
            tableLayout.addView(serieTableRow);
        }
    }

    @Override
    public void handleClick(View v) {
    }
}
