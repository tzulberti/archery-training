package ar.com.tzulberti.archerytraining.fragments.bow;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.dao.BowDAO;
import ar.com.tzulberti.archerytraining.fragments.common.AbstractTableDataFragment;
import ar.com.tzulberti.archerytraining.fragments.playoff.ViewPlayoffSeriesFragment;
import ar.com.tzulberti.archerytraining.model.bow.Bow;

/**
 * Created by tzulberti on 6/12/17.
 */

public class ViewExistingBowsFragment extends AbstractTableDataFragment {

    private BowDAO bowDAO;

    @Override
    protected void setViewObjects() {
        MainActivity activity = (MainActivity) getActivity();
        this.bowDAO = activity.getBowDAO();
    }

    /**
     * A row was selected so it must show the bow details
     * @param v the id of the row that was clicked which is the id of the bow
     */
    @Override
    public void handleClick(View v) {
        long bowId = v.getId();
        Bow bow = this.bowDAO.getBowsInformation(bowId).get(0);
        Bundle arguments = new Bundle();
        arguments.putSerializable(ViewBowFragment.BOW_ARGUMENT_KEY, bow);

        ViewBowFragment viewBowFragment = new ViewBowFragment();
        viewBowFragment.setArguments(arguments);
        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .replace(R.id.container, viewBowFragment);

        fragmentTransaction.commit();
    }

    @Override
    protected void addButtonsBeforeData(TableLayout tableLayout, Context context) {
    }

    @Override
    protected void addButtonsAfterData(TableLayout tableLayout, Context context) {
    }

    @Override
    protected void renderRow(Serializable data, TableRow tr, Context context) {
        Bow bow = (Bow) data;
        TextView bowName = new TextView(context);
        bowName.setText(bow.name);
        bowName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        tr.addView(bowName);
    }

    @Override
    protected List<Bow> getData() {
        return this.bowDAO.getBowsInformation(-1);
    }

    @Override
    protected void addNewValue() {
        ViewBowFragment viewBowFragment = new ViewBowFragment();
        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .replace(R.id.container, viewBowFragment);

        fragmentTransaction.commit();
    }
}
