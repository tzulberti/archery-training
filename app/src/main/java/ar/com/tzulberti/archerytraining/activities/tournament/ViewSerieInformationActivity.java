package ar.com.tzulberti.archerytraining.activities.tournament;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.activities.common.AbstractSerieArrowsActivity;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.constrains.RoundConstraint;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Shows all the serie impacts on the target or it will allow the user to
 * update the serie arrow information
 *
 * Created by tzulberti on 4/25/17.
 */
public class ViewSerieInformationActivity extends AbstractSerieArrowsActivity {


    @Override
    protected int getLayoutResource() {
        return R.layout.tournament_view_serie_arrow;
    }

    @Override
    protected void setAdditionalInformation() {
        TournamentSerie tournamentSerie = (TournamentSerie) this.serie;
        TournamentConstraint tournamentConstraint = tournamentSerie.tournament.tournamentConstraint;
        RoundConstraint roundConstraint = tournamentConstraint.getConstraintForSerie(this.serie.getIndex());
        int maxScore = tournamentConstraint.getMaxPossibleScore();
        ((TextView) this.findViewById(R.id.total_tournament_score)).setText(String.format("%s / %s", tournamentSerie.tournament.totalScore, maxScore));

        // check the number of text scrore that shoudl be hiddeen taken into account
        // the number of arrows per score
        for (int i = roundConstraint.arrowsPerSeries + 1; i < 7; i++) {
            int id = 0;

            switch (i) {
                case 4:
                    id = R.id.current_score4;
                    break;
                case 5:
                    id = R.id.current_score5;
                    break;
                case 6:
                    id = R.id.current_score6;
                    break;
            }

            if (id != 0) {
                this.findViewById(id).setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void saveSerie() {
        TournamentSerie tournamentSerie = (TournamentSerie) this.serie;
        TournamentConstraint tournamentConstraint = tournamentSerie.tournament.tournamentConstraint;
        RoundConstraint roundConstraint = tournamentConstraint.getConstraintForSerie(this.serie.getIndex());
        this.serieDataDAO.addSerieData(
            roundConstraint.distance,
            tournamentSerie.arrows.size(),
            SerieInformationConsts.TrainingType.TOURNAMENT
        );
        this.tournamentDAO.saveTournamentSerieInformation(tournamentSerie);
    }

    @Override
    protected void deleteSerie() {
        TournamentSerie tournamentSerie = (TournamentSerie) this.serie;
        this.tournamentDAO.deleteSerie(tournamentSerie.id);
    }

    @Override
    protected void addArrowData(float x, float y, int score, boolean isX) {
        TournamentSerieArrow arrow = new TournamentSerieArrow();
        arrow.xPosition = x;
        arrow.yPosition = y;
        arrow.score = score;
        arrow.isX = isX;
        TournamentSerie tournamentSerie = (TournamentSerie) this.serie;
        tournamentSerie.arrows.add(arrow);
    }

    @Override
    protected Class<? extends AppCompatActivity> getContainerDetailsFragment() {
        return ViewTournamentSeriesActivity.class;
    }

    @Override
    protected ISerie createNewSerie() {
        return this.tournamentDAO.createNewSerie((Tournament) this.serie.getContainer());
    }

    @Override
    protected boolean canActivateButtons() {
        Tournament tournament = (Tournament) this.serie.getContainer();
        TournamentConstraint tournamentConstraint = tournament.tournamentConstraint;
        RoundConstraint roundConstraint = tournamentConstraint.getConstraintForSerie(this.serie.getIndex());
        return this.serie.getArrows().size() == roundConstraint.arrowsPerSeries;
    }

    @Override
    protected boolean hasFinished() {
        Tournament tournament = (Tournament) this.serie.getContainer();
        TournamentConstraint tournamentConstraint = tournament.tournamentConstraint;
        return this.serie.getIndex() == tournamentConstraint.getMaxSeries();
    }
}
