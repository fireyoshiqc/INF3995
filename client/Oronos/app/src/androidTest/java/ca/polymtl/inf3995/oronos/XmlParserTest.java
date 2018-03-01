package ca.polymtl.inf3995.oronos;

/**
 * Created by Felix on 22/févr./2018.
 */

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import ca.polymtl.inf3995.oronos.parser.OronosXmlParser;
import ca.polymtl.inf3995.oronos.parser.Rocket;
import ca.polymtl.inf3995.oronos.parser.UnsupportedContainerWidgetException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class XmlParserTest {

    OronosXmlParser oxp;

    @Before
    public void setup() {
        oxp = new OronosXmlParser();
    }

    @Test
    public void canParseGoodFile() throws UnsupportedContainerWidgetException, XmlPullParserException, IOException {
        InputStream fis = this.getClass().getClassLoader().getResourceAsStream("10_polaris.xml");
        assertThat(oxp.parse(fis), instanceOf(Rocket.class));
        fis = this.getClass().getClassLoader().getResourceAsStream("11_valkyrieM2.xml");
        assertThat(oxp.parse(fis), instanceOf(Rocket.class));
    }

    @Test(expected = UnsupportedContainerWidgetException.class)
    public void throwsExceptionWhenHVWidgetIsWrong() throws UnsupportedContainerWidgetException, XmlPullParserException, IOException {
        InputStream fis = this.getClass().getClassLoader().getResourceAsStream("12_borked.xml");
        oxp.parse(fis);
    }

    /*
    @Test
    public void canDigDownToCAN() throws UnsupportedContainerWidgetException, XmlPullParserException, IOException {
        InputStream fis = this.getClass().getClassLoader().getResourceAsStream("11_valkyrieM2.xml");
        Rocket rocket = oxp.parse(fis);
        ContainableWidget contents = rocket.list.get(0).list.get(0).tabContainer.list.get(0).contents;
        assertThat(((AbstractWidgetContainer<AbstractCANContainer>) contents).list.get(0).list.get(0), instanceOf(CAN.class));
    }
    */
}