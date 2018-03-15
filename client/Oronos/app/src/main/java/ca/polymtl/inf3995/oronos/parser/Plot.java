package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;

import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;



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
    private TextView titleView;
    private TextView axisView;
    private LineChart chart;

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

    private void initializeDataList(){
        this.dataList = new ArrayList<Data>();
        int[] colors = {Color.RED,Color.GREEN,Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
        //TODO: get real data

        for (int can = 0; can < canList.size(); can++){
            List<Entry> entries = new ArrayList<Entry>();
            for (int i = 0; i < 10; i++) {
                // turn data into Entry objects
                entries.add(new Entry(i+can, i));
            }
            dataList.add(new Data(canList.get(can).getId(), colors[can], entries));
        }
    }

    private void initializeViews(){
        createTitle();
        createAxis();
        this.chart.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        this.containerLayout = new LinearLayout(context);
        this.containerLayout.setOrientation(LinearLayout.VERTICAL);
        this.containerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        this.containerLayout.addView(this.titleView);

        this.horizontalLayout = new LinearLayout(context);
        this.horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        this.horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        this.horizontalLayout.addView(this.axisView);
        this.horizontalLayout.addView(this.chart);

        this.containerLayout.addView(this.horizontalLayout);
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

        if(name == ""){
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

    private void generatePlot() {

        this.chart = new LineChart(context);

        LineData lineData = new LineData();
        List<ILineDataSet> lines = new ArrayList<ILineDataSet> ();
        for (Data data : dataList){
            lines.add(data.getDataSet());
        }
        String[] xAxis = new String[] {"0", "1", "2", "3", "4", "5", "6", "8", "9"};
        this.chart.setData(new LineData(lines));
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
