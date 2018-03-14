package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.R;


/**
 * Created by Felix on 15/févr./2018.
 */

public class Plot extends AbstractWidgetContainer<CAN> {

    private final String name;
    private final String unit;
    private final String axis;

    private final int CHART_HEIGHT = 400; //match_parent is not working like we want it to for the height

    private Context context;

    private LinearLayout containerLayout;
    private TextView textView;
    private LineChart chart;

    Plot(Context context, String name, String unit, String axis, List<CAN> list) {

        super(context, list);
        this.name = name;
        this.unit = unit;
        this.axis = axis;
        this.context = context;

        generatePlot();
        setGenericSettings();

        //TODO: linear vertical layout, add title textview on top of the chart
        containerLayout = new LinearLayout(context);

        addView(chart);


    }

    private void createTitle(){

        this.textView = new TextView(context);

        this.textView.setText(name);
        this.textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));


    }

    private void generatePlot() {

        this.chart = new LineChart(context);
        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < 10; i++) {
            // turn data into Entry objects
            entries.add(new Entry(i, i));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        LineData lineData = new LineData(dataSet);
        this.chart.setData(lineData);
        this.chart.invalidate(); // refresh

        //For each datasets in graph, set a different color to them

    }

    private void setGenericSettings() {

        this.chart.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                CHART_HEIGHT
        ));
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
