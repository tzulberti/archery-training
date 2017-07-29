package ar.com.tzulberti.archerytraining;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ar.com.tzulberti.archerytraining.dao.BowDAO;
import ar.com.tzulberti.archerytraining.dao.PlayoffDAO;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.dao.TournamentConstraintDAO;
import ar.com.tzulberti.archerytraining.dao.TournamentDAO;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;
import ar.com.tzulberti.archerytraining.fragments.MainFragment;
import ar.com.tzulberti.archerytraining.fragments.bow.ViewExistingBowsActivity;
import ar.com.tzulberti.archerytraining.fragments.playoff.ViewExistingPlayoffActivity;
import ar.com.tzulberti.archerytraining.fragments.retentions.ConfigureRetentionActivity;
import ar.com.tzulberti.archerytraining.fragments.series.ViewRawDataActivity;
import ar.com.tzulberti.archerytraining.fragments.tournament.ViewExistingTournamentsFragments;
import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import ar.com.tzulberti.archerytraining.helper.AppCache;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SerieDataDAO serieDataDAO;
    private TournamentDAO tournamentDAO;
    private PlayoffDAO playoffDAO;
    private BowDAO bowDAO;
    private TournamentConstraintDAO tournamentConstraintDAO;
    private BaseClickableFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context ctx = this.getApplicationContext();
        Sentry.init(
                "https://69c3b4db545e4a45bd10bbfe386cc5f8:59cbfb7b868d4a9fa86815ce82e4817b@sentry.io/179518",
                new AndroidSentryClientFactory(ctx)
        );


        this.currentFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, this.currentFragment)
                .commit();

        this.databaseHelper = new DatabaseHelper(this);
        this.serieDataDAO = new SerieDataDAO(this.databaseHelper);
        this.tournamentDAO = new TournamentDAO(this.databaseHelper);
        this.playoffDAO = new PlayoffDAO(this.databaseHelper);
        this.bowDAO = new BowDAO(this.databaseHelper);
        this.tournamentConstraintDAO = new TournamentConstraintDAO(this.databaseHelper);
        AppCache.initialize(this.tournamentConstraintDAO);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (this.currentFragment != null) {
            if (this.currentFragment.canGoBack()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }


    public SerieDataDAO getSerieDAO() {
        return this.serieDataDAO;
    }

    public TournamentDAO getTournamentDAO() { return this.tournamentDAO; }

    public PlayoffDAO getPlayoffDAO() { return this.playoffDAO; }

    public BowDAO getBowDAO() { return this.bowDAO; }

    public void goToMainOption(View selectedOption) {
        int id = selectedOption.getId();

        this.currentFragment = null;
        if (id == R.id.main_activity_series) {
            this.currentFragment = new ViewRawDataActivity();
        } else if (id == R.id.main_activity_today_retentions) {
            this.currentFragment = new ConfigureRetentionActivity();
        } else if (id == R.id.main_activity_today_tournament) {
            this.currentFragment = new ViewExistingTournamentsFragments();
        } else if (id == R.id.main_activity_playoff) {
            this.currentFragment = new ViewExistingPlayoffActivity();
        } else if (id == R.id.main_activity_bow) {
            this.currentFragment = new ViewExistingBowsActivity();
        } else {
            throw new RuntimeException("Unknown selected menu option");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .replace(R.id.container, this.currentFragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
