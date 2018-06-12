package ar.com.tzulberti.archerytraining.activities.common;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;


import com.google.firebase.analytics.FirebaseAnalytics;

import org.apache.commons.lang3.StringUtils;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.dao.BowDAO;
import ar.com.tzulberti.archerytraining.dao.PlayoffDAO;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.dao.ConstraintsDAO;
import ar.com.tzulberti.archerytraining.dao.TournamentDAO;
import ar.com.tzulberti.archerytraining.database.DatabaseHelper;

/**
 * Base class that should be used by all the other activities
 *
 * Created by tzulberti on 7/29/17.
 */
public abstract class BaseArcheryTrainingActivity extends AppCompatActivity {

    protected DatabaseHelper databaseHelper;
    protected SerieDataDAO serieDataDAO;
    protected TournamentDAO tournamentDAO;
    protected PlayoffDAO playoffDAO;
    protected BowDAO bowDAO;
    protected ConstraintsDAO constraintsDAO;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.createDAOs();
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    protected void createDAOs() {
        this.databaseHelper = new DatabaseHelper(this);
        this.serieDataDAO = new SerieDataDAO(this.databaseHelper);
        this.tournamentDAO = new TournamentDAO(this.databaseHelper);
        this.playoffDAO = new PlayoffDAO(this.databaseHelper);
        this.bowDAO = new BowDAO(this.databaseHelper);
        this.constraintsDAO = new ConstraintsDAO(this.databaseHelper);
    }


    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * @return indicates if there should be shown a help window for the current activity
     */
    protected boolean shouldShowHelp() {
        return false;
    }

    /**
     * @return the help text that should be shown on the current window
     */
    protected String helpText() {
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.shouldShowHelp()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.help_button) {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.help_dialog, null);
            WebView helpContent = (WebView) promptsView.findViewById(R.id.help_text_info);
            helpContent.loadData(this.helpText(), "text/html", "utf-8");


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(promptsView);
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Validates the input chosen by the user to make sure that it is a valid value
     * between the range of values selected
     *
     * @param input the input that the user did
     * @param minValue: the min value that the user might input
     * @param maxValue the max value that the user might input
     *
     * @return the error message if there was some kind of error
     */
    protected String validateNumber(String input, int minValue, int maxValue) {
        if (StringUtils.isEmpty(input)) {
            return this.getString(R.string.commonRequiredValidationError);
        }

        try {
            int res = Integer.valueOf(input);
            if (res < minValue || res > maxValue) {
                return this.getString(R.string.common_integer_value_between, minValue, maxValue);
            }
            return null;
        } catch (NumberFormatException e) {
            return this.getString(R.string.common_integer_value_between, minValue, maxValue);
        }

    }
}
