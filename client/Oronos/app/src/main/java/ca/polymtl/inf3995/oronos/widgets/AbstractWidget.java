package ca.polymtl.inf3995.oronos.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Felix on 13/févr./2018.
 */

public abstract class AbstractWidget extends View {

    /**
     * Pour utilisation du Widget à partir de code
     * @param context
     */
    public AbstractWidget(Context context) {
        super(context);
    }

    /**
     * Pour utilisation du Widget à partir d'un fichier XML
     * @param context
     * @param attrs
     */
    public AbstractWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /*
    public AbstractWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AbstractWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    */
}
