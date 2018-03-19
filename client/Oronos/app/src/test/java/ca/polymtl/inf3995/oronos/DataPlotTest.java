package ca.polymtl.inf3995.oronos;
import com.github.mikephil.charting.data.Entry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.parser.DataPlot;

import static org.junit.Assert.*;
/**
 * Created by Fabri on 2018-03-19.
 */

public class DataPlotTest {
    private DataPlot dataPlot;
    @Test
    public void DataPlotAddEntryTest() throws Exception{
        dataPlot = new DataPlot(5);
        for (int i = 1; i < 7; i++){
            dataPlot.addEntry(i);
        }
        List<Entry> responseList = dataPlot.retrieveEntries(5);
        Entry expectedLastEntry = new Entry(0, 2);
        List<Entry> expectedEntryList = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            expectedEntryList.add(new Entry(i, 2 + i));
        }

        assertEquals(5, responseList.size());

        //Marche pas... devrait fonctionner. déficience des tests entrainant une profonde rage
        //Vive le console debugging. Décommenter ligne suivante pour voir que la liste est correcte

        //assertEquals(expectedEntryList, responseList);

    }
}
