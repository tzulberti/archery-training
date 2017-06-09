package ar.com.tzulberti.archerytraining.fragments.playoff;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Text;

import java.util.Random;
import java.util.StringTokenizer;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.dao.PlayoffDAO;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractSerieArrowsFragment;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerie;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieArrow;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;

/**
 * Created by tzulberti on 6/4/17.
 */

public class ViewPlayoffSerieInformationFragment extends AbstractSerieArrowsFragment {

    private PlayoffDAO playoffDAO;
    private EditText opponentScoreEdit;

    @Override
    protected int getLayoutResource() {
        return R.layout.playoff_view_serie_arrows;
    }

    @Override
    protected void setDAOs() {
        MainActivity activity = (MainActivity) getActivity();
        this.playoffDAO = activity.getPlayoffDAO();
    }

    @Override
    protected void setAdditionalInformation(View view) {
        Playoff playoff = (Playoff) this.serie.getContainer();
        this.opponentScoreEdit = (EditText) view.findViewById(R.id.opponent_score);
        if (playoff.computerPlayOffConfiguration != null) {
            this.opponentScoreEdit.setVisibility(View.GONE);

            // set a random value so the user can compete
            TextView computerScoreText = (TextView) view.findViewById(R.id.computer_score);
            Random random = new Random();
            int computerScore = random.nextInt(playoff.computerPlayOffConfiguration.maxScore + 1 - playoff.computerPlayOffConfiguration.minScore) + playoff.computerPlayOffConfiguration.minScore;
            PlayoffSerie playoffSerie = (PlayoffSerie) this.serie;
            playoffSerie.opponentTotalScore = computerScore;
            computerScoreText.setText(String.valueOf(computerScore));

        } else {
            view.findViewById(R.id.computer_score).setVisibility(View.GONE);
            this.opponentScoreEdit.setError(this.getString(R.string.commonRequiredValidationError));
        }
    }

    @Override
    protected void saveSerie() {
        PlayoffSerie playoffSerie = (PlayoffSerie) this.serie;
        this.playoffDAO.updateSerie(playoffSerie);
    }

    @Override
    protected void deleteSerie() {
        PlayoffSerie playoffSerie = (PlayoffSerie) this.serie;
        this.playoffDAO.deleteSerie(playoffSerie.id);
    }

    @Override
    protected void addArrowData(float x, float y, int score, boolean isX) {
        PlayoffSerie playoffSerie = (PlayoffSerie) this.serie;
        PlayoffSerieArrow playoffSerieArrow = new PlayoffSerieArrow();
        playoffSerieArrow.xPosition = x;
        playoffSerieArrow.yPosition = y;
        playoffSerieArrow.score = score;
        playoffSerieArrow.isX = isX;
        playoffSerie.arrows.add(playoffSerieArrow);
    }

    @Override
    protected BaseClickableFragment getContainerDetailsFragment() {
        return new ViewPlayoffSeriesFragment();
    }

    @Override
    protected AbstractSerieArrowsFragment getSerieDetailsFragment() {
        return new ViewPlayoffSerieInformationFragment();
    }

    @Override
    protected ISerie createNewSerie() {
        return this.playoffDAO.createSerie((Playoff) this.serie.getContainer());
    }

    @Override
    protected boolean canActivateButtons() {
        PlayoffSerie playoffSerie = (PlayoffSerie) this.serie;
        if (this.serie.getArrows().size() != 3) {
            return false;
        }

        if (playoffSerie.playoff.computerPlayOffConfiguration != null) {
            return true;
        } else {
            // validates that the user has input the opponent score when not using computer
            String opponentScore = this.opponentScoreEdit.getText().toString();
            if (StringUtils.isEmpty(opponentScore)) {
                this.opponentScoreEdit.setError(this.getString(R.string.commonRequiredValidationError));
                return false;
            }
            playoffSerie.opponentTotalScore = Integer.valueOf(opponentScore);
            return true;
        }
    }

    @Override
    protected boolean hasFinished() {
        Playoff playoff = (Playoff) this.serie.getContainer();

        if (playoff.opponentPlayoffScore >= 6 || playoff.userPlayoffScore >= 6) {
            // one of the 2 got to 6 points so it finished
            return true;
        } else if (playoff.series.size() == 5) {
            // it has finished the 5 rounds
            return true;
        } else {
            return false;
        }
    }
}
