package ca.polymtl.inf3995.oronos.utils;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabri on 2018-03-17.
 */

//Class to be used in Plot.java
public class DataPlot {
    private final int ONE_SECOND_IN_MILLIS = 1000;

    private int nEntries;
    private List<Double> entriesList;
    private int MAX_ENTRIES;
    private long lastEntryTime = 0;

    public DataPlot(int maxEntries) {
        this.MAX_ENTRIES = maxEntries;
        entriesList = new ArrayList<Double>();
    }

    public void addEntry(double value) {
        long newEntryTime = System.currentTimeMillis();
        if(newEntryTime < lastEntryTime + ONE_SECOND_IN_MILLIS){
            return; //We don't add the value if not enough time has gone by
        }

        fillNoData(newEntryTime);
        lastEntryTime = newEntryTime;

        if (nEntries == MAX_ENTRIES){
            entriesList.remove(0);
        }
        entriesList.add(value);
        nEntries = Math.min(nEntries + 1, MAX_ENTRIES);
    }

    private void fillNoData(long newEntryTime){ //We want to always have a value corresponding to a second
        //if a second or more has gone by since the last received value, duplicate the last value for
        //the number of seconds that has gone by
        if(entriesList.isEmpty()){
            return;
        }

        if(newEntryTime > lastEntryTime + ONE_SECOND_IN_MILLIS){
            long rest = lastEntryTime;
            while(rest > newEntryTime){
                rest += ONE_SECOND_IN_MILLIS;
                entriesList.add(entriesList.get(0));
                if (nEntries == MAX_ENTRIES){
                    entriesList.remove(0);
                }
            }
        }
    }

    public List<Entry> retrieveEntries(int amount){
        if (amount > MAX_ENTRIES){
            return null;
        }
        List<Entry> formattedEntries = new ArrayList<Entry>();
        int index = 0;

        int actualAmount = amount;

        if(amount > nEntries){
            actualAmount = nEntries;
        }

        for (int i = nEntries - actualAmount; i < nEntries; i++){
            double entryd = entriesList.get(i);
            float entryf = (float)entryd;
            formattedEntries.add(new Entry(index - actualAmount, entryf));
            index++;
        }
        return formattedEntries;
    }

}
