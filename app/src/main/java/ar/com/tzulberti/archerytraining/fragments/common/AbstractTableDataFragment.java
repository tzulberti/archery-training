package ar.com.tzulberti.archerytraining.fragments.common;

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

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.MainActivity;
import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.fragments.BaseClickableFragment;
import ar.com.tzulberti.archerytraining.fragments.playoff.AddPlayoffFragment;
import ar.com.tzulberti.archerytraining.fragments.playoff.ViewPlayoffSeriesFragment;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;

/**
 * Used to show existing values as a table, and to do some action over those
 *
 * Created by tzulberti on 6/12/17.
 */
public abstract class AbstractTableDataFragment extends BaseClickableFragment{

    /**
     * Used to set the objects (dao or arguments) used to show the data later on
     */
    protected abstract void setViewObjects();

    /**
     * Used to add the buttons before the table with all the data is being shown
     * @param tableLayout the container which will contain all the data
     * @param context used to create new view elements
     */
    protected abstract void addButtonsBeforeData(TableLayout tableLayout, Context context);

    /**
     * Used to add the buttons after the table with all the data is being shown
     * @param tableLayout the container which will contain all the data
     * @param context used to create new view elements
     */
    protected abstract void addButtonsAfterData(TableLayout tableLayout, Context context);


    /**
     * Renders the data into a table rows
     * @param data the current value to be render
     */
    protected abstract void renderRow(Serializable data, TableRow tr, Context context);

    /**
     * Get all the information that is going to be shown
     * @return all the data that is going to be shown
     */
    protected abstract List<? extends Serializable> getData();

    /**
     * Handler used to add new values into the database
     *
     * Used when the user selected the add button
     */
    protected abstract void addNewValue();

    /**
     * Identifies if the user can clicks on the rows of the table
     * @return
     */
    protected boolean areRowsClickable() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        this.setViewObjects();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.common_show_existing_values, container, false);

        final MainActivity activity = (MainActivity) getActivity();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        final AbstractTableDataFragment self = this;
        fab.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {
                self.addNewValue();
            }
        });

        this.showInformation(view, (TableLayout) view.findViewById(R.id.existing_data));
        return view;
    }


    private void showInformation(View view, TableLayout dataContainer) {
        List<? extends Serializable> existingData = this.getData();
        Context context = getContext();

        this.addButtonsBeforeData(dataContainer, context);
        if (existingData == null) {
            return;
        }

        boolean areRowsClickable = this.areRowsClickable();

        for (Serializable data : existingData) {
            TableRow tr = new TableRow(context);
            tr.setPadding(0, 15, 0, 15);
            this.renderRow(data, tr, context);
            if (areRowsClickable) {
                tr.setClickable(true);
                tr.setOnClickListener(this);
            }
            dataContainer.addView(tr);
        }

        this.addButtonsAfterData(dataContainer, context);
    }

}
