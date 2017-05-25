package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;

/**
 * Created by tzulberti on 5/19/17.
 */

public class ViewTournamentSeriesFragment extends BaseTournamentFragment {

    public Tournament tournament;
    private TableLayout dataContainer;

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

                ViewSerieInformationFragment practiceTestingFragment = ViewSerieInformationFragment.createInstance(tournamentSerie);
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, practiceTestingFragment)
                        .commit();
            }
        });


        this.dataContainer = (TableLayout) view.findViewById(R.id.tournament_series_list);
        this.showExistingSeries();
        return view;
    }

    public void showExistingSeries() {
        List<TournamentSerie> existingSeries = this.tournamentDAO.getTournamentSeriesInformation(this.tournament.id);
        this.tournament.series = existingSeries;

        Context context = getContext();
        for (TournamentSerie data : existingSeries) {
            TableRow tr = new TableRow(context);
            tr.setPadding(0, 15, 0, 15);

            TextView serieIndexText = new TextView(context);
            serieIndexText.setText("Serie " + String.valueOf(data.index));
            serieIndexText.setGravity(Gravity.LEFT);

            tr.addView(serieIndexText);

            for (TournamentSerieArrow arrowData : data.arrows) {
                TextView arrowScoreText = new TextView(context);
                arrowScoreText.setText(TournamentHelper.getUserScore(arrowData.score));
                arrowScoreText.setTextColor(TournamentHelper.getFontColor(arrowData.score));
                arrowScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                arrowScoreText.setBackgroundResource(R.drawable.rounded);
                arrowScoreText.getBackground().setColorFilter(new PorterDuffColorFilter(TournamentHelper.getBackground(arrowData.score), PorterDuff.Mode.SRC_IN));
                arrowScoreText.setPadding(10, 0, 10, 0);
                arrowScoreText.setGravity(Gravity.CENTER);
                tr.addView(arrowScoreText);
            }

            TextView totalScoreText = new TextView(context);
            totalScoreText.setText(String.valueOf(data.totalScore));
            totalScoreText.setBackgroundResource(R.drawable.rounded);
            totalScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            totalScoreText.setGravity(Gravity.CENTER);

            tr.addView(totalScoreText);
            tr.setId((int) data.index);
            tr.setOnClickListener(this);

            this.dataContainer.addView(tr);
        }
    }

    @Override
    public void handleClick(View v) {
        TournamentSerie tournamentSerie = this.tournament.series.get(v.getId() -1);
        ViewSerieInformationFragment viewSerieInformationFragment = ViewSerieInformationFragment.createInstance(tournamentSerie);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .replace(R.id.container, viewSerieInformationFragment);
        fragmentTransaction.addToBackStack("Serie " + String.valueOf(tournamentSerie.id));
        fragmentTransaction.commit();
    }
}
