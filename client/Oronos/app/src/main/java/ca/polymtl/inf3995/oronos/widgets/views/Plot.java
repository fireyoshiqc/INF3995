package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import ca.polymtl.inf3995.oronos.utils.DataPlot;
import ca.polymtl.inf3995.oronos.services.BroadcastMessage;
import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;
import timber.log.Timber;


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



    Plot(Context context, String name, String unit, String axis, List<CAN> list) {

        super(context, list);
        this.name = name;
        this.unit = unit;
        this.axis = axis;
        this.canList = list;
        this.context = context;
        this.seconds = DEFAULT_TIME_SELECTED;
        this.chart = new LineChart(context);

        initializeDataList();
        Timber.v("initialized data list");

        refreshPlot();
        Timber.v("Refreshed plot");
        setGenericPlotSettings();
        Timber.v("Set generic plot settings");

        initializeViews();
        Timber.v("initialized views");

        IntentFilter intentFilter = new IntentFilter();
        for (CAN can : list) {
            intentFilter.addAction(can.getId());
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);

        Timber.v("setted up the necessary for receiving data");
        run();

    }

    private void run() {

        final Handler handler = new Handler();


        handler.postDelayed(new Runnable(){
            public void run(){

               refreshPlot();
                handler.postDelayed(this, REFRESH_DELAY);
            }
        }, REFRESH_DELAY);

    }

    private void initializeDataList() {
        dataMap = new HashMap<>();
        for(CAN can : this.canList){
            DataPlot dataPlot = new DataPlot(MAXIMUM_ENTRIES);
            String canID = can.getId();
            dataMap.put(canID, dataPlot);
            Timber.v("can ID from the CAN object: " + can.getId());
        }
    }



    private void refreshPlot() {
        int[] colors = {Color.RED,Color.GREEN,Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
        LineData lineData = new LineData();
        List<ILineDataSet> lines = new ArrayList<ILineDataSet>();
        int colorCount = 0;
        for (String dataName: dataMap.keySet()) {
            DataPlot dataPlot = dataMap.get(dataName);
            List<Entry> listEntry = dataPlot.retrieveEntries(this.seconds);
            if(!listEntry.isEmpty()){
                LineDataSet line = new LineDataSet(listEntry, dataName);
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) { //Gets execute each time a can msg is received
            BroadcastMessage msg = (BroadcastMessage) Parcels.unwrap(intent.getParcelableExtra("data"));
            msg.getCanSid();

            dataMap.get(msg.getCanSid()).addEntry(msg.getData1().doubleValue());

            //TODO: put new data in DataPlots of hashmap dataMap
            Timber.v("can sid: " + msg.getCanSid());
            Timber.v("data1: " + msg.getData1().intValue());
            Timber.v("data2: " + msg.getData1().doubleValue());
            //Timber.v("");


        }
    };


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

    //TODO: remove this if we don't need to change XAxis
    private void updateAxisScale() {
        XAxis xAxis = this.chart.getXAxis();
        xAxis.setAxisMaximum(seconds);
        xAxis.setAxisMinimum(0);
        this.chart.invalidate(); // refresh
    }

    private void createTitle() {

        this.titleView = new TextView(context);

        if (name.equals("")) {
            this.titleView.setText("this graph has no name");
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

    private void createSlider() {
        timeSecondsView = new TextView(context);
        timeSecondsView.setText(1 + " minute");
        LayoutParams timeViewParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        timeViewParams.weight = 4;
        timeSecondsView.setLayoutParams(timeViewParams);
        timeSecondsView.setGravity(Gravity.CENTER_VERTICAL);

        this.slider = new SeekBar(context);
        slider.setMax(MAXIMUM_ENTRIES - DEFAULT_TIME_SELECTED);
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SliderChangeListener();
        slider.setOnSeekBarChangeListener(seekBarChangeListener);
        LayoutParams sliderLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        sliderLayoutParams.weight = 1;
        slider.setLayoutParams(sliderLayoutParams);
    }

    private void initializeViews() { //Could have been an xml
        createTitle();
        createAxisText();
        createSlider();
        this.chart.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

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

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public String getAxis() {
        return axis;
    }

}
