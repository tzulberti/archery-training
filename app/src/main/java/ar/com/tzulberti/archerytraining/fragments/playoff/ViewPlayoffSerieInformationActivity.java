package ar.com.tzulberti.archerytraining.fragments.playoff;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.dao.PlayoffDAO;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.database.consts.SerieInformationConsts;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractSerieArrowsActivity;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerie;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieArrow;

/**
 * Created by tzulberti on 6/4/17.
 */

public class ViewPlayoffSerieInformationActivity extends AbstractSerieArrowsActivity {

    private PlayoffDAO playoffDAO;
    private SerieDataDAO serieDataDAO;

    private EditText opponentScoreEdit;

    @Override
    protected int getLayoutResource() {
        return R.layout.playoff_view_serie_arrows;
    }


    @Override
    protected void setAdditionalInformation() {
        Playoff playoff = (Playoff) this.serie.getContainer();
        PlayoffSerie playoffSerie = (PlayoffSerie) this.serie;
        this.opponentScoreEdit = (EditText) this.findViewById(R.id.opponent_score);

        if (playoff.computerPlayOffConfiguration != null) {
            this.opponentScoreEdit.setVisibility(View.GONE);


            TextView computerScoreText = (TextView) this.findViewById(R.id.computer_score);
            int computerScore = -1;
            if (playoffSerie.opponentTotalScore > 0) {
                // the user already saved the playoff serie so used the saved value
                computerScore = playoffSerie.opponentTotalScore;
            } else {
                // set a random value so the user can compete, when the serie isn't complete
                Random random = new Random();
                computerScore = random.nextInt(playoff.computerPlayOffConfiguration.maxScore + 1 - playoff.computerPlayOffConfiguration.minScore) + playoff.computerPlayOffConfiguration.minScore;
                playoffSerie.opponentTotalScore = computerScore;
            }
            computerScoreText.setText(String.valueOf(computerScore));

        } else {
            this.findViewById(R.id.computer_score).setVisibility(View.GONE);
            if (playoffSerie.opponentTotalScore > 0 ) {
                this.opponentScoreEdit.setText(String.valueOf(playoffSerie.opponentTotalScore));
            } else {
                this.opponentScoreEdit.setError(this.getString(R.string.commonRequiredValidationError));
            }
        }
    }

    @Override
    protected void saveSerie() {
        PlayoffSerie playoffSerie = (PlayoffSerie) this.serie;
        this.serieDataDAO.addSerieData(playoffSerie.playoff.tournamentConstraint.distance, playoffSerie.arrows.size(), SerieInformationConsts.TrainingType.PLAYOFF);
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
    protected Class<? extends AppCompatActivity> getContainerDetailsFragment() {
        return ViewPlayoffSeriesActivity.class;
    }

    @Override
    protected AbstractSerieArrowsActivity getSerieDetailsFragment() {
        return new ViewPlayoffSerieInformationActivity();
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
