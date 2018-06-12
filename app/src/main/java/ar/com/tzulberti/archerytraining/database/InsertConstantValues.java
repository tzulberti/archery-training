package ar.com.tzulberti.archerytraining.database;

import android.content.ContentValues;
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


    private void insertRoundConstrainInformation(SQLiteDatabase db) {
        List<RoundConstraint> roundConstraints = new ArrayList<RoundConstraint>();
        roundConstraints.add(new RoundConstraint(1, 70, 6, 6, 10, 1, "complete_archery_target.png"));
        roundConstraints.add(new RoundConstraint(2, 60, 6, 6, 10, 1, "complete_archery_target.png"));
        roundConstraints.add(new RoundConstraint(3, 50, 6, 6, 10, 1, "complete_archery_target.png"));
        roundConstraints.add(new RoundConstraint(4, 30, 6, 6, 10, 1, "complete_archery_target.png"));
        roundConstraints.add(new RoundConstraint(5, 20, 6, 6, 10, 1, "complete_archery_target.png"));
        roundConstraints.add(new RoundConstraint(6, 50, 6, 6, 10, 6, "reduced_outdoor_target.png"));
        roundConstraints.add(new RoundConstraint(7, 30, 6, 6, 10, 6, "reduced_outdoor_target.png"));
        roundConstraints.add(new RoundConstraint(8, 20, 6, 6, 10, 6, "reduced_outdoor_target.png"));

        roundConstraints.add(new RoundConstraint(9, 18, 10, 3, 10, 6, "triple_spot_target.png"));
        // Has the information of the target 80cm, 40cm
        roundConstraints.add(new RoundConstraint(10, 18, 10, 3, 10, 6, "complete_archery_target.png"));
        roundConstraints.add(new RoundConstraint(11, 12, 10, 3, 10, 6, "complete_archery_target.png"));


        for (RoundConstraint roundConstraint : roundConstraints) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(RoundConstraint.ID_COLUMN_NAME, roundConstraint.id);
            contentValues.put(RoundConstraint.ARROWS_PER_SERIES_COLUMN_NAME, roundConstraint.arrowsPerSeries);
            contentValues.put(RoundConstraint.DISTANCE_COLUMN_NAME, roundConstraint.distance);
            contentValues.put(RoundConstraint.MIN_SCORE_COLUMN_NAME, roundConstraint.minScore);
            contentValues.put(RoundConstraint.MAX_SCORE_COLUMN_NAME, roundConstraint.maxScore);
            contentValues.put(RoundConstraint.SERIES_PER_ROUND_COLUMN_NAME, roundConstraint.seriesPerRound);
            contentValues.put(RoundConstraint.TARGET_IMAGE_COLUMN_NAME, roundConstraint.targetImage);


            db.insertOrThrow(RoundConstraint.TABLE_NAME, null, contentValues);
        }

    }

    private void insertTournamentConstrainsData(SQLiteDatabase db) {
        List<TournamentConstraint> constraints = new ArrayList<TournamentConstraint>();
        constraints.add(new TournamentConstraint(1, "FITA 70x70 - 70m", true, "70_70_70m", 1, 1, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(20, "FITA 70x70 - 60m", true, "70_70_60m", 2, 2, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(30, "FITA 70x70 - 50m", true, "70_70_50m", 3, 3, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(40, "FITA 70x70 - 50m", true, "70_70_30m", 4, 4, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(50, "FITA 70x70 - 30m", true, "70_70_30m", 4, 4, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(60, "FITA 70x70 - 20m", true, "70_70_30m", 5, 5, 0, 0, 0, 0));


        constraints.add(new TournamentConstraint(1000, "Indoor - 18m - Triple Spot", false, "18_Triple_Spot", 9, 9, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(1010, "Indoor - 18m - 40cm", false, "18_40", 10, 10, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(1020, "Indoor - 18m - 60cm", false, "18_60", 10, 10, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(1030, "Indoor - 18m - 80cm", false, "18_80", 10, 10, 0, 0, 0, 0));
        constraints.add(new TournamentConstraint(1040, "Indoor - 12m - 80cm", false, "12_80", 11, 10, 0, 0, 0, 0));


        for (TournamentConstraint tournamentConstraint : constraints) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TournamentConstraint.ID_COLUMN_NAME, tournamentConstraint.id);
            contentValues.put(TournamentConstraint.NAME_COLUMN_NAME, tournamentConstraint.name);
            contentValues.put(TournamentConstraint.IS_OUTDOOR_COLUMN_NAME, tournamentConstraint.isOutdoor ? 1: 0);
            contentValues.put(TournamentConstraint.STRING_XML_KEY_COLUMN_NAME, tournamentConstraint.stringXMLKey);
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_1_ID_COLUMN_NAME, tournamentConstraint.roundContraint1Id);
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_2_ID_COLUMN_NAME, tournamentConstraint.roundContraint2Id > 0 ? tournamentConstraint.roundContraint2Id :  null );
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_3_ID_COLUMN_NAME, tournamentConstraint.roundContraint3Id > 0 ? tournamentConstraint.roundContraint3Id :  null );
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_4_ID_COLUMN_NAME, tournamentConstraint.roundContraint4Id > 0 ? tournamentConstraint.roundContraint4Id :  null );
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_5_ID_COLUMN_NAME, tournamentConstraint.roundContraint5Id > 0 ? tournamentConstraint.roundContraint5Id :  null );
            contentValues.put(TournamentConstraint.ROUND_CONSTRAINT_6_ID_COLUMN_NAME, tournamentConstraint.roundContraint6Id > 0 ? tournamentConstraint.roundContraint6Id :  null );


            db.insertOrThrow(TournamentConstraint.TABLE_NAME, null, contentValues);
        }
    }
}
