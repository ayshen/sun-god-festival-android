package edu.ucsd.sgf.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import edu.ucsd.sgf.R;
import edu.ucsd.sgf.util.Performance;
import edu.ucsd.sgf.util.Time;
import edu.ucsd.sgf.view.LineupViewPager;


public class LineupView extends View {

    private final static int TRANSPARENT = 0x00000000;
    private final static int PADDING = 8;

    private Performance[] mLineup = null;

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
    private float zoom = 1.0f;
    private float baseMaxY = 0.0f;


    public static LineupView instantiate(Context context,
            Performance[] lineup, int timeRange) {
        LineupView v = new LineupView(context);
        if(lineup == null) {
            v.mLineup = null;
            v.baseMaxY = 0;
            return v;
        }
        v.mLineup = lineup;
        java.util.Arrays.sort(v.mLineup,
                new java.util.Comparator<Performance>() {
            @Override
            public int compare(Performance p1, Performance p2) {
                return p2.end.compareTo(p1.end);
            }
        });
        v.baseMaxY = timeRange / LineupViewPager.MINUTES_PER_TEXT_LINE *
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                LineupViewPager.FONT_SIZE,
                context.getResources().getDisplayMetrics());
        return v;
    }


    public LineupView(Context context) {
        super(context);
        Resources res = context.getResources();

        mPaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, LineupViewPager.FONT_SIZE,
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

        update(scrollOffset, zoom);
        invalidate();
    }


    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, null,
                new Rect(0, 0, canvas.getWidth(), canvas.getHeight()),
                null);
    }


    public void update(float scrollOffset, float zoom) {
        this.scrollOffset = scrollOffset;
        this.zoom = zoom;

        float fontHeight = mPaint.descent() - mPaint.ascent();
        float fontScale = fontHeight * zoom /
                LineupViewPager.MINUTES_PER_TEXT_LINE;
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                PADDING, getContext().getResources().getDisplayMetrics());
        float offset = -scrollOffset;
        int bg = primaryEventBackgroundColor;
        Time lastBegin = new Time(24, 0);

        if(mLineup == null) {
            mCanvas.drawColor(backgroundColor);
            return;
        }

        for(Performance p: mLineup) {
            float changeoverHeight = lastBegin.compareTo(p.end) * fontScale;
            float eventHeight = p.end.compareTo(p.begin) * fontScale;

            mPaint.setColor(backgroundColor);
            mCanvas.drawRect(0, offset, width, offset += changeoverHeight,
                    mPaint);

            if(lastBegin.equals(p.end)) {
                if(bg == primaryEventBackgroundColor)
                    bg = secondaryEventBackgroundColor;
                else bg = primaryEventBackgroundColor;
            }
            else bg = primaryEventBackgroundColor;
            mPaint.setColor(bg);
            mCanvas.drawRect(0, offset, width,
                    offset + eventHeight, mPaint);

            mPaint.setColor(textColor);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mCanvas.drawText(p.artist,
                    padding,
                    offset + 0.5f * (eventHeight + fontHeight) - mPaint.descent(),
                    mPaint);

            String timeString = p.begin.to(p.end);
            mPaint.setTypeface(Typeface.DEFAULT);
            mCanvas.drawText(timeString,
                    width - padding - mPaint.measureText(timeString),
                    offset + 0.5f * (eventHeight + fontHeight) - mPaint.descent(),
                    mPaint);

            offset += eventHeight;
            lastBegin = p.begin;
        }

        mPaint.setColor(backgroundColor);
        mCanvas.drawRect(0, offset, width, zoom * baseMaxY, mPaint);

        invalidate();
    }
}
