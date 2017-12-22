package ar.com.tzulberti.archerytraining.helper.charts;


import com.github.mikephil.charting.data.Entry;

import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Used to show the values of the bars as the value - percentage
 *
 * Created by tzulberti on 12/21/17.
 */
public class BarAxisValueFormattter implements IValueFormatter {

    private long totalValues;
    private DecimalFormat decimalFormat;

    public BarAxisValueFormattter(long totalValues) {
        this.totalValues = totalValues;
        this.decimalFormat = new DecimalFormat();
        this.decimalFormat.setMaximumFractionDigits(2);
    }


    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return String.valueOf(value) + " - " + this.decimalFormat.format(value * 100 / totalValues) + "%";
    }
}
