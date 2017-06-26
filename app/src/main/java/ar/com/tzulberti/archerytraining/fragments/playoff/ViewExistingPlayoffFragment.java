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
import ar.com.tzulberti.archerytraining.dao.PlayoffDAO;
import ar.com.tzulberti.archerytraining.dao.SerieDataDAO;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractSerieArrowsFragment;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractTableDataFragment;
import ar.com.tzulberti.archerytraining.fragments.tournament.ViewTournamentSeriesFragment;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;

/**
 * Created by tzulberti on 6/4/17.
 */

public class ViewExistingPlayoffFragment extends AbstractTableDataFragment {

    protected PlayoffDAO playoffDAO;

    @Override
    protected void setViewObjects() {
        MainActivity activity = (MainActivity) getActivity();
        this.playoffDAO = activity.getPlayoffDAO();
    }

    @Override
    protected void addButtonsBeforeData(TableLayout tableLayout, Context context) {
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        trParams.span = 4;

        Button viewPlayoffStatsButton = new Button(context);
        viewPlayoffStatsButton.setText(this.getString(R.string.playoff_view_stats));
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
        Playoff playoff = (Playoff) data;
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
    }

    @Override
    protected List<? extends Serializable> getData() {
        return this.playoffDAO.getPlayoffs();
    }

    @Override
    protected void addNewValue() {
        AddPlayoffFragment addPlayoffFragment = new AddPlayoffFragment();
        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, addPlayoffFragment)
                .commit();
    }


    @Override
    public void handleClick(View v) {
        int playoffId = v.getId();
        BaseClickableFragment fragment = null;
        if (playoffId == (Integer.MAX_VALUE - 1)) {
            // the user selected to view the playoff stats
            fragment = new ViewPlayoffStatsFragment();

        } else {
            // the user selected to view one playoff
            Playoff playoff = this.playoffDAO.getCompletePlayoffData(playoffId);

            Bundle bundle = new Bundle();
            bundle.putSerializable(AbstractSerieArrowsFragment.CONTAINER_ARGUMENT_KEY, playoff);

            fragment = new ViewPlayoffSeriesFragment();
            fragment.setArguments(bundle);
        }

        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

    }

}
