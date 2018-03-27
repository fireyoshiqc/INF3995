package ca.polymtl.inf3995.oronos.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import ca.polymtl.inf3995.oronos.R;

/**
 * Created by Fabri on 2018-03-27.
 */

public class PdfActivity extends DrawerActivity {
    public static final String MESSAGE = "pdf list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        //onCreateDrawer();

    }


}
