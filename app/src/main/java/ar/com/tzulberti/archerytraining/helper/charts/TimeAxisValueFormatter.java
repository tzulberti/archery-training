package ar.com.tzulberti.archerytraining.helper.charts;


import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


import ar.com.tzulberti.archerytraining.helper.DatetimeHelper;

/**
 *
 * Created by tzulberti on 6/16/17.
 */
public class TimeAxisValueFormatter implements IAxisValueFormatter {

    private long referenceTimestamp; // minimum timestamp in your data set
    private DateFormat mDataFormat;

    public TimeAxisValueFormatter(long referenceTimestamp, DateFormat simpleDateFormat) {
        this.referenceTimestamp = referenceTimestamp;
        this.mDataFormat = simpleDateFormat;
    }


    /**
     * Called when a value from an axis is to be formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *
     * @param value the value to be formatted
     * @param axis  the axis the value belongs to
     * @return
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // convertedTimestamp = originalTimestamp - referenceTimestamp
        long convertedTimestamp = (long) value;

        // Retrieve original timestamp
        long originalTimestamp = referenceTimestamp + convertedTimestamp;

        // Convert timestamp to hour:minute
        return this.mDataFormat.format(DatetimeHelper.databaseValueToDate(originalTimestamp));
    }

}