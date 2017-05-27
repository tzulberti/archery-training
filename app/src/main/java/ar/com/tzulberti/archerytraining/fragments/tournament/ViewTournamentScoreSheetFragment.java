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
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Created by tzulberti on 5/26/17.
 */

public class ViewTournamentScoreSheetFragment extends BaseTournamentFragment {

    private Tournament tournament;
    private TableLayout tableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        View view = inflater.inflate(R.layout.tournament_view_tournament_score_sheet, container, false);
        this.setObjects();

        this.tournament = this.tournamentDAO.getTournamentInformation(this.getArguments().getLong("tournamentId"));
        this.tournamentDAO.getTournamentSeriesInformation(this.tournament);

        this.tableLayout = (TableLayout) view.findViewById(R.id.tournament_score_sheet_table);
        this.renderScoreSheet();
        return view;
    }


    private void renderScoreSheet() {
        Context context = this.getContext();
        int roundAccumulatedScore = 0;
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        for (TournamentSerie tournamentSerie : this.tournament.series) {
            if (tournamentSerie.index == 1 || tournamentSerie.index == 7) {
                int roundIndex = (tournamentSerie.index == 1) ? 1 : 2;
                TableRow tr = new TableRow(context);
                tr.setPadding(0, 25, 0, 10);
                TextView roundIndexText = new TextView(context);
                roundIndexText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                roundIndexText.setText("Ronda " + String.valueOf(roundIndex));
                tr.addView(roundIndexText);
                this.tableLayout.addView(tr);
                roundAccumulatedScore = 0;
            }

            TableRow serieTableRow = new TableRow(context);
            TextView serieIndexText = new TextView(context);
            serieIndexText.setText("Serie " + String.valueOf(tournamentSerie.index));
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
            this.tableLayout.addView(serieTableRow);
        }
    }

    @Override
    public void handleClick(View v) {
    }
}