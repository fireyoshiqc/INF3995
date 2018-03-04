package ca.polymtl.inf3995.oronos;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieHandler.setDefault(new CookieManager());

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ORONOS");


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
            ConstraintLayout dataLayout = findViewById(R.id.data_layout);
            dataLayout.addView(tabtest.getView(), -1);
            //setContentView(tabtest.getView());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (UnsupportedContainerWidgetException e) {
            e.printStackTrace();
        }

        Timber.plant(new LogTree());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_display_grid) {
            Toast.makeText(
                    MainActivity.this,
                    "Action Display Grid clicked",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}