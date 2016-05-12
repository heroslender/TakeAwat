package net.netne.kitlfre.takeawat;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InputFilterMinMax implements InputFilter {

    private double mMinValue;
    private double mMaxValue;

    private static final double MIN_VALUE_DEFAULT = Double.MIN_VALUE;
    private static final double MAX_VALUE_DEFAULT = Double.MAX_VALUE;

    public InputFilterMinMax(Double min, Double max) {
        this.mMinValue = (min != null ? min : MIN_VALUE_DEFAULT);
        this.mMaxValue = (max != null ? max : MAX_VALUE_DEFAULT);
    }

    public InputFilterMinMax(Integer min, Integer max) {
        this.mMinValue = (min != null ? min : MIN_VALUE_DEFAULT);
        this.mMaxValue = (max != null ? max : MAX_VALUE_DEFAULT);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String replacement = source.subSequence(start, end).toString();
            String newVal = dest.subSequence(0, dstart).toString() + replacement
                    + dest.subSequence(dend, dest.length()).toString();

            // check if there are leading zeros
            if (newVal.matches("0\\d+.*"))
                if (TextUtils.isEmpty(source))
                    return dest.subSequence(dstart, dend);
                else
                    return "";

            // check range
            double input = round(Double.parseDouble(newVal), 1);
            Log.e("double", input+" "+dstart+" "+dend);
            if (!isInRange(mMinValue, mMaxValue, input))
                if (TextUtils.isEmpty(source))
                    return input+"";
                    //return dest.subSequence(dstart, dend);
                else
                    return "";

            return null;
        } catch (NumberFormatException nfe) {
            Log.e("inputfilter", "parse");
        }
        return "";
    }

    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        //return bd.doubleValue();
        return (Math.ceil(bd.doubleValue() * 2) / 2);
    }
}
