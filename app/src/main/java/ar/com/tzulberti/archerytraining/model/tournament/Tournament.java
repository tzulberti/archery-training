package ar.com.tzulberti.archerytraining.model.tournament;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.base.ISerie;
import ar.com.tzulberti.archerytraining.model.base.ISerieContainer;

/**
 * Created by tzulberti on 5/17/17.
 */

public class Tournament implements ISerieContainer, Serializable {

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

    @Override
    public List<? extends ISerie> getSeries() {
        return this.series;
    }

    @Override
    public long getId() { return this.id; }

    @Override
    public int getSerieMaxPossibleScore() {
        if (this.isOutdoor) {
            return 60;
        } else {
            return 30;
        }
    }
}
