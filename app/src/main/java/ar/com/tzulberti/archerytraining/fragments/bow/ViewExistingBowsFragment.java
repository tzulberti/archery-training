package ar.com.tzulberti.archerytraining.fragments.bow;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

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
    }

    @Override
    protected void addButtonsBeforeData(TableLayout tableLayout, Context context) {
    }

    @Override
    protected void addButtonsAfterData(TableLayout tableLayout, Context context) {
    }

    @Override
    protected void renderRow(Serializable data, TableRow tr, Context context) {

    }

    @Override
    protected List<Bow> getData() {
        return this.bowDAO.getBowsInformation();
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
