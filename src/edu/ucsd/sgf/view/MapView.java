package edu.ucsd.sgf.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;


public class MapView extends View {

    private Paint mPaint = new Paint();

    public MapView(Context context) {
        super(context);
        mPaint.setColor(0xffffffff);
        mPaint.setStrokeWidth(4);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(0xff0000ff);
        canvas.drawLine(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
        canvas.drawLine(0, canvas.getHeight(), canvas.getWidth(), 0, mPaint);
    }
}
