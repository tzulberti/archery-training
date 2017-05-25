package ar.com.tzulberti.archerytraining.model.tournament;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tzulberti on 5/17/17.
 */

public class Tournament {

    public long id;
    public String name;
    public Date datetime;
    public int distance;
    public int targetSize;
    public boolean isOutdoor;
    public boolean isTournament;
    public int totalScore;

    public List<TournamentSerie> series;

    public Tournament(long id, String name, Date datetime) {
        this.id = id;
        this.name = name;
        this.datetime = datetime;
        this.series = new ArrayList<>();
    }
}
