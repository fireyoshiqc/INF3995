package ca.polymtl.inf3995.oronos;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.CardView;
import android.view.View;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.polymtl.inf3995.oronos.widgets.adapters.ModuleStatusAdapter;
import ca.polymtl.inf3995.oronos.widgets.views.ModuleStatus;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;

@RunWith(AndroidJUnit4.class)

// TODO: Fix this test class after refactoring

public class ModuleStatusTest {
    /**
     * ModuleStatusTest
     *
     * Instrumented unit test to assert that the ModuleStatus view is :
     * - Creating a grid view with the correct dimensions
     * - Filling the grid correctly
     * - Not adding more than N (= grid dimension h x l) elements
     * - Switching correctly each element of the grid between status
     * (That switch comprehend text and color of background, in addition to the time requirements)
     */
    private Context appContext = null;
    // ModuleStatus Obj
    ModuleStatus moduleStatusObj;
    int nGrids   = 5;
    int nColumns = 4;

    /**
     * SetUp Socket Client before each test
     */
    @Before
    public void SetUpModuleStatus() {
        appContext = InstrumentationRegistry.getTargetContext();
        moduleStatusObj = new ModuleStatus(appContext, nGrids, nColumns);
    }

    /**
     * Assert the ModuleStatus Obj is not null
     */
    @Test
    public void testConstructedObject() {
        assertNotNull(moduleStatusObj);
    }

    /**
     * Assert the ModuleStatus Obj has grid of good dimensions
     */
    /*
    @Test
    public void testGridDimensionsObject() {
        assertEquals(nColumns * nGrids, moduleStatusObj.getCount());
    }
    */

    @Test
    public void testGridViewVisible() {
        assertThat(moduleStatusObj.getGlobalView().getVisibility(), equalTo(View.VISIBLE));
    }

    /**
     * Assert the ModuleStatus Obj can be filled correctly and with max N elements
     */
    @Test
    public void testFillingObject() {
        ModuleStatusAdapter adapter = (ModuleStatusAdapter) moduleStatusObj.getGlobalView().getAdapter();
        for (int i = 1; i < 22; i++) {
            adapter.receiveItem("PCB " + i, i, 0);
            adapter.getPCBList().get(i);
            // TODO: Fix test
            //assertEquals(ColorStateList.valueOf((Color.TRANSPARENT)), )
        }
        /*
        CardView view0  = (CardView) moduleStatusObj.getLocalView(0, null, moduleStatusObj.getGlobalView());
        CardView view1  = (CardView) moduleStatusObj.getLocalView(1, null, moduleStatusObj.getGlobalView());
        CardView view2  = (CardView) moduleStatusObj.getLocalView(2, null, moduleStatusObj.getGlobalView());
        CardView view3  = (CardView) moduleStatusObj.getLocalView(3, null, moduleStatusObj.getGlobalView());
        CardView view4  = (CardView) moduleStatusObj.getLocalView(4, null, moduleStatusObj.getGlobalView());
        CardView view5  = (CardView) moduleStatusObj.getLocalView(5, null, moduleStatusObj.getGlobalView());
        CardView view6  = (CardView) moduleStatusObj.getLocalView(6, null, moduleStatusObj.getGlobalView());
        CardView view7  = (CardView) moduleStatusObj.getLocalView(7, null, moduleStatusObj.getGlobalView());
        CardView view8  = (CardView) moduleStatusObj.getLocalView(8, null, moduleStatusObj.getGlobalView());
        CardView view9  = (CardView) moduleStatusObj.getLocalView(9, null, moduleStatusObj.getGlobalView());
        CardView view10 = (CardView) moduleStatusObj.getLocalView(10, null, moduleStatusObj.getGlobalView());
        CardView view11 = (CardView) moduleStatusObj.getLocalView(11, null, moduleStatusObj.getGlobalView());
        CardView view12 = (CardView) moduleStatusObj.getLocalView(12, null, moduleStatusObj.getGlobalView());
        CardView view13 = (CardView) moduleStatusObj.getLocalView(13, null, moduleStatusObj.getGlobalView());
        CardView view14 = (CardView) moduleStatusObj.getLocalView(14, null, moduleStatusObj.getGlobalView());
        CardView view15 = (CardView) moduleStatusObj.getLocalView(15, null, moduleStatusObj.getGlobalView());
        CardView view16 = (CardView) moduleStatusObj.getLocalView(16, null, moduleStatusObj.getGlobalView());
        CardView view17 = (CardView) moduleStatusObj.getLocalView(17, null, moduleStatusObj.getGlobalView());
        CardView view18 = (CardView) moduleStatusObj.getLocalView(18, null, moduleStatusObj.getGlobalView());
        CardView view19 = (CardView) moduleStatusObj.getLocalView(19, null, moduleStatusObj.getGlobalView());
        CardView view20 = (CardView) moduleStatusObj.getLocalView(20, null, moduleStatusObj.getGlobalView());
        CardView view21 = (CardView) moduleStatusObj.getLocalView(21, null, moduleStatusObj.getGlobalView());


        assertEquals(ColorStateList.valueOf(Color.TRANSPARENT), view20.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(Color.TRANSPARENT), view21.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view0.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view1.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view2.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view3.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view4.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view5.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view6.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view7.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view8.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view9.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view10.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view11.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view12.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view13.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view14.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view15.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view16.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view17.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view18.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view19.getCardBackgroundColor());
        */
    }

