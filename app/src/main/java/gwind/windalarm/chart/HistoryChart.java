package gwind.windalarm.chart;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gwind.windalarm.data.MeteoStationData;

/**
 * Created by Giacomo Spanò on 26/06/2016.
 */
public class HistoryChart {

    Context mContext;
    private LineChart mWindChart;
    private LineChart mTrendChart;
    private LineChart mTemperatureChart;

    private LineData windData;
    private LineData trendData;
    private LineData temperatureData;

    private long startTimeMilliseconds;
    private long endTimeMilliseconds;

    public HistoryChart(Context context, LineChart windChart, LineChart trendChart, LineChart temperatureChart) {
        mContext = context;
        mWindChart = windChart;
        mTrendChart = trendChart;
        mTemperatureChart = temperatureChart;
    }

    public LineData getWindLineData() {
        return windData;
    }

    public boolean drawChart(List<MeteoStationData> meteoDataList) {

        if (meteoDataList == null || meteoDataList.size() == 0)
            return false;
        List<Entry> valsCompSpeed = new ArrayList<Entry>();
        List<Entry> valsCompAvSpeed = new ArrayList<Entry>();
        List<Entry> valsCompDirection = new ArrayList<Entry>();
        List<Entry> valsCompTrend = new ArrayList<Entry>();
        List<Entry> valsCompTemperature = new ArrayList<Entry>();

        Date lastTime = null;
        float x = 0f;
        startTimeMilliseconds = meteoDataList.get(0).date.getTime();
        endTimeMilliseconds = meteoDataList.get(meteoDataList.size()-1).date.getTime();

        for (int i = 0; i < meteoDataList.size(); i++) {

            MeteoStationData md = meteoDataList.get(i);
            Date date = null;
            if (md.date == null) {
                int k = 0;
                k++;
                continue;
            }
            date = md.date;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date.getTime());

            if (!md.date.equals(lastTime)) {

                //x = date.getTime();
                x = date.getTime() - startTimeMilliseconds;

                Float y;
                Entry entry;

                if (md.speed != null)
                    y = Float.valueOf(String.valueOf(md.speed));
                else
                    y = 0.0F;
                entry = new Entry(x, y);
                valsCompSpeed.add(entry);

                if (md.averagespeed != null)
                    y = Float.valueOf(String.valueOf(md.averagespeed));
                else
                    y = 0.0F;
                entry = new Entry(x, y);
                valsCompAvSpeed.add(entry);

                if (md.directionangle != null)
                    y = Float.valueOf(String.valueOf(md.directionangle));
                else
                    y = 0.0F;
                entry = new Entry(x, y);
                valsCompDirection.add(entry);

                if (md.trend != null)
                    y = Float.valueOf(String.valueOf(md.trend));
                else
                    y = 0.0F;
                entry = new Entry(x, y);
                valsCompTrend.add(entry);

                if (md.temperature != null)
                    y = Float.valueOf(String.valueOf(md.temperature));
                else
                    y = 0.0F;
                entry = new Entry(x, y);
                valsCompTemperature.add(entry);

                lastTime = md.date;
            } else {
                int k = 0;
                k++;
                continue;
            }
        }

        LineDataSet setCompSpeed = new LineDataSet(valsCompSpeed, "Velocità vento");
        setCompSpeed.setAxisDependency(YAxis.AxisDependency.LEFT);
        setCompSpeed.setColor(Color.RED);
        setCompSpeed.setDrawCircles(true);
        setCompSpeed.setCircleColor(Color.RED);

        LineDataSet setCompAvSpeed = new LineDataSet(valsCompAvSpeed, "Velocità media");
        setCompAvSpeed.setAxisDependency(YAxis.AxisDependency.LEFT);
        setCompAvSpeed.setColor(Color.BLUE);
        setCompAvSpeed.setDrawCircles(true);
        setCompAvSpeed.setCircleColor(Color.BLUE);

