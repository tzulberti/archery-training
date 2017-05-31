package ar.com.tzulberti.archerytraining.fragments.series;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;
import ar.com.tzulberti.archerytraining.model.SerieData;

/**
 * Created by tzulberti on 4/21/17.
 */

public class ViewRawDataFragment extends BaseSeriesFragment {

    private static final int MAX_VALUES_TO_SHOW = 20;

    private TableLayout rawDataTableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.cleanState(container);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.today_raw_data, container, false);
        this.setObjects();

        this.rawDataTableLayout = (TableLayout) view.findViewById(R.id.todayRawData);
        this.showInformation(view);
        return view;
    }

    @Override
    public void handleClick(View v) {
        this.serieDataDAO.deleteSerieId(v.getId());
        TableRow tr = (TableRow) v.getParent();
        rawDataTableLayout.removeView(tr);
    }

    private void showInformation(View view) {
        List<SerieData> serieDatas = this.serieDataDAO.getLastValues(MAX_VALUES_TO_SHOW);

        Context context = getContext();
        for (SerieData data : serieDatas) {
            TableRow tr = new TableRow(context);

            TextView distanceTextView = new TextView(context);
            TextView arrowsAmountText = new TextView(context);
            TextView datetimeText = new TextView(context);
            Button removeButton = new Button(context);

            distanceTextView.setText(String.valueOf(data.distance));
            arrowsAmountText.setText(String.valueOf(data.arrowsAmount));
            datetimeText.setText(DatetimeHelper.DATETIME_FORMATTER.format(data.datetime));

            removeButton.setText(getText(R.string.commonDelete));
            removeButton.setId(data.id);
            removeButton.setOnClickListener(this);

            tr.addView(distanceTextView);
            tr.addView(arrowsAmountText);
            tr.addView(datetimeText);
            tr.addView(removeButton);
            this.rawDataTableLayout.addView(tr);
        }
    }

}
