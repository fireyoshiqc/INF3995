package ca.polymtl.inf3995.oronos.parser;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabri on 2018-03-17.
 */

//Class to be used in Plot.java
public class DataPlot {
    private int nEntries;
    private List<Integer> entriesList;
    private int MAX_ENTRIES;

    public DataPlot(int maxEntries) {
        this.MAX_ENTRIES = maxEntries;
        entriesList = new ArrayList<Integer>();
    }

    public void addEntry(int value) {

        if (nEntries == MAX_ENTRIES){
            entriesList.remove(0);
        }
        entriesList.add(value);
        nEntries = Math.min(nEntries + 1, MAX_ENTRIES);
    }

    public List<Entry> retrieveEntries(int amount){
        if (amount > MAX_ENTRIES){
            return null;
        }
        List<Entry> formattedEntries = new ArrayList<Entry>();
        int index = 0;
        for (int i = nEntries - amount; i < nEntries; i++){
            formattedEntries.add(new Entry(index, entriesList.get(i)));
            index++;
        }
        return formattedEntries;
    }

}
