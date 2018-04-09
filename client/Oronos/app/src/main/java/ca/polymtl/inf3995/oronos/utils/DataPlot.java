package ca.polymtl.inf3995.oronos.utils;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Data Plot</h1>
 * Class to be used by Plot view
 *
 * @author Fabrice Charbonneau
 * @version 0.0
 * @see ca.polymtl.inf3995.oronos.widgets.views.Plot
 * @since 2018-04-12
 */

public class DataPlot {
    private static final int ONE_SECOND_IN_MILLIS = 1000;
    private final int maxEntries;
    private int nEntries;
    private List<Double> entriesList;
    private long lastEntryTime = 0;

    public DataPlot(int maxEntries) {
        this.maxEntries = maxEntries;
        entriesList = new ArrayList<>();
    }

    public void addEntry(double value) {
        long newEntryTime = System.currentTimeMillis();
        if (newEntryTime < lastEntryTime + ONE_SECOND_IN_MILLIS) {
            // We don't add the value if not enough time has gone by
            return;
        }

        fillNoData(newEntryTime);
        lastEntryTime = newEntryTime;

        if (nEntries == maxEntries) {
            entriesList.remove(0);
        }
        entriesList.add(value);
        nEntries = Math.min(nEntries + 1, maxEntries);
    }

    private void fillNoData(long newEntryTime) {
        /*
         * We want to always have a value corresponding to a second
         * if a second or more has gone by since the last received value, duplicate the last value for
         * the number of seconds that has gone by
         */
        if (entriesList.isEmpty()) {
            return;
        }

        if (newEntryTime > lastEntryTime + ONE_SECOND_IN_MILLIS) {
            long rest = lastEntryTime;
            while (rest > newEntryTime) {
                rest += ONE_SECOND_IN_MILLIS;
                entriesList.add(entriesList.get(0));
                if (nEntries == maxEntries) {
                    entriesList.remove(0);
                }
            }
        }
    }

    public List<Entry> retrieveEntries(int amount) {
        List<Entry> formattedEntries = new ArrayList<>();
        if (amount > maxEntries) {
            return formattedEntries;
        }
        if (entriesList.isEmpty()) {
            return formattedEntries;
        }

        int index = 0;

        int actualAmount = amount;

        if (amount > nEntries) {
            actualAmount = nEntries;
        }

        for (int i = nEntries - actualAmount; i < nEntries; i++) {
            double entryd = entriesList.get(i);
            float entryf = (float) entryd;

            //TODO: Remplacer ceci?
            // GROS HACK SALE DE LA MORT QUI TUE. JE RÉCITE 20 'JE VOUS SALUE MARIE' AVANT D'ALLER
            // ME COUCHER À TOUS LES SOIRS TANT QUE CE BOUT DE CODE EXISTE!!!
            if (Math.abs(entryf) < 1.0e-20)
                entryf = 0.0f;

            formattedEntries.add(new Entry(index - actualAmount, entryf));
            index++;
        }
        return formattedEntries;
    }

}
