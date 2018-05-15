package kr.co.ezinfotech.parkingmaster;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class MyAxisValueFormatter2 implements IAxisValueFormatter
{
    private String[] mValues;

    public MyAxisValueFormatter2(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        /* ORIGINAL
        return mValues[(int) value];
        */
        int intValue = (int) value;
        if (mValues.length > intValue && intValue >= 0)
            return mValues[intValue];
        return "";
    }
}
