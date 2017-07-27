package com.udacity.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.stockhawk.StockHawkApp.HISTORY_EXTRA_KEY;
import static com.udacity.stockhawk.StockHawkApp.SYMBOL_EXTRA_KEY;

public class DetailViewFragment extends Fragment implements IAxisValueFormatter {

    public static final int VALUE_INDEX = 1;
    public static final int DATE_INDEX = 0;
    public static final int TEXT_COLOR = Color.WHITE;

    @BindView(R.id.chart)
    LineChart chart;

    private List<String> stockHistoryDates = new ArrayList<>();
    private List<Entry> stockHistoryValues = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_view, container, false);
        ButterKnife.bind(this, view);
        Bundle arguments = getArguments();

        String[] history = {};
        if (arguments != null) {
            String symbol = arguments.getString(SYMBOL_EXTRA_KEY);
            String historyAsString = arguments.getString(HISTORY_EXTRA_KEY);
            if (historyAsString != null) {
                history = historyAsString.split("\n");
            }
            parseData(history);
            setDataToChart();
            setText(symbol);

            chart.invalidate();
        }
        return view;
    }

    private void setDataToChart() {
        LineDataSet price = new LineDataSet(stockHistoryValues, getString(R.string.price));
        LineData lineData = new LineData(price);
        chart.setData(lineData);
    }

    private void setText(String symbol) {
        Description description = new Description();
        description.setText(symbol + " - " + getString(R.string.price));
        chart.setDescription(description);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(this);

        setTextColor(description, xAxis);
    }

    private void parseData(String[] history) {
        for (int index = 0; index < history.length; index++) {
            String[] data = history[index].split(", ");
            stockHistoryValues.add(new Entry(index, Float.parseFloat(data[VALUE_INDEX])));
            stockHistoryDates.add(data[DATE_INDEX]);
        }
    }

    private void setTextColor(Description description, XAxis xAxis) {
        Legend legend = chart.getLegend();
        legend.setTextColor(TEXT_COLOR);
        description.setTextColor(TEXT_COLOR);
        xAxis.setTextColor(TEXT_COLOR);
        YAxis yAxisL = chart.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxisR = chart.getAxis(YAxis.AxisDependency.RIGHT);
        yAxisL.setTextColor(TEXT_COLOR);
        yAxisR.setTextColor(TEXT_COLOR);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(
                (long) Float.parseFloat(stockHistoryDates.get((int) Math.floor(value))));
        int year = calendar.get(Calendar.YEAR) - 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return month + "/" + day + "/" + year;
    }
}
