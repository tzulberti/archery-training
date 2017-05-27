package ar.com.tzulberti.archerytraining.helper;

import android.graphics.Color;

/**
 * Created by tzulberti on 5/23/17.
 */

public class TournamentHelper {


    public static final String getUserScore(int score, boolean isX) {
        if (score == 0) {
            return "M";
        } else if (isX) {
            return "X";
        } else if (score < 10) {
            return String.valueOf(score);
        } else {
            return String.valueOf(score);
        }
    }

    public static final int getBackground(int score) {
        int res = 0;
        switch (score) {
            case 10 : case 9:
                res = Color.YELLOW;
                break;
            case 8 : case 7:
                res = Color.RED;
                break;
            case 6 : case 5:
                res = Color.BLUE;
                break;
            case 4 : case 3:
                res = Color.BLACK;
                break;
            case 2 : case 1:
                res = Color.WHITE;
                break;
            case 0:
                res = Color.BLACK;
                break;
        }
        return res;
    }

    public static final int getFontColor(int score) {
        if (score == 0 || score == 3 || score == 4) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }
}
