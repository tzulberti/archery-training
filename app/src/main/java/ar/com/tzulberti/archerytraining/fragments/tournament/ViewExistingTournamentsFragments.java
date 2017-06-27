package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;

import ar.com.tzulberti.archerytraining.dao.TournamentDAO;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractSerieArrowsFragment;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractTableDataFragment;
import ar.com.tzulberti.archerytraining.fragments.playoff.ViewPlayoffSeriesFragment;
import ar.com.tzulberti.archerytraining.fragments.playoff.ViewPlayoffStatsFragment;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentConfiguration;

/**
 * Created by tzulberti on 5/17/17.
 */

public class ViewExistingTournamentsFragments extends AbstractTableDataFragment {


    protected TournamentDAO tournamentDAO;

    @Override
    protected void setViewObjects() {
        MainActivity activity = (MainActivity) getActivity();
        this.tournamentDAO = activity.getTournamentDAO();
    }

    @Override
    protected void addButtonsBeforeData(TableLayout tableLayout, Context context) {
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        trParams.span = 4;

        Button viewPlayoffStatsButton = new Button(context);
        viewPlayoffStatsButton.setText(this.getString(R.string.stats_view));
        viewPlayoffStatsButton.setId(Integer.MAX_VALUE - 1);
        viewPlayoffStatsButton.setOnClickListener(this);
        viewPlayoffStatsButton.setLayoutParams(trParams);

        TableRow tr1 = new TableRow(context);
        tr1.addView(viewPlayoffStatsButton);
        tableLayout.addView(tr1);
    }

    @Override
    protected void addButtonsAfterData(TableLayout tableLayout, Context context) {

    }

    @Override
    protected void renderRow(Serializable data, TableRow tr, Context context) {
        Tournament tournament = (Tournament) data;
        ImageView imageView = new ImageView(context);
        TextView nameText = new TextView(context);
        TextView datetimeText = new TextView(context);
        TextView totalScoreText = new TextView(context);


        if (tournament.isTournament) {
            imageView.setImageResource(R.drawable.ic_trophy);
        } else {
            imageView.setImageResource(R.drawable.ic_bow);
        }
        nameText.setText(tournament.name);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);


        totalScoreText.setText(String.valueOf(tournament.totalScore) + "/" + String.valueOf(TournamentConfiguration.MAX_SCORE_FOR_TOURNAMENT));
        totalScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        datetimeText.setText(DatetimeHelper.DATE_FORMATTER.format(tournament.datetime));


        tr.addView(imageView);
        tr.addView(nameText);
        tr.addView(totalScoreText);
        tr.addView(datetimeText);
        tr.setId((int) tournament.id);
    }

    @Override
    protected List<? extends Serializable> getData() {
        return this.tournamentDAO.getExistingTournaments();
    }

    @Override
    protected void addNewValue() {
        AddTournamentFragment addTournamentFragment = new AddTournamentFragment();
        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, addTournamentFragment)
                .commit();
    }


    @Override
    public void handleClick(View v) {
        int tournamentId = v.getId();

        BaseClickableFragment fragment = null;
        if (tournamentId == (Integer.MAX_VALUE - 1)) {
            // the user selected to view the playoff stats
            fragment = new ViewTournamentsStatsFragment();

        } else {
            Tournament tournament = this.tournamentDAO.getTournamentInformation(tournamentId);
            this.tournamentDAO.getTournamentSeriesInformation(tournament);

            Bundle bundle = new Bundle();
            bundle.putSerializable(AbstractSerieArrowsFragment.CONTAINER_ARGUMENT_KEY, tournament);
            fragment = new ViewTournamentSeriesFragment();
            fragment.setArguments(bundle);
        }


        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}
