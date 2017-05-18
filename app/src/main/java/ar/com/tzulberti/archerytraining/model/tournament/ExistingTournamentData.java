package ar.com.tzulberti.archerytraining.model.tournament;

import java.util.Date;

/**
 * Created by tzulberti on 5/17/17.
 */

public class ExistingTournamentData {

    public int id;
    public String name;
    public Date datetime;

    public ExistingTournamentData(int id, String name, Date datetime) {
        this.id = id;
        this.name = name;
        this.datetime = datetime;
    }
}
