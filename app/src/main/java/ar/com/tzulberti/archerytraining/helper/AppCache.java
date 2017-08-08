package ar.com.tzulberti.archerytraining.helper;

import android.content.res.Resources;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.tzulberti.archerytraining.dao.ConstraintsDAO;
import ar.com.tzulberti.archerytraining.model.constrains.RoundConstraint;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;


/**
 * Basic app cache used to initialize all the values
 *
 * Created by tzulberti on 7/21/17.
 */
public class AppCache {

    public static final String TOURNAMENT_KEY_PREFIX = "tournament_";

    public static Map<Integer, TournamentConstraint> tournamentConstraintMap;
    public static Map<String, TournamentConstraint> tournamentConstraintSpinnerMap;
    public static List<String> tournamentTypes;

    public static void initialize(ConstraintsDAO constraintsDAO, Resources resources, String packageName) {

        Map<Integer, RoundConstraint> roundConstraintMap = constraintsDAO.getRoundConstraints();
        AppCache.tournamentConstraintMap = new HashMap<>();
        AppCache.tournamentConstraintSpinnerMap = new HashMap<>();
        AppCache.tournamentTypes = new ArrayList<>();

        for (TournamentConstraint tournamentConstraint : constraintsDAO.getTournamentConstraints(roundConstraintMap)) {
            int nameId = resources.getIdentifier(TOURNAMENT_KEY_PREFIX + tournamentConstraint.stringXMLKey, "string", packageName);
            String translatedName = tournamentConstraint.name;
            if (nameId > 0) {
                translatedName = resources.getString(nameId);
            }
            tournamentConstraint.translatedName = translatedName;
            tournamentTypes.add(translatedName);
            tournamentConstraintSpinnerMap.put(translatedName, tournamentConstraint);
            tournamentConstraintMap.put(tournamentConstraint.id, tournamentConstraint);
        }

    }
}
