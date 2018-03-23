package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.utils.DataPlot;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;


/**
 * Created by Felix on 15/févr./2018.
 */

public class Plot extends AbstractWidgetContainer<CAN> {

    //consts
    private final int REFRESH_DELAY = 1000; //milliseconds
    private final int UPDATE_DATA_DELAY = 1000; //milliseconds
    private final int TITLE_TEXT_SIZE = 30; //size in sp
    private final int DEFAULT_TIME_SELECTED = 60; //seconds
    private final int MAXIMUM_ENTRIES = 300; //corresponds to seconds, we have 1 entry/second

    //params
    private final String name;
    private final String unit;
    private final String axis;
    private final List<CAN> canList;
    private Context context;

    //variables
    private Map<String, DataPlot> dataMap;
    private int seconds;

    //Views, layouts...
    private LinearLayout containerLayout;
    private LinearLayout horizontalLayout;
    private LinearLayout innerPlotVerticalLayout;
    private LinearLayout timeSelectionLayout;
    private TextView titleView;
    private TextView axisView;
    private TextView timeSecondsView;
    private LineChart chart;
    private SeekBar slider;
    private BroadcastReceiver broadcastReceiver;

    public Plot(Context context, String name, String unit, String axis, List<CAN> list) {

        super(context, list);
        this.name = name;
        this.unit = unit;
        this.axis = axis;
        this.canList = list;
        this.context = context;
        this.seconds = DEFAULT_TIME_SELECTED;
        this.chart = new LineChart(context);

        initializeDataList();

        refreshPlot();
        setGenericPlotSettings();

        initializeViews();
        run();

    }

    private void run() {

        final Handler handler = new Handler();


        handler.postDelayed(new Runnable() {
            public void run() {

                refreshPlot();
                handler.postDelayed(this, REFRESH_DELAY);
            }
        }, REFRESH_DELAY);

    }

    private void initializeDataList() {
        dataMap = new HashMap<>();
        for (CAN can : this.canList) {
            DataPlot dataPlot = new DataPlot(MAXIMUM_ENTRIES);
            String canID = can.getId();
            dataMap.put(canID, dataPlot);
        }
    }

    private void refreshPlot() {
        if (broadcastReceiver == null && GlobalParameters.canSid != null && GlobalParameters.canModuleTypes != null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) { //Gets execute each time a can msg is received
                    BroadcastMessage msg = Parcels.unwrap(intent.getParcelableExtra("data"));
                    dataMap.get(msg.getCanSid()).addEntry(msg.getData1().doubleValue());
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            for (CAN can : list) {
                intentFilter.addAction(can.getId());
            }

            for (String key : GlobalParameters.canModuleTypes.keySet()) {
                intentFilter.addCategory(key);
            }

            for (int i = 0; i < 16; i++) {
                intentFilter.addCategory(String.format("%d", i));
            }

            LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);
        }


        int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
        LineData lineData = new LineData();
        List<ILineDataSet> lines = new ArrayList<ILineDataSet>();
        int colorCount = 0;
        for (CAN can : this.canList) {
            DataPlot dataPlot = dataMap.get(can.getId());
            List<Entry> listEntry = dataPlot.retrieveEntries(this.seconds);
            if (!listEntry.isEmpty()) {
                LineDataSet line = new LineDataSet(listEntry, can.getId());
                line.setColor(colors[colorCount]);
                line.setCircleColor(colors[colorCount]);
                lines.add(line);
                colorCount++;
            }
        }

        this.chart.setData(new LineData(lines));
        this.chart.invalidate(); // refresh
    }

    private void setGenericPlotSettings() {
        //no interaction
        this.chart.setTouchEnabled(false);

        this.chart.setNoDataText("No data");
        //no desc
        Description desc = new Description();
        desc.setText("");
        this.chart.setDescription(desc);

    }

    private void createAxisText() {

        this.axisView = new TextView(context);
        this.axisView.setText(axis + " (" + unit + ")");
        this.axisView.setRotation(-90);
        this.axisView.setGravity(Gravity.CENTER_HORIZONTAL);
        this.axisView.setGravity(Gravity.CENTER_VERTICAL);
        this.axisView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

    }

    private void createTitle() {

        this.titleView = new TextView(context);

        if (name.equals("")) {
            //this.titleView.setText("plot");
        } else {
            this.titleView.setText(name);
        }
        this.titleView.setTextSize(TITLE_TEXT_SIZE);
        this.titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        this.titleView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

    }

    private void createSlider() {
        timeSecondsView = new TextView(context);
        timeSecondsView.setText(1 + " minute");
        LayoutParams timeViewParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        timeViewParams.weight = (float) 3.5;
        timeSecondsView.setLayoutParams(timeViewParams);
        timeSecondsView.setGravity(Gravity.CENTER_VERTICAL);

        this.slider = new SeekBar(context);
        slider.setMax(MAXIMUM_ENTRIES - DEFAULT_TIME_SELECTED);
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SliderChangeListener();
        slider.setOnSeekBarChangeListener(seekBarChangeListener);
        LayoutParams sliderLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        sliderLayoutParams.weight = 1;
        slider.setLayoutParams(sliderLayoutParams);
        DisplayMetrics dm = slider.getResources().getDisplayMetrics();
        sliderLayoutParams.setMargins(convertDpToPx(100, dm), convertDpToPx(0, dm), convertDpToPx(0, dm), convertDpToPx(0, dm));
    }

    private void initializeViews() { //Could have been an xml
        createTitle();
        createAxisText();
        createSlider();
        this.chart.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        this.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                )
        );

        //main container
        this.containerLayout = new LinearLayout(context);
        this.containerLayout.setOrientation(LinearLayout.VERTICAL);
        this.containerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        //horizontal layout with axis and chart
        this.horizontalLayout = new LinearLayout(context);
        this.horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams horizontalLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParams.weight = 20;
        this.horizontalLayout.setLayoutParams(horizontalLayoutParams);

        this.innerPlotVerticalLayout = new LinearLayout(context);
        this.innerPlotVerticalLayout.setOrientation(LinearLayout.VERTICAL);

        this.horizontalLayout.addView(this.axisView);
        this.horizontalLayout.addView(this.chart);

        //second horizontal layout for time selection
        this.timeSelectionLayout = new LinearLayout(context);
        this.timeSelectionLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams timeSelectionLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        timeSelectionLayoutParams.weight = 1;
        this.timeSelectionLayout.setLayoutParams(timeSelectionLayoutParams);

        this.timeSelectionLayout.addView(slider);
        this.timeSelectionLayout.addView(timeSecondsView);

        this.containerLayout.addView(this.titleView);
        this.containerLayout.addView(this.horizontalLayout);
        this.containerLayout.addView(this.timeSelectionLayout);

        addView(this.containerLayout);
    }

    private int convertDpToPx(int dp, DisplayMetrics displayMetrics) {
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(pixels);
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public String getAxis() {
        return axis;
    }

    //Slider
    private class SliderChangeListener implements SeekBar.OnSeekBarChangeListener {
        private int timeSelected;

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int minuteInSeconds = 60;
            timeSelected = i + minuteInSeconds;
            int minutesShow = timeSelected / minuteInSeconds;
            int secondsShow = timeSelected % minuteInSeconds;
            String appendSecondStr = " seconds";
            if (secondsShow < 10) {
                appendSecondStr = "   seconds";
            }
            timeSecondsView.setText(Integer.toString(minutesShow) + " minutes " + Integer.toString(secondsShow) + appendSecondStr);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seconds = timeSelected;
            refreshPlot();
        }
    }
}
