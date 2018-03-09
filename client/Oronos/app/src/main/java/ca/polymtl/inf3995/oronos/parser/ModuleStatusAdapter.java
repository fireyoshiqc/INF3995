package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ModuleStatusAdapter extends BaseAdapter {
    private Context mContext;
    private int nGrid;
    private int nColumns;
    private List<PCBPair> PCBList;

    public ModuleStatusAdapter(Context c, int nGrid, int nColumns) {
        mContext = c;
        this.nGrid = nGrid;
        this.nColumns = nColumns;
        PCBList = new ArrayList<>();
    }

    public int getCount() {
        return this.nGrid * this.nColumns;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public void addItem(String PCBname, int noSerial) {
        if (PCBList.size() < getCount()) {
            PCBPair pcbPair = new PCBPair(PCBname, noSerial);
            PCBList.add(pcbPair);
        }
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        CardView cardView;
        TextView textView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            textView = new TextView(mContext);
            textView.setPadding(8, 8, 8, 8);
            textView.setText(PCBList.get(position).getPCBName()
                    + "\n"
                    + Integer.toString(PCBList.get(position).getNoSerial()) );
            cardView = new CardView(mContext);
            cardView.setCardBackgroundColor(Color.GREEN);
            cardView.addView(textView);
        } else {
            cardView = (CardView) convertView;
        }

        ((TextView)cardView.getChildAt(0)).setText(PCBList.get(position).getPCBName()
                + "\n"
                + Integer.toString(PCBList.get(position).getNoSerial()) );
        return cardView;
    }

    private class PCBPair
    {
        private final String name;
        private final int noSerial;

        public PCBPair(String aName, int aNoSerial)
        {
            name   = aName;
            noSerial = aNoSerial;
        }

        public String getPCBName()   { return name; }
        public int getNoSerial() { return noSerial; }
    }
}

