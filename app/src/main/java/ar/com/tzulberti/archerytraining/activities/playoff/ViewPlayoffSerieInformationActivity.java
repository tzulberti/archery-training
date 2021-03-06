package ar.com.tzulberti.archerytraining.activities.playoff;

import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.AbstractSerieArrowsActivity;
import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.constrains.RoundConstraint;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerie;
import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieArrow;
import ar.com.tzulberti.archerytraining.model.series.SerieData;

/**
 * Used to view one of the playoffserie arrows on the target
 *
 * Created by tzulberti on 6/4/17.
 */
public class ViewPlayoffSerieInformationActivity extends AbstractSerieArrowsActivity {

    private static final int MAX_ARROWS_ON_PLAYOFF_SERIE = 3;

    private EditText opponentScoreEdit;

    @Override
    protected int getLayoutResource() {
        return R.layout.playoff_view_serie_arrows;
    }

    @Override
    protected boolean shouldShowHelp() {
        return true;
    }

    @Override
    protected String helpText() {
        return this.getString(R.string.playoff_view_serie_help);
    }


    @Override
    protected void setAdditionalInformation() {
        Playoff playoff = (Playoff) this.serie.getContainer();
        PlayoffSerie playoffSerie = (PlayoffSerie) this.serie;
        this.opponentScoreEdit = (EditText) this.findViewById(R.id.opponent_score);


        if (playoff.computerPlayOffConfiguration != null) {
            this.opponentScoreEdit.setVisibility(View.GONE);

            TextView computerScoreText = (TextView) this.findViewById(R.id.computer_score);
            int computerScore;
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
                this.opponentScoreEdit.setError(this.getString(R.string.playoff_opponent_score_required));
            }
            this.opponentScoreEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ViewPlayoffSerieInformationActivity.this.canActivateButtons()) {
                        ViewPlayoffSerieInformationActivity.this.activateButtons();
                    }
                }
            });
        }
    }

    @Override
    protected void saveSerie() {
        PlayoffSerie playoffSerie = (PlayoffSerie) this.serie;
        TournamentConstraint tournamentConstraint = playoffSerie.getContainer().getTournamentConstraint();
        RoundConstraint roundConstraint = tournamentConstraint.getConstraintForSerie(playoffSerie.getIndex());
        this.serieDataDAO.addSerieData(roundConstraint.distance, playoffSerie.arrows.size(), SerieData.TrainingType.PLAYOFF);
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
    protected ISerie createNewSerie() {
        return this.playoffDAO.createSerie((Playoff) this.serie.getContainer());
    }

    @Override
    protected boolean canAddArrowImpact() {
        return this.serie.getArrows().size() < MAX_ARROWS_ON_PLAYOFF_SERIE;
    }

    @Override
    protected int getMaxSerieScore() {
        RoundConstraint roundConstraint = this.serie.getContainer().getTournamentConstraint().getConstraintForSerie(this.serie.getIndex());
        return roundConstraint.maxScore * MAX_ARROWS_ON_PLAYOFF_SERIE;
    }

    @Override
    protected boolean canActivateButtons() {
        PlayoffSerie playoffSerie = (PlayoffSerie) this.serie;
        if (this.serie.getArrows().size() < MAX_ARROWS_ON_PLAYOFF_SERIE) {
            return false;
        }

        if (playoffSerie.playoff.computerPlayOffConfiguration != null) {
            return true;
        } else {
            // validates that the user has input the opponent score when not using computer
            String opponentScore = this.opponentScoreEdit.getText().toString();
            String opponentScoreError = this.validateNumber(opponentScore, 0, this.getMaxSerieScore());
            if (! StringUtils.isEmpty(opponentScoreError)) {
                this.opponentScoreEdit.setError(opponentScoreError);
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
