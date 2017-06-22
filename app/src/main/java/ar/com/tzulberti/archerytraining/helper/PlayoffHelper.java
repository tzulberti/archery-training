package ar.com.tzulberti.archerytraining.helper;

import ar.com.tzulberti.archerytraining.model.playoff.PlayoffSerieScore;

/**
 * Created by tzulberti on 6/7/17.
 */

public class PlayoffHelper {

    public static PlayoffSerieScore getScore(int userArrowsScore, int opponentArrowsScore) {
        int opponentScore = 0;
        int userScore = 0;
        if (opponentArrowsScore > userArrowsScore) {
            opponentScore = 2;
        } else if (opponentArrowsScore < userArrowsScore) {
            userScore = 2;
        } else {
            opponentScore = 1;
            userScore = 1;
        }
        PlayoffSerieScore serieScore = new PlayoffSerieScore();
        serieScore.userPoints = userScore;
        serieScore.opponentPoints = opponentScore;
        return serieScore;
    }
}
