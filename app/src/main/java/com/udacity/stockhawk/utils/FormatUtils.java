package com.udacity.stockhawk.utils;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mariona on 22/3/17.
 */

public class FormatUtils {

    private static final DecimalFormat DOLLAR_FORMAT = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    private static final DecimalFormat DOLLAR_FORMAT_WITH_PLUS = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    private static final DecimalFormat PERCENTAGE_FORMAT = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
    private static final String HIST_LINE_SEP = "\n";
    private static final String HIST_DATA_SEP = ", ";

    static {
        DOLLAR_FORMAT_WITH_PLUS.setPositivePrefix("+$");
        PERCENTAGE_FORMAT.setMaximumFractionDigits(2);
        PERCENTAGE_FORMAT.setMinimumFractionDigits(2);
        PERCENTAGE_FORMAT.setPositivePrefix("+");
    }


    public static String formatPrice(float price) {
        return format(price, DOLLAR_FORMAT);
    }

    public static String formatPriceWithSign(float price) {
        return format(price, DOLLAR_FORMAT_WITH_PLUS);
    }

    public static String formatPercentage(float value) {
        return format(value/100, PERCENTAGE_FORMAT);
    }

    public static List<HistoricalData> parseHistory(String history) {
        List<HistoricalData> list = new ArrayList<>();
        String[] lines = history.split(HIST_LINE_SEP);

        for (int i = lines.length - 1; i >= 0; i --) {
            String line = lines[i];
            String[] data = line.split(HIST_DATA_SEP);
            HistoricalData hd = new HistoricalData(Float.valueOf(data[0]), Float.valueOf(data[1]));
            list.add(hd);
        }
        return list;
    }

    private static String format(Object value, Format format) {
        return format.format(value);
    }
}
