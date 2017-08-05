package ar.com.tzulberti.archerytraining.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.bow.ViewExistingBowsActivity;
import ar.com.tzulberti.archerytraining.activities.common.BaseArcheryTrainingActivity;
import ar.com.tzulberti.archerytraining.activities.playoff.ViewExistingPlayoffActivity;
import ar.com.tzulberti.archerytraining.activities.retentions.ConfigureRetentionActivity;
import ar.com.tzulberti.archerytraining.activities.series.ViewRawDataActivity;
import ar.com.tzulberti.archerytraining.activities.tournament.ViewExistingTournamentsActivity;
import ar.com.tzulberti.archerytraining.helper.AppCache;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

public class MainActivity extends BaseArcheryTrainingActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Context ctx = this.getApplicationContext();
        Sentry.init(
                "https://69c3b4db545e4a45bd10bbfe386cc5f8:59cbfb7b868d4a9fa86815ce82e4817b@sentry.io/179518",
                new AndroidSentryClientFactory(ctx)
        );

        this.createDAOs();
        AppCache.initialize(this.constraintsDAO, this.getResources(), this.getPackageName());
    }



    public void goToMainOption(View selectedOption) {
        int id = selectedOption.getId();

        Intent intent = null;
        if (id == R.id.main_activity_series) {
            intent = new Intent(this, ViewRawDataActivity.class);

        } else if (id == R.id.main_activity_today_retentions) {
            intent = new Intent(this, ConfigureRetentionActivity.class);

        } else if (id == R.id.main_activity_today_tournament) {
            intent = new Intent(this, ViewExistingTournamentsActivity.class);

        } else if (id == R.id.main_activity_playoff) {
            intent = new Intent(this, ViewExistingPlayoffActivity.class);

        } else if (id == R.id.main_activity_bow) {
            intent = new Intent(this, ViewExistingBowsActivity.class);

        } else {
            throw new RuntimeException("Unknown selected menu option");
        }

        startActivity(intent);
    }

}
