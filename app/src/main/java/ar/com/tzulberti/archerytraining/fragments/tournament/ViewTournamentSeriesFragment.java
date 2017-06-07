package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractSerieArrowsFragment;
import ar.com.tzulberti.archerytraining.helper.TournamentHelper;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentConfiguration;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerieArrow;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;

/**
 * Created by tzulberti on 5/19/17.
 */

public class ViewTournamentSeriesFragment extends BaseTournamentFragment {

    public Tournament tournament;
    private TableLayout dataContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.cleanState(container);
        View view = inflater.inflate(R.layout.tournament_view_series, container, false);
        this.setObjects();

        this.tournament = this.getTournamentArgument();

        final MainActivity activity = (MainActivity) this.getActivity();
        final ViewTournamentSeriesFragment self = this;


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {
                // make sure that the user can add another serie to this tournament
                TournamentSerie tournamentSerie = self.tournamentDAO.createNewSerie(self.tournament);
                if (tournamentSerie == null) {
                    // TODO show message to the user
                    return ;
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable(ViewSerieInformationFragment.SERIE_ARGUMENT_KEY, tournamentSerie);
                ViewSerieInformationFragment viewSerieInformationFragment = new ViewSerieInformationFragment();
                viewSerieInformationFragment.setArguments(bundle);

                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, viewSerieInformationFragment)
                        .commit();
            }
        });


        this.dataContainer = (TableLayout) view.findViewById(R.id.tournament_series_list);
        this.showExistingSeries();
        return view;
    }

    public void showExistingSeries() {


        if (this.getArguments().containsKey("creating")) {
            this.getArguments().remove("creating");
            TournamentSerie tournamentSerie = this.tournamentDAO.createNewSerie(tournament);

            Bundle bundle = new Bundle();
            bundle.putSerializable(ViewSerieInformationFragment.SERIE_ARGUMENT_KEY, tournamentSerie);
            ViewSerieInformationFragment viewSerieInformationFragment = new ViewSerieInformationFragment();
            viewSerieInformationFragment.setArguments(bundle);

            FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .replace(R.id.container, viewSerieInformationFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return;
        }
        
        Context context = this.getContext();
        for (TournamentSerie data : this.tournament.series) {
            TableRow tr = new TableRow(context);
            tr.setPadding(0, 15, 0, 15);

            TextView serieIndexText = new TextView(context);
            serieIndexText.setText(getString(R.string.tournament_serie_current_serie, data.index));
            serieIndexText.setGravity(Gravity.LEFT);

            tr.addView(serieIndexText);

            for (TournamentSerieArrow arrowData : data.arrows) {
                TextView arrowScoreText = new TextView(context);
                arrowScoreText.setText(TournamentHelper.getUserScore(arrowData.score, arrowData.isX));
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

        int span = 1;
        if (! this.tournament.series.isEmpty()) {
            // the +2 is because the series index and the total score
            span = TournamentConfiguration.MAX_ARROW_PER_SERIES + 2;
        }

        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        trParams.span = span;


        boolean buttonsEnabled = ! this.tournament.series.isEmpty();
        // add the buttons to delete/view charts for the tournament
        TableRow tr1 = new TableRow(context);
        Button viewChartsButton = new Button(context);
        viewChartsButton.setId(Integer.MAX_VALUE - 1);
        viewChartsButton.setText(R.string.tournament_view_all_impacts);
        viewChartsButton.setLayoutParams(trParams);
        viewChartsButton.setOnClickListener(this);
        viewChartsButton.setEnabled(buttonsEnabled);
        tr1.addView(viewChartsButton);
        this.dataContainer.addView(tr1);

        TableRow tr2 = new TableRow(context);
        Button viewScoreSheetButton = new Button(context);
        viewScoreSheetButton.setId(Integer.MAX_VALUE - 2);
        viewScoreSheetButton.setText(R.string.tournament_view_score_sheet);
        viewScoreSheetButton.setLayoutParams(trParams);
        viewScoreSheetButton.setOnClickListener(this);
        viewScoreSheetButton.setEnabled(buttonsEnabled);
        tr2.addView(viewScoreSheetButton);
        this.dataContainer.addView(tr2);


        TableRow tr3 = new TableRow(context);
        Button viewTournamentArrowStats = new Button(context);
        viewTournamentArrowStats.setId(Integer.MAX_VALUE - 3);
        viewTournamentArrowStats.setText(R.string.tournament_view_arrow_stats);
        viewTournamentArrowStats.setLayoutParams(trParams);
        viewTournamentArrowStats.setOnClickListener(this);
        viewTournamentArrowStats.setEnabled(buttonsEnabled);
        tr3.addView(viewTournamentArrowStats);
        this.dataContainer.addView(tr3);

        TableRow trN = new TableRow(context);
        Button deleteButton = new Button(context);
        deleteButton.setId(Integer.MAX_VALUE - 15);
        deleteButton.setText(R.string.tournament_view_tournament_delete);
        deleteButton.setLayoutParams(trParams);
        deleteButton.setOnClickListener(this);
        deleteButton.getBackground().setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
        trN.addView(deleteButton);
        this.dataContainer.addView(trN);

    }

    @Override
    public void handleClick(View v) {
        int id = v.getId();
        Context context = this.getContext();
        final MainActivity activity = (MainActivity) this.getActivity();

        if (id == Integer.MAX_VALUE - 1 || id == Integer.MAX_VALUE - 2 || id == Integer.MAX_VALUE -3 ) {
            // selected the option to view all impacts for the current tournament
            Bundle bundle = new Bundle();
            bundle.putSerializable(AbstractSerieArrowsFragment.CONTAINER_ARGUMENT_KEY, this.tournament);

            BaseTournamentFragment fragment = null;
            if (id == Integer.MAX_VALUE - 1) {
                fragment = new ViewAllTournamentTargetArrowFragment();
            } else if (id == Integer.MAX_VALUE - 2) {
                fragment = new ViewTournamentScoreSheetFragment();
            } else if (id == Integer.MAX_VALUE - 3) {
                fragment = new ViewTournamentArrowStatsFragment();
            }

            fragment.setArguments(bundle);

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == Integer.MAX_VALUE - 15) {
            // selected option to delete the tournament
            new AlertDialog.Builder(context)
                    .setTitle(R.string.common_confirmation_dialog_title)
                    .setMessage(R.string.tournament_confirm_delete_tournament)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            tournamentDAO.deleteTournament(tournament.id);

                            // go to the screen with all the tournaments
                            FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, new ViewExistingTournamentsFragments())
                                    .commit();

                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        } else {
            TournamentSerie tournamentSerie = this.tournament.series.get(v.getId() - 1);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ViewSerieInformationFragment.SERIE_ARGUMENT_KEY, tournamentSerie);
            ViewSerieInformationFragment viewSerieInformationFragment = new ViewSerieInformationFragment();
            viewSerieInformationFragment.setArguments(bundle);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .replace(R.id.container, viewSerieInformationFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