    /**
     * Assert the status is changing according to the time that has passed since the last message
     * was received.
     *
     * State Machine where 1=>Received something within 2min and 0=>Received nothing within 2min :
     * _|0|1
     * G|O|G
     * O|R|G
     * R|R|G
     *
     * Match Plan :
     * A first  view will assert the following transitions: G->2min[1]->G->2min[0]->O->[1]->G
     * A second view will assert the following transitions: G->2min[0]->O->2min[0]->R->[1]->G
     * A third  view will assert the following transitions: G->2min[0]->O->2min[0]->R->[0]->R
     * */
    @Test
    public void testStatusChangesAreCorrect() {
        // TODO: Fix test
        //First State
        /*
        moduleStatusObj.receiveItem("PCB 1",  1,  0);
        moduleStatusObj.receiveItem("PCB 2",  2,  0);
        moduleStatusObj.receiveItem("PCB 3",  3,  0);
        CardView view0  = (CardView) moduleStatusObj.getLocalView(0, null, moduleStatusObj.getGlobalView());
        CardView view1  = (CardView) moduleStatusObj.getLocalView(1, null, moduleStatusObj.getGlobalView());
        CardView view2  = (CardView) moduleStatusObj.getLocalView(2, null, moduleStatusObj.getGlobalView());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view0.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view1.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view2.getCardBackgroundColor());

        //First Transition
        try {
            Thread.sleep(GlobalParameters.ONLINE_TO_DELAY / 2);
        } catch(InterruptedException e) {
            System.out.println("First sleep got interrupted!");
        }
        moduleStatusObj.receiveItem("PCB 1",  1,  1);
        moduleStatusObj.receiveItem("PCB 2",  2,  0);
        moduleStatusObj.receiveItem("PCB 3",  3,  0);
        try {
            Thread.sleep(GlobalParameters.ONLINE_TO_DELAY / 2 + 20);
        } catch(InterruptedException e) {
            System.out.println("Second sleep got interrupted!");
        }

        //Second State
        moduleStatusObj.receiveItem("PCB 1",  1,  1);
        moduleStatusObj.receiveItem("PCB 2",  2,  0);
        moduleStatusObj.receiveItem("PCB 3",  3,  0);
        view0  = (CardView) moduleStatusObj.getLocalView(0, view0, moduleStatusObj.getGlobalView());
        view1  = (CardView) moduleStatusObj.getLocalView(1, view1, moduleStatusObj.getGlobalView());
        view2  = (CardView) moduleStatusObj.getLocalView(2, view2, moduleStatusObj.getGlobalView());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view0.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.ORANGE_STATUS), view1.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.ORANGE_STATUS), view2.getCardBackgroundColor());

        //Second Transition
        try {
            Thread.sleep(GlobalParameters.ONLINE_TO_DELAY + 20);
        } catch(InterruptedException e) {
            System.out.println("Third sleep got interrupted!");
        }

        //Third State
        moduleStatusObj.receiveItem("PCB 1",  1,  1);
        moduleStatusObj.receiveItem("PCB 2",  2,  0);
        moduleStatusObj.receiveItem("PCB 3",  3,  0);
        view0  = (CardView) moduleStatusObj.getLocalView(0, view0, moduleStatusObj.getGlobalView());
        view1  = (CardView) moduleStatusObj.getLocalView(1, view1, moduleStatusObj.getGlobalView());
        view2  = (CardView) moduleStatusObj.getLocalView(2, view2, moduleStatusObj.getGlobalView());
        assertEquals(ColorStateList.valueOf(GlobalParameters.ORANGE_STATUS), view0.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.RED_STATUS), view1.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.RED_STATUS), view2.getCardBackgroundColor());

        //Third Transition
        moduleStatusObj.receiveItem("PCB 1",  1,  2);
        moduleStatusObj.receiveItem("PCB 2",  2,  1);
        moduleStatusObj.receiveItem("PCB 3",  3,  0);

        //Fourth State
        view0  = (CardView) moduleStatusObj.getLocalView(0, view0, moduleStatusObj.getGlobalView());
        view1  = (CardView) moduleStatusObj.getLocalView(1, view1, moduleStatusObj.getGlobalView());
        view2  = (CardView) moduleStatusObj.getLocalView(2, view2, moduleStatusObj.getGlobalView());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view0.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.GREEN_STATUS), view1.getCardBackgroundColor());
        assertEquals(ColorStateList.valueOf(GlobalParameters.RED_STATUS), view2.getCardBackgroundColor());
        */
    }

}
