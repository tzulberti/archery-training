package ar.com.tzulberti.archerytraining.fragments.tournament;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.model.tournament.Tournament;
import ar.com.tzulberti.archerytraining.model.tournament.TournamentSerie;

/**
 * Created by tzulberti on 5/17/17.
 */

public class AddTournamentFragment extends BaseTournamentFragment {

    private static final int[] REQUIRED_VALUES = {
            R.id.name,
            R.id.distance,
            R.id.target_size
    };

    private View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tournament_add_new, container, false);
        this.setObjects();

        return view;
    }

    @Override
    public void handleClick(View clickButton) {
        // validate the values to make sure everything is ok
        View v = this.getView();
        for (int elementId : this.REQUIRED_VALUES) {
            EditText element = (EditText) v.findViewById(elementId);

            if (element == null) {
                System.err.println(String.format("No element found for %s, [%s, %s, %s]", elementId, this.REQUIRED_VALUES[0], this.REQUIRED_VALUES[1], this.REQUIRED_VALUES[2]));
            }
            String value = element.getText().toString();

            if (StringUtils.isBlank(value)) {
                element.setError("");
                return ;
            }
        }

        System.err.println(String.format("is_outdoor: %s", ((CheckBox) v.findViewById(R.id.is_outdoor)).isChecked()));
        Tournament tournament = this.tournamentDAO.createTournament(
                ((EditText) v.findViewById(R.id.name)).getText().toString(),
                Integer.valueOf(((EditText) v.findViewById(R.id.distance)).getText().toString()),
                Integer.valueOf(((EditText) v.findViewById(R.id.target_size)).getText().toString()),
                ((CheckBox) v.findViewById(R.id.is_outdoor)).isChecked(),
                ((CheckBox) v.findViewById(R.id.is_tournament)).isChecked()
        );


        MainActivity activity = (MainActivity) this.getActivity();
        Bundle bundle = new Bundle();
        bundle.putLong("tournamentId", tournament.id);
        bundle.putInt("creating", 1);

        ViewTournamentSeriesFragment tournamentSeriesFragment = new ViewTournamentSeriesFragment();
        tournamentSeriesFragment.setArguments(bundle);

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, tournamentSeriesFragment)
                .commit();
    }
}
