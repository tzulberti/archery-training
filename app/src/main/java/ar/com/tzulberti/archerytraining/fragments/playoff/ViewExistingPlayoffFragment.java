package ar.com.tzulberti.archerytraining.fragments.playoff;

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
import ar.com.tzulberti.archerytraining.fragments.tournament.ViewTournamentSeriesFragment;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;

/**
 * Created by tzulberti on 6/4/17.
 */

public class ViewExistingPlayoffFragment extends BasePlayoffFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        this.setObjects();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.playoff_view_existing, container, false);

        final MainActivity activity = (MainActivity) getActivity();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {
                AddPlayoffFragment addPlayoffFragment = new AddPlayoffFragment();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, addPlayoffFragment)
                        .commit();
            }
        });

        this.showInformation(view, (TableLayout) view.findViewById(R.id.playoff_list));
        return view;
    }


    private void showInformation(View view, TableLayout dataContainer) {
        List<Playoff> existingPlayoffs = this.playoffDAO.getPlayoffs();
        Context context = getContext();
        if (existingPlayoffs == null) {
            return;
        }

        for (Playoff playoff : existingPlayoffs) {
            TableRow tr = new TableRow(context);
            tr.setPadding(0, 15, 0, 15);

            ImageView imageView = new ImageView(context);
            TextView nameText = new TextView(context);
            TextView datetimeText = new TextView(context);
            TextView totalScoreText = new TextView(context);

            if (playoff.computerPlayOffConfiguration == null) {
                imageView.setImageResource(R.drawable.ic_standing_man);
            } else {
                imageView.setImageResource(R.drawable.ic_computer);
            }
            nameText.setText(playoff.name);
            nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            totalScoreText.setText(String.valueOf(playoff.userPlayoffScore) + " - " + String.valueOf(playoff.opponentPlayoffScore));
            totalScoreText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            datetimeText.setText(DatetimeHelper.DATE_FORMATTER.format(playoff.datetime));

            tr.addView(imageView);
            tr.addView(nameText);
            tr.addView(totalScoreText);
            tr.addView(datetimeText);
            tr.setId((int) playoff.id);
            tr.setOnClickListener(this);

            dataContainer.addView(tr);
        }
    }

    @Override
    public void handleClick(View v) {
        int tournamentId = v.getId();
        Bundle bundle = new Bundle();
        bundle.putLong("tournamentId", tournamentId);

        ViewTournamentSeriesFragment tournamentSeriesFragment = new ViewTournamentSeriesFragment();
        tournamentSeriesFragment.setArguments(bundle);

        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .replace(R.id.container, tournamentSeriesFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

}
