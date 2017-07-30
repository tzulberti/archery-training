package ar.com.tzulberti.archerytraining.activities.bow;

import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.dao.BowDAO;
import ar.com.tzulberti.archerytraining.activities.common.AbstractTableDataActivity;
import ar.com.tzulberti.archerytraining.model.bow.Bow;

/**
 * Created by tzulberti on 6/12/17.
 */

public class ViewExistingBowsActivity extends AbstractTableDataActivity {

    @Override
    protected void addButtonsBeforeData(TableLayout tableLayout) {

    }

    @Override
    protected void addButtonsAfterData(TableLayout tableLayout) {

    }

    @Override
    protected void renderRow(Serializable data, TableRow tr) {
        Bow bow = (Bow) data;
        TextView bowName = new TextView(this);
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
        Intent intent = new Intent(this, ViewBowActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        long bowId = v.getId();
        Bow bow = this.bowDAO.getBowsInformation(bowId).get(0);

        Intent intent = new Intent(this, ViewBowActivity.class);
        intent.putExtra(ViewBowActivity.BOW_ARGUMENT_KEY, bow);
        startActivity(intent);
    }
}
