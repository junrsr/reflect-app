package com.example.reflect;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendsFragment extends Fragment {

    private LineChart moodChart; // chart used to track mood over the past 7 days

    private View view;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "trends1";
    private static final String ARG_PARAM2 = "trends2";

    public TrendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrendsFragment.
     */
    public static TrendsFragment newInstance(String param1, String param2) {
        TrendsFragment fragment = new TrendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialise all component values
        this.view = view;
        initValues();

        // draw the different charts
        drawMoodChart();
    }

    private void initValues(){
        moodChart = view.findViewById(R.id.moodLineChart);
    }

    private void drawMoodChart(){
        // create a null description and apply it to the line chart
        Description description = new Description();
        description.setEnabled(false);
        moodChart.setDescription(description);

        // create font
        Typeface interTypeface = ResourcesCompat.getFont(getContext(), R.font.inter_bold);

        // configuration of the legend
        Legend legend = moodChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP); // align the legend at the top
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // center align the legend
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // have the legend display horizontally
        legend.setDrawInside(false); // draw legend outside of graph
        legend.setForm(Legend.LegendForm.CIRCLE); // adjust the legend to a circle
        legend.setFormSize(20f);
        legend.setTextColor(getContext().getColor(R.color.white));
        legend.setTypeface(interTypeface); // adjust the font
        legend.setTextSize(14f);
        legend.setXEntrySpace(70f);
        legend.setYOffset(10f);

        // don't draw right side axis
        moodChart.getAxisRight().setDrawLabels(false);
        moodChart.getAxisRight().setDrawAxisLine(false);
        moodChart.getAxisRight().setDrawGridLines(false);

        // configure x-axis settings
        XAxis xAxis = moodChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x-axis at bottom
        xAxis.setDrawGridLines(false); // don't draw the grid lines
        xAxis.setDrawAxisLine(false); // don't draw the axis line

        // configure x-axis labels
        xAxis.setTextColor(getContext().getColor(R.color.white)); // adjust the x-axis color
        xAxis.setTextSize(16f);
        xAxis.setTypeface(interTypeface); // adjust the font

        // set x-axis label values
        String[] labels = new String[]{"M", "T", "W", "T", "F", "S", "S"};
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // repeat for appropriate days
                if (value >= 0 && value <= labels.length){
                    // pair the relevant x-coordinate value with the appropriate date
                    return labels[(int) value];
                }
                return super.getFormattedValue(value);
            }
        });

        // configure y-axis settings
        YAxis yAxis = moodChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(6f); // between 0-6 so we can display values from 1-5 easily (moodScore)
        yAxis.setDrawLabels(false); // don't show labels
        yAxis.setDrawGridLines(false); // don't draw grid lines
        yAxis.setDrawAxisLine(false); // don't draw axis line

        // list to store all entries
        List<Entry> currentEntries = new ArrayList<>();

        // retrieving all date components
        Date today = new Date();
        Date startOfWeek = getStartOfWeek(today); // gets the start of the week

        // create a new database
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase database = databaseHelper.getReadableDatabase(); // get a readable instance -- no need to write anything

        // variable to store the current day
        int count = 0;

        // repeat until we exceed the current day
        while (startOfWeek.getTime() <= today.getTime()){
            // query the database for that specific date
            Cursor moodResults = database.query(Utils.JOURNAL_TABLE, new String[] {"moodScore"}, "date = ?", new String[] {"" + startOfWeek.getTime()}, null, null, null);

            // if an entry exists
            if (moodResults.getCount() != 0 && moodResults.moveToFirst()){
                // get the mood score
                int moodScore = moodResults.getInt(0);

                // add the mood score to current entries
                currentEntries.add(new Entry(count, moodScore));

            }

            // close the cursor
            moodResults.close();

            // increment the day
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startOfWeek);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            startOfWeek = calendar.getTime();

            // increment the count
            count++;
        }

        // close the database
        database.close();

        // todo: update with actual historical content (if applicable)
        // historical entries
        List<Entry> historicalEntries = new ArrayList<>();
        historicalEntries.add(new Entry(0, 5f));
        historicalEntries.add(new Entry(1, 3f));
        historicalEntries.add(new Entry(3, 1f));
        historicalEntries.add(new Entry(4, 1f));
        historicalEntries.add(new Entry(6, 2f));

        // create a new data set to represent the current week
        LineDataSet currentDataSet = new LineDataSet(currentEntries, "This Week");
//        currentDataSet.setDrawCircles(false); // don't draw circles at each point
        currentDataSet.setCircleColor(getContext().getColor(R.color.emotionGood));
        currentDataSet.setDrawCircleHole(false);
        currentDataSet.setDrawValues(false); // don't display the values at each point
        currentDataSet.setColor(getContext().getColor(R.color.emotionGood));
        currentDataSet.setLineWidth(4f);

        // create a new data set to represent historical data
        LineDataSet historicalDataSet = new LineDataSet(historicalEntries, "Average");
//        historicalDataSet.setDrawCircles(false); // don't draw circles at each point
        historicalDataSet.setCircleColor(getContext().getColor(R.color.shaded));
        historicalDataSet.setDrawCircleHole(false);
        historicalDataSet.setDrawValues(false); // don't display the values at each point
        historicalDataSet.setColor(getContext().getColor(R.color.shaded));
        historicalDataSet.setLineWidth(4f);

        // put both data sets in the line chart
        LineData lineData =  new LineData(currentDataSet, historicalDataSet);
        moodChart.setData(lineData);

        // add extra padding to the bottom of the chart
        moodChart.setExtraOffsets(0, 0, 0, 20f);

        // disable interactivity
        moodChart.setHighlightPerTapEnabled(false);
        moodChart.setTouchEnabled(false);

        // display the graph
        moodChart.invalidate();


    }


    private Date getStartOfWeek(Date date){
        // calendar set to the current date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // set calendar to the start of the week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // set time to 00:00:00 for easier comparison
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // get the start of the week as a date object
        Date startOfWeek = calendar.getTime();
        return startOfWeek;
    }
}