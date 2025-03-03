package ru.ampstudy.bible.component.immutable.box;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;

public class TopFadeEdgeScrollView extends NestedScrollView {

    public TopFadeEdgeScrollView(Context context) {
        super(context);
    }

    public TopFadeEdgeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopFadeEdgeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return 0.0f;
    }
}