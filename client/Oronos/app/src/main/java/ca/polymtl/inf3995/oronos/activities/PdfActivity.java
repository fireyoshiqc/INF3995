package ca.polymtl.inf3995.oronos.activities;

import android.os.Bundle;
import android.widget.TextView;

import ca.polymtl.inf3995.oronos.R;

/**
 * <h1>Pdf Activity</h1>
 * The Pdf Activity is responsible of managing the PDF list the client gets from the server and to
 * provocate PDF downloads that are requested by the user.
 *
 * @author Fabrice Charbonneau
 * @version 0.0
 * @since 2018-04-12
 */
public class PdfActivity extends DrawerActivity {
    public static final String MESSAGE = "pdf list";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        changeToolbarTitle("PDF downloads");
        TextView pdfTextViewTitle = findViewById(R.id.pdf_title);
        pdfTextViewTitle.setText("List of PDFs available for download: ");

    }


}
