package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.view.View;
import android.widget.TextView;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.dao.TournamentDAO;
import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractSerieArrowsFragment;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.common.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Created by tzulberti on 4/25/17.
 */

public class ViewSerieInformationFragment extends AbstractSerieArrowsFragment {


    private TournamentDAO tournamentDAO;
    private SerieDataDAO serieDataDAO;

    @Override
    protected int getLayoutResource() {
        return R.layout.tournament_view_serie_arrow;
    }

    @Override
    protected void setAdditionalInformation(View view) {
        TournamentSerie tournamentSerie = (TournamentSerie) this.serie;
        TournamentConstraint tournamentConstraint = tournamentSerie.tournament.tournamentConstraint;
        int maxScore = 10 * tournamentConstraint.seriesPerRound * 2 * tournamentConstraint.arrowsPerSeries;
        ((TextView) view.findViewById(R.id.total_tournament_score)).setText(String.format("%s / %s", tournamentSerie.tournament.totalScore, maxScore));

        // check the number of text scrore that shoudl be hiddeen taken into account
        // the number of arrows per score
        for (int i = tournamentConstraint.arrowsPerSeries + 1; i < 7; i++) {
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
            view.findViewById(id).setVisibility(View.GONE);
        }
    }

    @Override
    protected void saveSerie() {
        TournamentSerie tournamentSerie = (TournamentSerie) this.serie;
        this.serieDataDAO.addSerieData(
            tournamentSerie.tournament.tournamentConstraint.distance,
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
    protected void setDAOs() {
        MainActivity activity = (MainActivity) getActivity();
        this.tournamentDAO = activity.getTournamentDAO();
        this.serieDataDAO = activity.getSerieDAO();
    }

    @Override
    protected BaseClickableFragment getContainerDetailsFragment() {
        return new ViewTournamentSeriesFragment();
    }

    @Override
    protected AbstractSerieArrowsFragment getSerieDetailsFragment() {
        return new ViewSerieInformationFragment();
    }

    @Override
    protected ISerie createNewSerie() {
        return this.tournamentDAO.createNewSerie((Tournament) this.serie.getContainer());
    }

    @Override
    protected boolean canActivateButtons() {
        Tournament tournament = (Tournament) this.serie.getContainer();
        return this.serie.getArrows().size() == tournament.tournamentConstraint.arrowsPerSeries;
    }

    @Override
    protected boolean hasFinished() {
        Tournament tournament = (Tournament) this.serie.getContainer();
        return this.serie.getIndex() == (tournament.tournamentConstraint.seriesPerRound * 2);
    }
}
