package io.knows.saturn.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

/**
 * Created by ryun on 15-5-5.
 */
public class RoundedBackgroundSpan extends ReplacementSpan {

    float mMargin = 20f;
    float mPadding = 50.0f;
    final Paint mPaintBackground;

    public RoundedBackgroundSpan(int color) {
        mPaintBackground = new Paint();
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setColor(color);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end) + mPadding + mMargin);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int width = (int) paint.measureText(text, start, end);
        RectF rect = new RectF(x, top, x + width + mPadding, bottom);
        canvas.drawRoundRect(rect, 10, 10, mPaintBackground);

//        paint.setColor(mTextColor); //use the default text paint to preserve font size/style
        int xPos = Math.round(x + (mPadding / 2));
        canvas.drawText(text, start, end, xPos, y, paint);
    }
}
