package ar.com.tzulberti.archerytraining.helper;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.tzulberti.archerytraining.dao.TournamentConstraintDAO;
import ar.com.tzulberti.archerytraining.model.common.TournamentConstraint;


/**
 * Basic app cache used to initialize all the values
 *
 * Created by tzulberti on 7/21/17.
 */
public class AppCache {

    public static Map<Integer, TournamentConstraint> tournamentConstraintMap;
    public static Map<String, TournamentConstraint> tournamentConstraintSpinnerMap;
    public static List<String> tournamentTypes;

    public static void initialize(TournamentConstraintDAO tournamentConstraintDAO) {

        AppCache.tournamentConstraintMap = new HashMap<>();
        AppCache.tournamentConstraintSpinnerMap = new HashMap<>();
        AppCache.tournamentTypes = new ArrayList<>();


        for (TournamentConstraint tournamentConstraint : tournamentConstraintDAO.getValues()) {
            tournamentTypes.add(tournamentConstraint.name);
            tournamentConstraintSpinnerMap.put(tournamentConstraint.name, tournamentConstraint);
            tournamentConstraintMap.put(tournamentConstraint.id, tournamentConstraint);
        }

    }
}
