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
import ca.polymtl.inf3995.oronos.services.DataDispatcher;
import ca.polymtl.inf3995.oronos.utils.DataPlot;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;
import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;
import timber.log.Timber;


/**
 * Created by Felix on 15/févr./2018.
 */

public class Plot extends AbstractWidgetContainer<CAN> implements DataDispatcher.CANDataListener {

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

    private TextView titleView;
    private TextView axisView;
    private TextView timeSecondsView;
    private LineChart chart;
    private SeekBar slider;

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
        DataDispatcher.registerCANDataListener(this);

        refreshPlot();
        initializeViews();
        setGenericPlotSettings();
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

        int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
        List<ILineDataSet> lines = new ArrayList<ILineDataSet>();
        int colorCount = 0;
        for (CAN can : this.canList) {
            List<Entry> listEntry;
            DataPlot dataPlot = dataMap.get(can.getId());
            listEntry = dataPlot.retrieveEntries(this.seconds);

            if (!listEntry.isEmpty()) {
                LineDataSet line = new LineDataSet(listEntry, can.getId());

                //Settings pour chaque LineDataSet
                line.setColor(colors[colorCount]);
                line.setCircleColor(colors[colorCount]);
                line.setDrawValues(false); //pas de label au-dessus des points

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

        //Couleur du texte, la même que axisView qui se change automatiquement selon le theme sélectionné
        this.chart.getXAxis().setTextColor(this.axisView.getTextColors().getDefaultColor());
        this.chart.getAxisLeft().setTextColor(this.axisView.getTextColors().getDefaultColor());
        this.chart.getAxisRight().setTextColor(this.axisView.getTextColors().getDefaultColor());
        this.chart.getLegend().setTextColor(this.axisView.getTextColors().getDefaultColor());


    }

    private void createAxisText() {

        this.axisView = new TextView(context);
        this.axisView.setText(String.format("%s (%s)", axis, unit));
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
        timeSecondsView.setText(String.format("%s minutes %s%s", 1, 0, " seconds"));
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
        LinearLayout containerLayout = new LinearLayout(context);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        containerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        //horizontal layout with axis and chart
        LinearLayout horizontalLayout = new LinearLayout(context);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams horizontalLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParams.weight = 20;
        horizontalLayout.setLayoutParams(horizontalLayoutParams);

        LinearLayout innerPlotVerticalLayout = new LinearLayout(context);
        innerPlotVerticalLayout.setOrientation(LinearLayout.VERTICAL);

        horizontalLayout.addView(this.axisView);
        horizontalLayout.addView(this.chart);

        //second horizontal layout for time selection
        LinearLayout timeSelectionLayout = new LinearLayout(context);
        timeSelectionLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams timeSelectionLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        timeSelectionLayoutParams.weight = 1;
        timeSelectionLayout.setLayoutParams(timeSelectionLayoutParams);

        timeSelectionLayout.addView(slider);
        timeSelectionLayout.addView(timeSecondsView);

        containerLayout.addView(this.titleView);
        containerLayout.addView(horizontalLayout);
        containerLayout.addView(timeSelectionLayout);

        addView(containerLayout);
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

    @Override
    public void onCANDataReceived(BroadcastMessage msg) {
        dataMap.get(msg.getCanSid()).addEntry(msg.getData1().doubleValue());
    }

    @Override
    public List<String> getCANSidList() {
        if (list.isEmpty()) {
            return null;
        }
        ArrayList<String> sidList = new ArrayList<>();
        for (CAN can : list) {
            sidList.add(can.getId());
        }
        return sidList;
    }

    @Override
    public String getSourceModule() {
        return null;
    }

    @Override
    public String getSerialNumber() {
        return null;
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
            timeSecondsView.setText(String.format("%s minutes %s%s", Integer.toString(minutesShow), Integer.toString(secondsShow), appendSecondStr));
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
