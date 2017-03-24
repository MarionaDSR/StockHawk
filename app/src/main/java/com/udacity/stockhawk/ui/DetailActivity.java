package com.udacity.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.utils.FormatUtils;
import com.udacity.stockhawk.utils.HistoricalData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.stockhawk.data.Contract.Quote.POSITION_ABSOLUTE_CHANGE;
import static com.udacity.stockhawk.data.Contract.Quote.POSITION_HISTORY;
import static com.udacity.stockhawk.data.Contract.Quote.POSITION_PERCENTAGE_CHANGE;
import static com.udacity.stockhawk.data.Contract.Quote.POSITION_PRICE;

public class DetailActivity extends AppCompatActivity {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_title)
    TextView tvTitle;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_value)
    TextView tvValue;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_absolute_change)
    TextView tvAbsolute;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_percentage_change)
    TextView tvPercentage;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_chart)
    LineChart lcChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        String symbol = getIntent().getStringExtra(getString(R.string.extra_symbol_key));
        tvTitle.setText(symbol);

        lcChart.setNoDataText(getString(R.string.chart_no_data));

        Cursor cursor = getContentResolver().query(Contract.Quote.makeUriForStock(symbol), null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // data available: _id, symbol, price, absolute_change, percentage_change, history

            tvValue.setText(FormatUtils.formatPrice(cursor.getFloat(POSITION_PRICE)));
            float absoluteChange = cursor.getFloat(POSITION_ABSOLUTE_CHANGE);
            tvAbsolute.setText(FormatUtils.formatPriceWithSign(absoluteChange));
            tvPercentage.setText(FormatUtils.formatPercentage(cursor.getFloat(POSITION_PERCENTAGE_CHANGE)));

            if (absoluteChange > 0) {
                tvAbsolute.setBackgroundResource(R.drawable.percent_change_pill_green);
                tvPercentage.setBackgroundResource(R.drawable.percent_change_pill_green);
            } else {
                tvAbsolute.setBackgroundResource(R.drawable.percent_change_pill_red);
                tvPercentage.setBackgroundResource(R.drawable.percent_change_pill_red);
            }

            String history = cursor.getString(POSITION_HISTORY);
            List<HistoricalData> historyList = FormatUtils.parseHistory(history);
            List<Entry> entries = new ArrayList<>();
            for (HistoricalData hd : historyList)
            {
                entries.add(new Entry(hd.getDate(), hd.getValue()));
            }
            if (!entries.isEmpty()) {
                LineData lineData = prepareData(entries, symbol);
                lcChart.setData(lineData);
                lcChart.invalidate();
            }
        }
        cursor.close();
    }

    private LineData prepareData(List<Entry> entries, String title) {
        LineDataSet dataSet = new LineDataSet(entries, title);
        int chartColor = Color.BLUE;

        dataSet.enableDashedLine(10f, 5f, 0f);
        dataSet.enableDashedHighlightLine(10f, 5f, 0f);
        dataSet.setColor(chartColor);
        dataSet.setCircleColor(chartColor);
        dataSet.setLineWidth(1f);
        dataSet.setCircleRadius(3f);

        dataSet.setHighlightLineWidth(2f);
        dataSet.setHighLightColor(Color.RED);

        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(6f);
        dataSet.setDrawFilled(true);
        dataSet.setFormLineWidth(1f);
        dataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        dataSet.setFormSize(15.f);
        dataSet.setFillColor(chartColor);

        LineData lineData = new LineData(dataSet);
        lineData.setValueFormatter(new LargeValueFormatter());

        lcChart.setBackgroundColor(Color.LTGRAY);
        lcChart.getXAxis().setValueFormatter(new AxisDateFormatter(this));
        lcChart.setContentDescription(getString(R.string.chart_desc));
        return lineData;
    }

    public class AxisDateFormatter implements IAxisValueFormatter {

        Context context;

        public AxisDateFormatter(Context context) {
            this.context = context;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis((long) value);
            return context.getString(R.string.chart_axis_date_format, cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH) + 1));
        }
    }
}
