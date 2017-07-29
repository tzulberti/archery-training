package ar.com.tzulberti.archerytraining.activities.playoff;

import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.dao.PlayoffDAO;
import ar.com.tzulberti.archerytraining.activities.common.AbstractSerieArrowsActivity;
import ar.com.tzulberti.archerytraining.activities.common.AbstractTableDataActivity;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.playoff.Playoff;

/**
 * Created by tzulberti on 6/4/17.
 */

public class ViewExistingPlayoffActivity extends AbstractTableDataActivity {

    protected PlayoffDAO playoffDAO;


    @Override
    protected void addButtonsBeforeData(TableLayout tableLayout) {
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        trParams.span = 4;

        Button viewPlayoffStatsButton = new Button(this);
        viewPlayoffStatsButton.setText(this.getString(R.string.stats_view));
        viewPlayoffStatsButton.setId(Integer.MAX_VALUE - 1);
        viewPlayoffStatsButton.setOnClickListener(this);
        viewPlayoffStatsButton.setLayoutParams(trParams);

        TableRow tr1 = new TableRow(this);
        tr1.addView(viewPlayoffStatsButton);
        tableLayout.addView(tr1);
    }

    @Override
    protected void addButtonsAfterData(TableLayout tableLayout) {

    }

    @Override
    protected void renderRow(Serializable data, TableRow tr) {
        Playoff playoff = (Playoff) data;
        ImageView imageView = new ImageView(this);
        TextView nameText = new TextView(this);
        TextView datetimeText = new TextView(this);
        TextView totalScoreText = new TextView(this);

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
        Intent intent = new Intent(this, AddPlayoffActivity.class);
        startActivity(intent);
    }



    @Override
    public void onClick(View v) {
        int playoffId = v.getId();
        Class activity = null;


        Intent intent = null;
        if (playoffId == (Integer.MAX_VALUE - 1)) {
            // the user selected to view the playoff stats
            intent = new Intent(this, ViewPlayoffStatsActivity.class);
        } else {

            // the user selected to view one playoff
            Playoff playoff = this.playoffDAO.getCompletePlayoffData(playoffId);

            intent = new Intent(this, ViewPlayoffSeriesActivity.class);
            intent.putExtra(AbstractSerieArrowsActivity.CONTAINER_ARGUMENT_KEY, playoff);
        }

        startActivity(intent);
    }
}
