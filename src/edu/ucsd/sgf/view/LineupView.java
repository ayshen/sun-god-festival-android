package edu.ucsd.sgf.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import edu.ucsd.sgf.R;
import edu.ucsd.sgf.util.Performance;


public class LineupView extends View {

    private final static int TRANSPARENT = 0x00000000;

    private int width = 0;
    private int height = 0;

    private Bitmap mBitmap = null;
    private Canvas mCanvas = new Canvas();
    private Paint mPaint = new Paint();

    private int backgroundColor = TRANSPARENT;
    private int primaryEventBackgroundColor = TRANSPARENT;
    private int secondaryEventBackgroundColor = TRANSPARENT;
    private int textColor = TRANSPARENT;

    private float scrollOffset = 0.0f;
    private float maxScrollOffset = 0.0f;


    public LineupView(Context context) {
        super(context);
        Resources res = context.getResources();

        mPaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12,
                res.getDisplayMetrics()));

        backgroundColor = res.getColor(R.color.lineup_background_color);
        primaryEventBackgroundColor = res.getColor(
                R.color.lineup_primary_event_background_color);
        secondaryEventBackgroundColor = res.getColor(
                R.color.lineup_secondary_event_background_color);
        textColor = res.getColor(R.color.lineup_text_color);
    }


    @Override
    protected void onSizeChanged(int w, int h, int _w, int _h) {
        width = w;
        height = h;

        if(mBitmap != null)
            mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        mCanvas = new Canvas(mBitmap);

        invalidate();
    }


    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, null,
                new Rect(0, 0, canvas.getWidth(), canvas.getHeight()),
                null);
    }
}
