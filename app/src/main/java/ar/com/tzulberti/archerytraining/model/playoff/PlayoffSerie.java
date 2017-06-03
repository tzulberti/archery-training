package ar.com.tzulberti.archerytraining.model.playoff;

import java.util.List;

import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;

/**
 * Created by tzulberti on 6/3/17.
 */

public class PlayoffSerie {

    public long id;
    public int index;
    public int userTotalScore;
    public int opponentTotalScore;
    public List<TournamentSerieArrow> arrows;
    public Playoff playoff;
}
