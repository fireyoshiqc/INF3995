package ca.polymtl.inf3995.oronos.widgets.views;

import android.content.Context;

import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.widgets.containers.AbstractWidgetContainer;


/**
 * Created by Felix on 15/févr./2018.
 */

public class Plot extends AbstractWidgetContainer<CAN> {

    private final String name;
    private final String unit;
    private final String axis;
    private final List<CAN> canList;

    private List<Data> dataList;

    private int seconds;

    private Context context;

    private LinearLayout containerLayout;
    private LinearLayout horizontalLayout;
    private LinearLayout timeSelectionLayout;
    private TextView titleView;
    private TextView axisView;
    private TextView timeSecondsView;
    private LineChart chart;

    private SeekBar slider;




    //Class containing information about a certain data from the graph, allowing to update it
    private class Data {

        private LineDataSet lineDataSet;

        Data(String nameID, int color, List<Entry> entryList){
            this.lineDataSet = new LineDataSet(entryList, nameID);
            this.lineDataSet.setColor(color);
            this.lineDataSet.setCircleColor(color);
        }

        public LineDataSet getDataSet(){
            return this.lineDataSet;
        }

    }


    private class SliderChangeListener implements SeekBar.OnSeekBarChangeListener {
        private int timeSelected;

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            timeSelected = i + 60;
            int minutesShow = timeSelected/60;
            int secondsShow = timeSelected%60;
            String appendSecondStr = " seconds";
            if (secondsShow < 10){
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
            updateAxisScale();
        }
    }



    private void initializeDataList(){
        this.dataList = new ArrayList<Data>();
        int[] colors = {Color.RED,Color.GREEN,Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
        //TODO: get real data

        for (int can = 0; can < canList.size(); can++){
            List<Entry> entries = new ArrayList<Entry>();
            for (int i = 0; i < 5*seconds; i++) {
                // turn data into Entry objects
                entries.add(new Entry(i+can, i));
            }
            dataList.add(new Data(canList.get(can).getId(), colors[can], entries));
        }
    }


    private void initializeViews(){
        createTitle();
        createAxis();
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

    Plot(Context context, String name, String unit, String axis, List<CAN> list) {

        super(context, list);
        this.name = name;
        this.unit = unit;
        this.axis = axis;
        this.canList = list;
        this.context = context;
        this.seconds = 60;
        this.chart = new LineChart(context);

        initializeDataList();

        generatePlot();
        setGenericPlotSettings();

        initializeViews();

    }

    private void createAxis(){

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

    private void createTitle(){

        this.titleView = new TextView(context);

        if(name.equals("")){
            this.titleView.setText("this graph has no name");
        } else {
            this.titleView.setText(name);
        }
        this.titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        this.titleView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

    }

    private void createSlider(){
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
        slider.setMax(240);
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SliderChangeListener();
        slider.setOnSeekBarChangeListener(seekBarChangeListener);
        LayoutParams sliderLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        sliderLayoutParams.weight = 1;
        slider.setLayoutParams(sliderLayoutParams);
    }

    private void generatePlot() {

        LineData lineData = new LineData();
        List<ILineDataSet> lines = new ArrayList<ILineDataSet> ();
        for (Data data : dataList){
            lines.add(data.getDataSet());
        }
        String[] xAxis = new String[] {"0", "1", "2", "3", "4", "5", "6", "8", "9"};
        this.chart.setData(new LineData(lines));
        this.chart.invalidate(); // refresh

    }

    private void updateAxisScale(){
        XAxis xAxis = this.chart.getXAxis();
        xAxis.setAxisMaximum(seconds);
        xAxis.setAxisMinimum(0);
        this.chart.invalidate(); // refresh
    }

    private void setGenericPlotSettings() {
        //no interaction
        this.chart.setTouchEnabled(false);

        this.chart.setNoDataText("No data");
        Description desc = new Description();
        desc.setText("Graph description");
        this.chart.setDescription(desc);

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
