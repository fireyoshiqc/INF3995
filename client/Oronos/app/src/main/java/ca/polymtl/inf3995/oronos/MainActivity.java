package ca.polymtl.inf3995.oronos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.CookieHandler;
import java.net.CookieManager;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import ca.polymtl.inf3995.oronos.parser.DualVWidget;
import ca.polymtl.inf3995.oronos.parser.OronosXmlParser;
import ca.polymtl.inf3995.oronos.parser.Rocket;
import ca.polymtl.inf3995.oronos.parser.Tab;
import ca.polymtl.inf3995.oronos.parser.TabContainer;
import ca.polymtl.inf3995.oronos.parser.UnsupportedContainerWidgetException;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CookieHandler.setDefault(new CookieManager());

        OronosXmlParser parser = new OronosXmlParser(getWindow().getContext());
        try {
            InputStream fis = getAssets().open("10_polaris.xml");
            Rocket rocket = parser.parse(fis);
            //setContentView(rocket.getList().get().getView());

            TabContainer tabtest = (TabContainer)rocket.getList().get(0);
            for (Tab tab : tabtest.getList()) {
                ((DualVWidget)tab.getContents()).buildContents();
            }
            tabtest.buildTabs(getWindow().getContext());
            setContentView(tabtest.getView());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedContainerWidgetException e) {
            e.printStackTrace();
        }

        //setContentView(R.layout.activity_main);

        Timber.plant(new LogTree());

    }


}