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
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;

import ar.com.tzulberti.archerytraining.fragments.common.AbstractSerieArrowsFragment;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentConfiguration;

/**
 * Created by tzulberti on 5/17/17.
 */

public class ViewExistingTournamentsFragments extends BaseTournamentFragment {

    private TableLayout dataContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tournament_view_existing, container, false);
        this.setObjects();

        final MainActivity activity = (MainActivity) getActivity();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {
                AddTournamentFragment addTournamentFragment = new AddTournamentFragment();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, addTournamentFragment)
                        .commit();
            }
        });


        this.dataContainer = (TableLayout) view.findViewById(R.id.tournaments_lists);
        this.showInformation(view);
        return view;
    }


    private void showInformation(View view) {
        List<Tournament> exitingTournaments = this.tournamentDAO.getExistingTournaments();
        Context context = getContext();

        for (Tournament data : exitingTournaments) {
            TableRow tr = new TableRow(context);
            tr.setPadding(0, 15, 0, 15);

            ImageView imageView = new ImageView(context);
            TextView nameText = new TextView(context);
            TextView datetimeText = new TextView(context);
            TextView totalScoreText = new TextView(context);


            if (data.isTournament) {
                imageView.setImageResource(R.drawable.ic_trophy);
            } else {
                imageView.setImageResource(R.drawable.ic_bow);
            }
            nameText.setText(data.name);
            nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);


            totalScoreText.setText(String.valueOf(data.totalScore) + "/" + String.valueOf(TournamentConfiguration.MAX_SCORE_FOR_TOURNAMENT));
            totalScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            datetimeText.setText(DatetimeHelper.DATE_FORMATTER.format(data.datetime));


            tr.addView(imageView);
            tr.addView(nameText);
            tr.addView(totalScoreText);
            tr.addView(datetimeText);
            tr.setId((int) data.id);
            tr.setOnClickListener(this);

            this.dataContainer.addView(tr);
        }
    }

    @Override
    public void handleClick(View v) {
        int tournamentId = v.getId();

        Tournament tournament = this.tournamentDAO.getTournamentInformation(tournamentId);
        this.tournamentDAO.getTournamentSeriesInformation(tournament);

        Bundle bundle = new Bundle();
        bundle.putSerializable(AbstractSerieArrowsFragment.CONTAINER_ARGUMENT_KEY, tournament);


        ViewTournamentSeriesFragment tournamentSeriesFragment = new ViewTournamentSeriesFragment();
        tournamentSeriesFragment.setArguments(bundle);

        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .replace(R.id.container, tournamentSeriesFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}
