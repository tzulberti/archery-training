package ar.com.tzulberti.archerytraining.dao;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.model.constrains.RoundConstraint;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;


/**
 * Dao used to get the information for tournament constraints and round constraints
 *
 * Created by tzulberti on 7/19/17.
 */

public class ConstraintsDAO extends BaseDAO{

    public ConstraintsDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper);
    }


    public Map<Integer, RoundConstraint> getRoundConstraints() {

        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " +
                    RoundConstraint.ID_COLUMN_NAME + ", " +
                        RoundConstraint.DISTANCE_COLUMN_NAME + ", " +
                        RoundConstraint.SERIES_PER_ROUND_COLUMN_NAME + ", " +
                        RoundConstraint.ARROWS_PER_SERIES_COLUMN_NAME + ", " +
                        RoundConstraint.MAX_SCORE_COLUMN_NAME + ", " +
                        RoundConstraint.MIN_SCORE_COLUMN_NAME + ", " +
                        RoundConstraint.TARGET_IMAGE_COLUMN_NAME + " " +
                "FROM " +  RoundConstraint.TABLE_NAME,
                null
        );

        Map<Integer, RoundConstraint> res = new HashMap<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            res.put(id,
                    new RoundConstraint(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getInt(3),
                            cursor.getInt(4),
                            cursor.getInt(5),
                            cursor.getString(6)
                    )
            );
        }
        cursor.close();

        return res;
    }

    public List<TournamentConstraint> getTournamentConstraints(Map<Integer, RoundConstraint> roundConstraintMap) {
        List<TournamentConstraint> res = new ArrayList<>();
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " +
                    TournamentConstraint.ID_COLUMN_NAME + ", " +
                        TournamentConstraint.NAME_COLUMN_NAME + ", " +
                        TournamentConstraint.IS_OUTDOOR_COLUMN_NAME + ", " +
                        TournamentConstraint.STRING_XML_KEY_COLUMN_NAME + ", " +
                        TournamentConstraint.ROUND_CONSTRAINT_1_ID_COLUMN_NAME + ", " +
                        TournamentConstraint.ROUND_CONSTRAINT_2_ID_COLUMN_NAME + ", " +
                        TournamentConstraint.ROUND_CONSTRAINT_3_ID_COLUMN_NAME + ", " +
                        TournamentConstraint.ROUND_CONSTRAINT_4_ID_COLUMN_NAME + ", " +
                        TournamentConstraint.ROUND_CONSTRAINT_5_ID_COLUMN_NAME + ", " +
                        TournamentConstraint.ROUND_CONSTRAINT_6_ID_COLUMN_NAME + " " +
                "FROM " +  TournamentConstraint.TABLE_NAME + " " +
                "ORDER BY " + TournamentConstraint.ID_COLUMN_NAME,
                null
        );

        while (cursor.moveToNext()) {
            TournamentConstraint tournamentConstraint = new TournamentConstraint(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2) == 1,
                cursor.getString(3),
                cursor.getInt(4),
                cursor.isNull(5) ? null : cursor.getInt(5),
                cursor.isNull(6) ? null : cursor.getInt(6),
                cursor.isNull(7) ? null : cursor.getInt(7),
                cursor.isNull(8) ? null : cursor.getInt(8),
                cursor.isNull(9) ? null : cursor.getInt(9)
            );
            res.add(tournamentConstraint);

            Integer[] constrainsIds = new Integer[] {
                tournamentConstraint.roundContraint1Id,
                tournamentConstraint.roundContraint2Id,
                tournamentConstraint.roundContraint3Id,
                tournamentConstraint.roundContraint4Id,
                tournamentConstraint.roundContraint5Id,
                tournamentConstraint.roundContraint6Id,
            };
            for (Integer roundConstraintId : constrainsIds) {
                if (roundConstraintId == null ) {
                    break;
                }
                tournamentConstraint.roundConstraintList.add(roundConstraintMap.get(roundConstraintId));
            }

        }
        cursor.close();

        return res;
    }
}
