package it.polimi.expogame.support;

/**
 * Created by degrigis on 29/01/15.
 */
import android.content.Context;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

import it.polimi.expogame.R;

public class GIFview extends View {

    private Movie mMovie;

    public GIFview(Context context) {
        super(context);
        initializeView();
    }

    public GIFview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public GIFview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeView();
    }

    private void initializeView() {


    }

}
