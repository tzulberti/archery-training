package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;

/**
 * Created by tzulberti on 5/19/17.
 */

public class ViewTournamentSeriesFragment extends BaseTournamentFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tournament_view_series, container, false);
        this.setObjects();

        final MainActivity activity = (MainActivity) getActivity();

        /*
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
        */
        return view;
    }

    @Override
    public void handleClick(View v) {
    }
}
