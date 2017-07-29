package ar.com.tzulberti.archerytraining.activities.common;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.R;

/**
 * Used to show existing values as a table, and to do some action over those
 *
 * Created by tzulberti on 6/12/17.
 */
public abstract class AbstractTableDataActivity extends BaseArcheryTrainingActivity implements View.OnClickListener {

    /**
     * Used to add the buttons before the table with all the data is being shown
     * @param tableLayout the container which will contain all the data
     */
    protected abstract void addButtonsBeforeData(TableLayout tableLayout);

    /**
     * Used to add the buttons after the table with all the data is being shown
     * @param tableLayout the container which will contain all the data
     */
    protected abstract void addButtonsAfterData(TableLayout tableLayout);


    /**
     * Renders the data into a table rows
     * @param data the current value to be render
     */
    protected abstract void renderRow(Serializable data, TableRow tr);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.createDAOs();
        setContentView(R.layout.common_show_existing_values);


        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        final AbstractTableDataActivity self = this;
        fab.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {
                self.addNewValue();
            }
        });

        this.showInformation((TableLayout) this.findViewById(R.id.existing_data));
    }


    private void showInformation(TableLayout dataContainer) {
        List<? extends Serializable> existingData = this.getData();

        this.addButtonsBeforeData(dataContainer);
        if (existingData == null) {
            return;
        }

        boolean areRowsClickable = this.areRowsClickable();

        for (Serializable data : existingData) {
            TableRow tr = new TableRow(this);
            tr.setPadding(0, 15, 0, 15);
            this.renderRow(data, tr);
            if (areRowsClickable) {
                tr.setClickable(true);
                tr.setOnClickListener(this);
            }
            dataContainer.addView(tr);
        }

        this.addButtonsAfterData(dataContainer);
    }

}
