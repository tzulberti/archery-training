package ar.com.tzulberti.archerytraining.helper;

import android.graphics.Color;

import ar.com.tzulberti.archerytraining.R;

/**
 * Helper functions used to render the data of the score correctly
 *
 * Created by tzulberti on 5/23/17.
 */
public class TournamentHelper {

    public static final int[] ALL_COLORS = new int[]{
        Color.YELLOW,
        Color.RED,
        Color.BLUE,
        Color.BLACK,
        Color.WHITE,
        Color.GREEN
    };

    public static final Integer[] COLORS_TEXT = new Integer[]{
        R.string.stats_yellow,
        R.string.stats_red,
        R.string.stats_blue,
        R.string.stats_black,
        R.string.stats_white,
        R.string.stats_m,
    };

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
                res = Color.GREEN;
                break;
        }
        return res;
    }

    public static final int getFontColor(int score) {
        if (score == 3 || score == 4) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }
}
