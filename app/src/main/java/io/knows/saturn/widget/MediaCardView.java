package io.knows.saturn.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by ryun on 15-4-22.
 */
public class MediaCardView extends CardView {

    public MediaCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec + 50);
    }
}
