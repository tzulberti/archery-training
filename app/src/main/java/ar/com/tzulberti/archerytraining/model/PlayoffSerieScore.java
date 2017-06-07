package ar.com.tzulberti.archerytraining.model;

/**
 * Used to indicate who won the playoff serie, and the points that it should up.
 *
 * This doesn't have information about the real score but it will indicates who won
 * the serie and add its corresponding points so there are 3 possible values:
 * 2-0, 1-1, 0-2
 *
 * Created by tzulberti on 6/7/17.
 */
public class PlayoffSerieScore {

    public int userPoints;
    public int opponentPoints;
}
