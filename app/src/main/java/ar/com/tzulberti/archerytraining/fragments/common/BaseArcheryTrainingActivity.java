package ar.com.tzulberti.archerytraining.fragments.common;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import ar.com.tzulberti.archerytraining.dao.BowDAO;
import ar.com.tzulberti.archerytraining.dao.PlayoffDAO;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.dao.TournamentConstraintDAO;
import ar.com.tzulberti.archerytraining.dao.TournamentDAO;
import ar.com.tzulberti.archerytraining.database.DatabaseHelper;

/**
 * Created by tzulberti on 7/29/17.
 */

public abstract class BaseArcheryTrainingActivity extends AppCompatActivity {

    protected DatabaseHelper databaseHelper;
    protected SerieDataDAO serieDataDAO;
    protected TournamentDAO tournamentDAO;
    protected PlayoffDAO playoffDAO;
    protected BowDAO bowDAO;
    protected TournamentConstraintDAO tournamentConstraintDAO;

    protected void createDAOs() {
        this.databaseHelper = new DatabaseHelper(this);
        this.serieDataDAO = new SerieDataDAO(this.databaseHelper);
        this.tournamentDAO = new TournamentDAO(this.databaseHelper);
        this.playoffDAO = new PlayoffDAO(this.databaseHelper);
        this.bowDAO = new BowDAO(this.databaseHelper);
        this.tournamentConstraintDAO = new TournamentConstraintDAO(this.databaseHelper);
    }

    protected void cleanState() {
    }

    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
