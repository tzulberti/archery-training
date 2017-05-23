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
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;

/**
 * Created by tzulberti on 5/19/17.
 */

public class ViewTournamentSeriesFragment extends BaseTournamentFragment {

    public Tournament tournament;
    private TableLayout rawDataTableLayout;

    public static ViewTournamentSeriesFragment newInstance(Tournament tournament) {
        ViewTournamentSeriesFragment fragment = new ViewTournamentSeriesFragment();
        fragment.tournament = tournament;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tournament_view_series, container, false);
        this.setObjects();

        final MainActivity activity = (MainActivity) getActivity();
        final ViewTournamentSeriesFragment self = this;


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {
                // make sure that the user can add another serie to this tournament
                TournamentSerie tournamentSerie = self.tournamentDAO.createNewSerie(self.tournament);
                if (tournamentSerie == null) {
                    // TODO show message to the user
                    System.err.println("TODO XXX PENDING: el usuario no deberia ver el boton en este caso");
                    return ;
                }

                ViewSerieInformationFragment practiceTestingFragment = ViewSerieInformationFragment.createInstance(tournamentSerie, true);
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
        List<TournamentSerie> exitingTournaments = this.tournamentDAO.getTournamentSeriesInformation(this.tournament.id);

        Context context = getContext();
        for (TournamentSerie data : exitingTournaments) {
            TableRow tr = new TableRow(context);

            TextView indexText = new TextView(context);
            TextView totalText = new TextView(context);
            TextView datetimeText = new TextView(context);

            Button removeButton = new Button(context);

            indexText.setText(String.valueOf(data.index + 1));
            totalText.setText(String.valueOf(data.totalScore));

            removeButton.setText("Delete");
            removeButton.setId((int) data.id);
            removeButton.setOnClickListener(this);

            tr.addView(indexText);
            tr.addView(totalText);
            for (TournamentSerieArrow arrow : data.arrows) {
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
