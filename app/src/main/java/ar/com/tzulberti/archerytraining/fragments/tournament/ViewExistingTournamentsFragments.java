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
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;

/**
 * Created by tzulberti on 5/17/17.
 */

public class ViewExistingTournamentsFragments extends BaseTournamentFragment {

    private TableLayout rawDataTableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tournament_view_existing, container, false);
        this.setObjects();

        final MainActivity activity = (MainActivity) getActivity();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {
                AddTournamentFragment practiceTestingFragment = new AddTournamentFragment();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, practiceTestingFragment)
                        .commit();
            }
        });


        this.rawDataTableLayout = (TableLayout) view.findViewById(R.id.tournamentExistingValues);
        this.showInformation(view);
        return view;
    }


    @Override
    public void handleClick(View v) {
    }

    private void showInformation(View view) {
        List<Tournament> exitingTournaments = this.tournamentDAO.getExistingTournaments();

        Context context = getContext();
        for (Tournament data : exitingTournaments) {
            TableRow tr = new TableRow(context);

            TextView nameText = new TextView(context);
            TextView datetimeText = new TextView(context);

            Button removeButton = new Button(context);

            nameText.setText(data.name);
            datetimeText.setText(DatetimeHelper.DATETIME_FORMATTER.format(data.datetime));

            removeButton.setText("Delete");
            removeButton.setId((int) data.id);
            removeButton.setOnClickListener(this);

            tr.addView(nameText);
            tr.addView(datetimeText);
            tr.addView(removeButton);
            this.rawDataTableLayout.addView(tr);
        }
    }
}