        LineDataSet setCompDirection = new LineDataSet(valsCompDirection, "Direzione");
        setCompDirection.setAxisDependency(YAxis.AxisDependency.RIGHT);
        setCompDirection.setColor(Color.BLACK);
        setCompDirection.setDrawCircles(true);
        setCompDirection.setCircleColor(Color.BLACK);
        setCompDirection.enableDashedLine(10f,10f,10f);

        LineDataSet setCompTrend = new LineDataSet(valsCompTrend, "Trend");
        setCompTrend.setAxisDependency(YAxis.AxisDependency.LEFT);
        setCompTrend.setColor(Color.GREEN);
        setCompTrend.setDrawCircles(true);
        setCompTrend.setCircleColor(Color.GREEN);

        LineDataSet setCompTemperature = new LineDataSet(valsCompTemperature, "Temperatura");
        setCompTemperature.setAxisDependency(YAxis.AxisDependency.LEFT);
        setCompTemperature.setColor(Color.CYAN);
        setCompTemperature.setDrawCircles(true);
        setCompTemperature.setCircleColor(Color.CYAN);

        // use the interface ILineDataSet
        List<ILineDataSet> windDataSets = new ArrayList<ILineDataSet>();
        List<ILineDataSet> trendDataSets = new ArrayList<ILineDataSet>();
        List<ILineDataSet> temperatureDataSets = new ArrayList<ILineDataSet>();

        windDataSets.add(setCompSpeed);
        windDataSets.add(setCompAvSpeed);
        windDataSets.add(setCompDirection);
        trendDataSets.add(setCompTrend);
        temperatureDataSets.add(setCompTemperature);

        setCompDirection.setValueFormatter(new DirectionValueFormatter());


        mWindChart.setDescription("");
        //mWindChart.setDescriptionPosition(0,);

        XAxis xAxis = mWindChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setGranularity(60000L * 5f); // one minute in millis

        XAxis xTrendAxis = mTrendChart.getXAxis();
        xTrendAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xTrendAxis.setDrawAxisLine(true);
        xTrendAxis.setDrawGridLines(true);
        xTrendAxis.setTextColor(Color.BLACK);
        xTrendAxis.setGranularity(60000L * 5f); // one minute in millis

        XAxis xTemperatureAxis = mTemperatureChart.getXAxis();
        xTemperatureAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xTemperatureAxis.setDrawAxisLine(true);
        xTemperatureAxis.setDrawGridLines(true);
        xTemperatureAxis.setTextColor(Color.BLACK);
        xTemperatureAxis.setGranularity(60000L * 5f); // one minute in millis

        AxisValueFormatter avf = new AxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                Float f = value;
                long milliseconds = f.longValue();
                return mFormat.format(new Date(startTimeMilliseconds + milliseconds));

