package ca.polymtl.inf3995.oronos.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

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
        changeToolbarTitle("PDF downloads");
        TextView pdfTextViewTitle = findViewById(R.id.pdf_title);
        pdfTextViewTitle.setText("List of PDFs available for download: ");

    }


}
