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

    private float fontHeight = 0.0f;
    private float padding = 0.0f;


    /** Create a lineup view for a given lineup, possibly with a larger range
    of time to display.
    @param context {@link android.content.Context} object for calling the
    superclass constructor.
    @param lineup array of {@link edu.ucsd.sgf.util.Performance} objects
    representing the schedule for the stage for which this lineup view is
    intended.
    @param timeRange expanded length of time to display.
    */
    public static LineupView instantiate(Context context,
            Performance[] lineup, int timeRange) {

        LineupView v = new LineupView(context);

        // Special case for no lineup data.
        if(lineup == null) {
            v.mLineup = null;
            v.baseMaxY = 0;
            return v;
        }

        // Set up the lineup data.
        v.mLineup = lineup;
        java.util.Arrays.sort(v.mLineup,
                new java.util.Comparator<Performance>() {
            @Override
            public int compare(Performance p1, Performance p2) {
                return p2.end.compareTo(p1.end);
            }
        });

        // Compute the height of the renderable content at the minimum zoom
        // level. This serves as a delimiter for scrolling.
        v.baseMaxY = timeRange / LineupViewPager.MINUTES_PER_TEXT_LINE *
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                LineupViewPager.FONT_SIZE,
                context.getResources().getDisplayMetrics());
        return v;
    }


    /** Default view constructor.
    Set up some values like the five-minute metric and the color palette.
    */
    public LineupView(Context context) {
        super(context);
        Resources res = context.getResources();

        // Figure out how tall a five-minute section is at the minimum zoom
        // level. The font size is determined by the parent LineupViewPager.
        mPaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, LineupViewPager.FONT_SIZE,
                res.getDisplayMetrics()));

        // Load the color palette from the resources.
        backgroundColor = res.getColor(R.color.lineup_background_color);
        primaryEventBackgroundColor = res.getColor(
                R.color.lineup_primary_event_background_color);
        secondaryEventBackgroundColor = res.getColor(
                R.color.lineup_secondary_event_background_color);
        textColor = res.getColor(R.color.lineup_text_color);

        // Compute some constants.
        fontHeight = mPaint.descent() - mPaint.ascent();
        padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                PADDING, getContext().getResources().getDisplayMetrics());
    }


    @Override
    protected void onSizeChanged(int w, int h, int _w, int _h) {
        // Save the new dimensions for future reference.
        width = w;
        height = h;

        // Recreate the backing bitmap and renderer canvase.
        if(mBitmap != null)
            mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        mCanvas = new Canvas(mBitmap);

        // Redraw.
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
        // Update internal variables for the scroll offset and zoom level.
        this.scrollOffset = scrollOffset;
        this.zoom = zoom;

        // Compute the height of a five-minute block.
        float fontScale = fontHeight * zoom /
                LineupViewPager.MINUTES_PER_TEXT_LINE;

        // Prepare some state variables for rendering.
        float offset = -scrollOffset;
        int bg = primaryEventBackgroundColor;
        Time lastBegin = new Time(24, 0);

        // Special case: no lineup data.
        if(mLineup == null) {
            mCanvas.drawColor(backgroundColor);
            return;
        }

        for(Performance p: mLineup) {
            // Compute heights for the changeover between the previous
            // performance (if any) and for the current performance.
            float changeoverHeight = lastBegin.compareTo(p.end) * fontScale;
            float eventHeight = p.end.compareTo(p.begin) * fontScale;

            // Draw changeover (if any).
            mPaint.setColor(backgroundColor);
            mCanvas.drawRect(0, offset, width, offset += changeoverHeight,
                    mPaint);

            // Set event background color. This is more complicated than it
            // needs to be because we want zero-changeover performance pairs to
            // be distinguishable.
            if(lastBegin.equals(p.end)) {
                if(bg == primaryEventBackgroundColor)
                    bg = secondaryEventBackgroundColor;
                else bg = primaryEventBackgroundColor;
            }
            else bg = primaryEventBackgroundColor;

            // Draw the event background.
            mPaint.setColor(bg);
            mCanvas.drawRect(0, offset, width,
                    offset + eventHeight, mPaint);

            // Draw the text for the artist.
            mPaint.setColor(textColor);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mCanvas.drawText(p.artist,
                    padding,
                    offset + 0.5f * (eventHeight + fontHeight) - mPaint.descent(),
                    mPaint);

            // Draw the text for the performance begin and end times.
            String timeString = p.begin.to(p.end);
            mPaint.setTypeface(Typeface.DEFAULT);
            mCanvas.drawText(timeString,
                    width - padding - mPaint.measureText(timeString),
                    offset + 0.5f * (eventHeight + fontHeight) - mPaint.descent(),
                    mPaint);

            // Update state variables.
            offset += eventHeight;
            lastBegin = p.begin;
        }

        // Draw the last bit of background that occurs before any performance.
        mPaint.setColor(backgroundColor);
        mCanvas.drawRect(0, offset, width, zoom * baseMaxY, mPaint);

        // Force the changes to become visible.
        invalidate();
    }


    public boolean onSingleTapUp(MotionEvent e) {

        // Figure out the total offset Y. Notice that we need to compensate for
        // the PagerTabStrip that shows the stage names.
        float targetY = e.getY() + scrollOffset -
                LineupViewPager.PAGERTABSTRIP_HEIGHT;

        // Scale the total offset Y into time space. Make sure we compensate
        // for the zoom level.
        float timeBeforeEnd = targetY / zoom / fontHeight *
                LineupViewPager.MINUTES_PER_TEXT_LINE;

        // Figure out the time to which the touch event corresponds.
        float targetTime = (new Time(24, 0)).intValue() - timeBeforeEnd;

        // Try to find a performance that contains the target time. If there
        // is one, do something special.
        for(Performance p: mLineup) {
            if(p.begin.intValue() <= targetTime &&
                    p.end.intValue() >= targetTime) {
                android.widget.Toast.makeText(
                        getContext(),
                        p.artist,
                        android.widget.Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
}