                //return mFormat.format(new Date(milliseconds));
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        };

        xAxis.setValueFormatter(avf);
        xTemperatureAxis.setValueFormatter(avf);
        xTrendAxis.setValueFormatter(avf);

        YAxis yLeftAxis = mWindChart.getAxisLeft();
        yLeftAxis.setDrawAxisLine(true);
        yLeftAxis.setDrawGridLines(true);
        yLeftAxis.setAxisMinValue(0f); // start at zero
        yLeftAxis.setAxisMaxValue(35f); // the axis maximum is 100
        yLeftAxis.setGranularity(5f);
        yLeftAxis.setLabelCount(10);
        //yLeftAxis.setGranularity(22.5f); // one minute in millis
        //yLeftAxis.setInverted(true);


        YAxis yRightAxis = mWindChart.getAxisRight();
        yRightAxis.setDrawAxisLine(true);
        yRightAxis.setDrawGridLines(true);
        yRightAxis.setAxisMinValue(0f); // start at zero
        yRightAxis.setAxisMaxValue(337.50f); // the axis maximum is 100
        yRightAxis.setLabelCount(16);
        yRightAxis.setGranularity(22.5f);
        yRightAxis.setInverted(true);
        yRightAxis.setGridColor(Color.GREEN);
        yRightAxis.setValueFormatter(new DirectionAxisValueFormatter());

        windData = new LineData(windDataSets);
        mWindChart.setData(windData);
        trendData = new LineData(trendDataSets);
        mTrendChart.setData(trendData);
        temperatureData = new LineData(temperatureDataSets);
        mTemperatureChart.setData(temperatureData);

        Legend legend = mWindChart.getLegend();
        legend.setFormSize(10f); // set the size of the legend forms/shapes
        legend.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setXEntrySpace(20f); // set the space between the legend entries on the x-axis
        legend.setYEntrySpace(5f); // set the space between the legend entries on the y-axis
        legend.setEnabled(true);

        legend = mTemperatureChart.getLegend();
        legend.setFormSize(10f); // set the size of the legend forms/shapes
        legend.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setXEntrySpace(20f); // set the space between the legend entries on the x-axis
        legend.setYEntrySpace(5f); // set the space between the legend entries on the y-axis
        legend.setEnabled(true);

        legend = mTrendChart.getLegend();
        legend.setFormSize(10f); // set the size of the legend forms/shapes
        legend.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setXEntrySpace(20f); // set the space between the legend entries on the x-axis
        legend.setYEntrySpace(5f); // set the space between the legend entries on the y-axis
        legend.setEnabled(true);

        Calendar cal = Calendar.getInstance();
        cal.setTime(meteoDataList.get(meteoDataList.size()-1).date);
        cal.add(Calendar.HOUR_OF_DAY, -2); //minus number would decrement the hours
        Date leftDate = cal.getTime();
        long leftDateTimeMilliseconds = leftDate.getTime();

        //mWindChart.zoom(1f,1f,1f,1f); // ripristina lo zoom iniziale dal draw precedente
        //mWindChart.zoom(4f,1f,3*(endTimeMilliseconds-startTimeMilliseconds)/4,35/4);
        //mWindChart.moveViewToX(leftDateTimeMilliseconds);
        //mWindChart.setVisibleXRangeMaximum(20); // allow 20 values to be displayed at once on the x-axis, not more
        //mWindChart.moveViewToX(10); // set

        // muovi la finestra a leftdatetime
        //mTemperatureChart.zoom(4f,1f,3*(endTimeMilliseconds-startTimeMilliseconds)/4,35/4);
        //mTemperatureChart.zoom(10f,1f,leftDateTimeMilliseconds,35/2);
        //mTemperatureChart.moveViewToX(leftDateTimeMilliseconds);
        //mTemperatureChart.moveViewToX (3*(endTimeMilliseconds-startTimeMilliseconds)/4);

        mTrendChart.zoom(2f,1f,3*(endTimeMilliseconds-startTimeMilliseconds)/4,35/4);
        mTrendChart.moveViewToX (3*(endTimeMilliseconds-startTimeMilliseconds)/4);

        mWindChart.invalidate(); // refresh
        mTemperatureChart.invalidate(); // refresh
        mTrendChart.invalidate(); // refresh

        return true;
    }

    private final String[] directionSymbols = {
            "E", "E-NE", "NE", "NE-N",
            "N", "N-NO", "NO", "NO-O",
            "O", "O-SO", "SO", "SO-S",
            "S", "S-SE", "SE", "SE-E"};

    private class DirectionAxisValueFormatter implements AxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value > 22.5 * 15 || value < 0) return "" + value + "N/A";
            return "" +/*value+ " " +*/ directionSymbols[(int) (value / 22.5)];
        }

        @Override
        public int getDecimalDigits() {
            return 1;
        }
    }

    private class DirectionValueFormatter implements ValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if (value > 22.5 * 15 || value < 0) return "" + value + "N/A";
            return "" +/*value+ " " +*/ directionSymbols[(int) (value / 22.5)];
        }
    }

}
