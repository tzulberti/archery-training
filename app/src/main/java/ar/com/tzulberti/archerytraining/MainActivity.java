package ar.com.tzulberti.archerytraining;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ar.com.tzulberti.archerytraining.dao.BowDAO;
import ar.com.tzulberti.archerytraining.dao.PlayoffDAO;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.dao.TournamentDAO;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;
import ar.com.tzulberti.archerytraining.fragments.MainFragment;
import ar.com.tzulberti.archerytraining.fragments.bow.ViewExistingBowsFragment;
import ar.com.tzulberti.archerytraining.fragments.playoff.ViewExistingPlayoffFragment;
import ar.com.tzulberti.archerytraining.fragments.retentions.ConfigureRetention;
import ar.com.tzulberti.archerytraining.fragments.series.ViewRawDataFragment;
import ar.com.tzulberti.archerytraining.fragments.tournament.ViewExistingTournamentsFragments;
import ar.com.tzulberti.archerytraining.database.DatabaseHelper;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseHelper databaseHelper;
    private SerieDataDAO serieDataDAO;
    private TournamentDAO tournamentDAO;
    private PlayoffDAO playoffDAO;
    private BowDAO bowDAO;
    private BaseClickableFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Context ctx = this.getApplicationContext();
        Sentry.init(
                "https://69c3b4db545e4a45bd10bbfe386cc5f8:59cbfb7b868d4a9fa86815ce82e4817b@sentry.io/179518",
                new AndroidSentryClientFactory(ctx)
        );

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        this.currentFragment = null;
        if (id == R.id.nav_today_serie) {
            this.currentFragment = new ViewRawDataFragment();
        } else if (id == R.id.nav_retentions) {
            this.currentFragment = new ConfigureRetention();
        } else if (id == R.id.nav_tournament) {
            this.currentFragment = new ViewExistingTournamentsFragments();
        }  else if (id == R.id.nav_playoff) {
            this.currentFragment = new ViewExistingPlayoffFragment();
        } else if (id == R.id.nav_bow) {
            this.currentFragment = new ViewExistingBowsFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, this.currentFragment)
                .commit();
        return true;
    }

    public void goToFragment(View selectedOption) {
        int id = selectedOption.getId();

        this.currentFragment = null;
        if (id == R.id.main_activity_series) {
            this.currentFragment = new ViewRawDataFragment();
        } else if (id == R.id.main_activity_today_retentions) {
            this.currentFragment = new ConfigureRetention();
        } else if (id == R.id.main_activity_today_tournament) {
            this.currentFragment = new ViewExistingTournamentsFragments();
        } else if (id == R.id.main_activity_playoff) {
            this.currentFragment = new ViewExistingPlayoffFragment();
        } else if (id == R.id.main_activity_bow) {
            this.currentFragment = new ViewExistingBowsFragment();
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


    public SerieDataDAO getSerieDAO() {
        return this.serieDataDAO;
    }

    public TournamentDAO getTournamentDAO() { return this.tournamentDAO; }

    public PlayoffDAO getPlayoffDAO() { return this.playoffDAO; }

    public BowDAO getBowDAO() { return this.bowDAO; }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment == null) {
            this.currentFragment = null;
        } else {
            this.currentFragment = (BaseClickableFragment) fragment;
        }
    }

    public void onClick(View v) {
        this.currentFragment.handleClick(v);
    }

}
