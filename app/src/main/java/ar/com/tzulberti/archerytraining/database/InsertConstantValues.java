package ar.com.tzulberti.archerytraining.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ar.com.tzulberti.archerytraining.model.constrains.RoundConstraint;
import ar.com.tzulberti.archerytraining.model.constrains.TournamentConstraint;

/**
 * Used to insert all the fixture values into the database.
 *
 * Created by tzulberti on 7/19/17.
 */
public class InsertConstantValues {


    public void insertAllFixtureData(SQLiteDatabase db) {
        this.insertRoundConstrainInformation(db);
        this.insertTournamentConstrainsData(db);
    }

    boolean checkIfExists(SQLiteDatabase db, String tableName, long id ) {
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + tableName + " WHERE id = ?",
                new String[] {String.valueOf(id)}
        );
        boolean res = cursor.getCount() > 0;
        cursor.close();
        return res;
    }

    private void insertRoundConstrainInformation(SQLiteDatabase db) {
        List<RoundConstraint> roundConstraints = new ArrayList<>();
        // recurve outdoor
        roundConstraints.add(new RoundConstraint(1, 70, 6, 6, 10, 1, "complete_archery_target.png"));
        roundConstraints.add(new RoundConstraint(2, 60, 6, 6, 10, 1, "complete_archery_target.png"));
        roundConstraints.add(new RoundConstraint(3, 50, 6, 6, 10, 1, "complete_archery_target.png"));
        roundConstraints.add(new RoundConstraint(4, 30, 6, 6, 10, 1, "complete_archery_target.png"));
        roundConstraints.add(new RoundConstraint(5, 20, 6, 6, 10, 1, "complete_archery_target.png"));

        // compound outdoor
        roundConstraints.add(new RoundConstraint(6, 50, 6, 6, 10, 5, "reduced_outdoor_target.png"));
        roundConstraints.add(new RoundConstraint(7, 30, 6, 6, 10, 5, "reduced_outdoor_target.png"));
        roundConstraints.add(new RoundConstraint(8, 20, 6, 6, 10, 5, "reduced_outdoor_target.png"));

        roundConstraints.add(new RoundConstraint(9, 18, 10, 3, 10, 6, "triple_spot_target.png"));
        // Has the information of the target 80cm, 40cm
        roundConstraints.add(new RoundConstraint(10, 18, 10, 3, 10, 1, "complete_archery_target.png"));
        roundConstraints.add(new RoundConstraint(11, 12, 10, 3, 10, 1, "complete_archery_target.png"));


        for (RoundConstraint roundConstraint : roundConstraints) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(RoundConstraint.ID_COLUMN_NAME, roundConstraint.id);
            contentValues.put(RoundConstraint.ARROWS_PER_SERIES_COLUMN_NAME, roundConstraint.arrowsPerSeries);
            contentValues.put(RoundConstraint.DISTANCE_COLUMN_NAME, roundConstraint.distance);
            contentValues.put(RoundConstraint.MIN_SCORE_COLUMN_NAME, roundConstraint.minScore);
            contentValues.put(RoundConstraint.MAX_SCORE_COLUMN_NAME, roundConstraint.maxScore);
            contentValues.put(RoundConstraint.SERIES_PER_ROUND_COLUMN_NAME, roundConstraint.seriesPerRound);
            contentValues.put(RoundConstraint.TARGET_IMAGE_COLUMN_NAME, roundConstraint.targetImage);


            if (this.checkIfExists(db, RoundConstraint.TABLE_NAME, roundConstraint.id)) {
                db.update(RoundConstraint.TABLE_NAME,
                        contentValues,
                        RoundConstraint.ID_COLUMN_NAME + " = ?",
                        new String[] {String.valueOf(roundConstraint.id)}
                );
            } else {
                db.insertOrThrow(RoundConstraint.TABLE_NAME, null, contentValues);
            }
        }

    }

    private void insertTournamentConstrainsData(SQLiteDatabase db) {
        List<TournamentConstraint> constraints = new ArrayList<>();
        constraints.add(new TournamentConstraint(1, "Recurve - FITA 70x70 - 70m", true, "recuve_70_70_70m", 1, 1, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(20, "Recurve - FITA 70x70 - 60m", true, "recurve_70_70_60m", 2, 2, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(30, "Compound - FITA 70x70 - 50m", true, "compound_70_70_50m", 6, 6, 0, 0, 0, 0));


        constraints.add(new TournamentConstraint(35, "Recurve - School - FITA 70x70 - 60m", true, "recurve_70_70_50m", 2, 2, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(40, "Recurve - School - FITA 70x70 - 50m", true, "recurve_70_70_50m", 3, 3, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(50, "Recurve - School - FITA 70x70 - 30m", true, "recurve_70_70_30m", 4, 4, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(60, "Recurve - School - FITA 70x70 - 20m", true, "recurve_70_70_20m", 5, 5, 0, 0, 0, 0));


        constraints.add(new TournamentConstraint(1000, "Recurve - Indoor - 18m - Triple Spot", false, "18_Triple_Spot", 9, 9, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(1001, "Compound - Indoor - 18m - Triple Spot", false, "18_Triple_Spot", 9, 9, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(1010, "Indoor - School - 18m - 40cm", false, "18_40", 10, 10, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(1020, "Indoor - School - 18m - 60cm", false, "18_60", 10, 10, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(1030, "Indoor - School - 18m - 80cm", false, "18_80", 10, 10, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(1040, "Indoor - School - 12m - 80cm", false, "12_80", 11, 11, 0, 0, 0, 0));


        for (TournamentConstraint tournamentConstraint : constraints) {
            // check if the value exists and if that is the case then update it
            ContentValues contentValues = new ContentValues();
            contentValues.put(TournamentConstraint.ID_COLUMN_NAME, tournamentConstraint.id);
            contentValues.put(TournamentConstraint.NAME_COLUMN_NAME, tournamentConstraint.name);
            contentValues.put(TournamentConstraint.IS_OUTDOOR_COLUMN_NAME, tournamentConstraint.isOutdoor ? 1 : 0);
            contentValues.put(TournamentConstraint.STRING_XML_KEY_COLUMN_NAME, tournamentConstraint.stringXMLKey);
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_1_ID_COLUMN_NAME, tournamentConstraint.roundContraint1Id);
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_2_ID_COLUMN_NAME, tournamentConstraint.roundContraint2Id > 0 ? tournamentConstraint.roundContraint2Id : null);
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_3_ID_COLUMN_NAME, tournamentConstraint.roundContraint3Id > 0 ? tournamentConstraint.roundContraint3Id : null);
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_4_ID_COLUMN_NAME, tournamentConstraint.roundContraint4Id > 0 ? tournamentConstraint.roundContraint4Id : null);
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_5_ID_COLUMN_NAME, tournamentConstraint.roundContraint5Id > 0 ? tournamentConstraint.roundContraint5Id : null);
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_6_ID_COLUMN_NAME, tournamentConstraint.roundContraint6Id > 0 ? tournamentConstraint.roundContraint6Id : null);

            if (this.checkIfExists(db, TournamentConstraint.TABLE_NAME, tournamentConstraint.id)) {
                db.update(TournamentConstraint.TABLE_NAME,
                        contentValues,
                        TournamentConstraint.ID_COLUMN_NAME + " = ?",
                        new String[] {String.valueOf(tournamentConstraint.id)}
                        );
            } else {
                db.insertOrThrow(TournamentConstraint.TABLE_NAME, null, contentValues);
            }
        }
    }
}
