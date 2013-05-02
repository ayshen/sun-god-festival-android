package edu.ucsd.sgf.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;


public class MapView extends View {

    public MapView(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(0xff0000ff);
    }
}
