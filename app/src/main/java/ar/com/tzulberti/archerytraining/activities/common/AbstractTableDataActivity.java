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

    public static final String CREATING_INTENT_KEY = "creating";


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
     * @param tr the table row where to add the information
     */
    protected void renderRow(Serializable data, TableRow tr) {
        throw new UnsupportedOperationException();
    }

    /**
     * Renders the data in the table as another table
     * @param data the data to be shown
     * @param mainTableContainer where the data should be included
     */
    protected void renderInformation(Serializable data, TableLayout mainTableContainer) {
        throw new UnsupportedOperationException();
    }

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
     * @return if the user can clicks on the rows of the table
     */
    protected boolean areRowsClickable() {
        return true;
    }

    /**
     * @return if the user can view the button to add a new value
     */
    protected boolean enableAddNew() { return true; }

    /**
     * Get the data from the intent that is used to create a new value
     */
    protected void getValueFromIntent() {}

    /**
     * @return if the data should be rendered using rows, or if inside the table there
     *      are going to be other tables
     */
    protected boolean renderDataUsingRows() { return true; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.createDAOs();
        this.getValueFromIntent();
        setContentView(R.layout.common_show_existing_values);


        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        if (this.enableAddNew()) {
            final AbstractTableDataActivity self = this;
            fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    self.addNewValue();
                }
            });
        } else {
            fab.setVisibility(View.GONE);
        }

        if (this.getIntent().hasExtra(CREATING_INTENT_KEY)) {
            this.getIntent().removeExtra(CREATING_INTENT_KEY);
            this.createDAOs();
            this.addNewValue();
        } else {
            this.showInformation((TableLayout) this.findViewById(R.id.existing_data));
        }
    }


    private void showInformation(TableLayout dataContainer) {
        List<? extends Serializable> existingData = this.getData();

        this.addButtonsBeforeData(dataContainer);
        if (existingData == null) {
            return;
        }

        boolean areRowsClickable = this.areRowsClickable();

        for (Serializable data : existingData) {
            if (this.renderDataUsingRows()) {
                TableRow tr = new TableRow(this);
                tr.setPadding(0, 15, 0, 15);
                this.renderRow(data, tr);
                if (areRowsClickable) {
                    tr.setClickable(true);
                    tr.setOnClickListener(this);
                }
                dataContainer.addView(tr);
            } else {
                this.renderInformation(data, dataContainer);
            }


        }

        this.addButtonsAfterData(dataContainer);
    }

}
