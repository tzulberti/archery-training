package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.tournament.ExistingTournamentData;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrowData;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieData;

/**
 * Created by tzulberti on 5/19/17.
 */

public class ViewTournamentSeriesFragment extends BaseTournamentFragment {

    public long tournamentId;
    private TableLayout rawDataTableLayout;

    public static ViewTournamentSeriesFragment newInstance(long tournamentId) {
        ViewTournamentSeriesFragment fragment = new ViewTournamentSeriesFragment();
        fragment.tournamentId = tournamentId;


        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tournament_view_series, container, false);
        this.setObjects();

        final MainActivity activity = (MainActivity) getActivity();


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {
                ViewSerieInformation practiceTestingFragment = new ViewSerieInformation();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, practiceTestingFragment)
                        .commit();
            }
        });


        this.rawDataTableLayout = (TableLayout) view.findViewById(R.id.tournamentExistingValues);
        this.showExistingSeries();
        return view;
    }

    public void showExistingSeries() {
        List<TournamentSerieData> exitingTournaments = this.tournamentDAO.getTournamentSeriesInformation(this.tournamentId);

        Context context = getContext();
        for (TournamentSerieData data : exitingTournaments) {
            TableRow tr = new TableRow(context);

            TextView indexText = new TextView(context);
            TextView totalText = new TextView(context);
            TextView datetimeText = new TextView(context);

            Button removeButton = new Button(context);

            indexText.setText(String.valueOf(data.index + 1));
            totalText.setText(String.valueOf(data.totalScore));

            removeButton.setText("Delete");
            removeButton.setId(data.id);
            removeButton.setOnClickListener(this);

            tr.addView(indexText);
            tr.addView(totalText);
            for (TournamentSerieArrowData arrow : data.arrows) {
                TextView arrowText = new TextView(context);
                arrowText.setText(String.valueOf(arrow.score));
                tr.addView(arrowText);
            }
            tr.addView(datetimeText);
            tr.addView(removeButton);
            this.rawDataTableLayout.addView(tr);
        }
    }

    @Override
    public void handleClick(View v) {
    }
}
