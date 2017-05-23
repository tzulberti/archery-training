package ar.com.tzulberti.archerytraining.model.tournament;

import java.util.List;

/**
 * Created by tzulberti on 5/22/17.
 */

public class TournamentSerie {

    public long id;
    public int index;
    public int totalScore;
    public List<TournamentSerieArrow> arrows;
    public Tournament tournament;

    public boolean isCompleted() {
        if (this.tournament.isOutdoor && this.arrows.size() == 6) {
            return true;
        } else if (this.arrows.size() == 3) {
            return true;
        } else {
            return false;
        }
    }
}
