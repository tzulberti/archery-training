package ar.com.tzulberti.archerytraining.activities.tournament;

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
import ar.com.tzulberti.archerytraining.activities.common.ContainerStatsActivity;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;

/**
 * For one tournament shows all the series and the different options
 * Created by tzulberti on 5/19/17.
 */

public class ViewTournamentSeriesActivity extends BaseArcheryTrainingActivity implements View.OnClickListener {

    public Tournament tournament;
    private TableLayout dataContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.createDAOs();
        setContentView(R.layout.tournament_view_series);

        this.tournament = (Tournament) this.getIntent().getSerializableExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY);

        final ViewTournamentSeriesActivity self = this;

        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        if (this.tournament.series.size() == (this.tournament.tournamentConstraint.seriesPerRound * 2)) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // make sure that the user can add another serie to this tournament
                    TournamentSerie tournamentSerie = self.tournamentDAO.createNewSerie(self.tournament);
                    if (tournamentSerie == null) {
                        // TODO show message to the user
                        return;
                    }

                    Intent intent = new Intent(self, ViewSerieInformationActivity.class);
                    intent.putExtra(ViewSerieInformationActivity.SERIE_ARGUMENT_KEY, tournamentSerie);
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
            TournamentSerie tournamentSerie = this.tournamentDAO.createNewSerie(tournament);

            Intent intent = new Intent(this, ViewSerieInformationActivity.class);
            intent.putExtra(ViewSerieInformationActivity.SERIE_ARGUMENT_KEY, tournamentSerie);
            startActivity(intent);

            return;
        }
        

        for (TournamentSerie data : this.tournament.series) {
            TableRow tr = new TableRow(this);
            tr.setPadding(0, 15, 0, 15);

            TextView serieIndexText = new TextView(this);
            serieIndexText.setText(getString(R.string.tournament_serie_current_serie, data.index));
            serieIndexText.setGravity(Gravity.START);

            tr.addView(serieIndexText);

            for (TournamentSerieArrow arrowData : data.arrows) {
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
            totalScoreText.setText(String.valueOf(data.totalScore));
            totalScoreText.setBackgroundResource(R.drawable.rounded);
            totalScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            totalScoreText.setGravity(Gravity.CENTER);

            tr.addView(totalScoreText);
            tr.setId(data.index);
            tr.setOnClickListener(this);

            this.dataContainer.addView(tr);
        }

        int span = 1;
        if (! this.tournament.series.isEmpty()) {
            // the +2 is because the series index and the total score
            span = this.tournament.tournamentConstraint.arrowsPerSeries + 2;
        }

        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        trParams.span = span;


        boolean buttonsEnabled = ! this.tournament.series.isEmpty();
        // add the buttons to delete/view charts for the tournament
        TableRow tr1 = new TableRow(this);
        Button viewChartsButton = new Button(this);
        viewChartsButton.setId(Integer.MAX_VALUE - 1);
        viewChartsButton.setText(R.string.tournament_view_all_impacts);
        viewChartsButton.setLayoutParams(trParams);
        viewChartsButton.setOnClickListener(this);
        viewChartsButton.setEnabled(buttonsEnabled);
        tr1.addView(viewChartsButton);
        this.dataContainer.addView(tr1);

        TableRow tr2 = new TableRow(this);
        Button viewScoreSheetButton = new Button(this);
        viewScoreSheetButton.setId(Integer.MAX_VALUE - 2);
        viewScoreSheetButton.setText(R.string.tournament_view_score_sheet);
        viewScoreSheetButton.setLayoutParams(trParams);
        viewScoreSheetButton.setOnClickListener(this);
        viewScoreSheetButton.setEnabled(buttonsEnabled);
        tr2.addView(viewScoreSheetButton);
        this.dataContainer.addView(tr2);


        TableRow tr3 = new TableRow(this);
        Button viewTournamentArrowStats = new Button(this);
        viewTournamentArrowStats.setId(Integer.MAX_VALUE - 3);
        viewTournamentArrowStats.setText(R.string.tournament_view_arrow_stats);
        viewTournamentArrowStats.setLayoutParams(trParams);
        viewTournamentArrowStats.setOnClickListener(this);
        viewTournamentArrowStats.setEnabled(buttonsEnabled);
        tr3.addView(viewTournamentArrowStats);
        this.dataContainer.addView(tr3);

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
            // selected the option to view all impacts for the current tournament
            Intent intent = null;
            if (id == Integer.MAX_VALUE - 1) {
                intent = new Intent(this, ViewAllTournamentTargetArrowActivity.class);

            } else if (id == Integer.MAX_VALUE - 2) {
                intent = new Intent(this, ViewTournamentScoreSheetActivity.class);

            } else if (id == Integer.MAX_VALUE - 3) {
                intent = new Intent(this, ContainerStatsActivity.class);
            }

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
