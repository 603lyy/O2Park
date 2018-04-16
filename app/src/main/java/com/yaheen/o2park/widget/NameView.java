package com.yaheen.o2park.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.util.AttributeSet;

public class NameView extends android.support.v7.widget.AppCompatTextView {

    private NameGroupView groupView;

    public NameView(Context context, SpannableString name, NameGroupView groupView) {
        super(context);
        setText(name);
        this.groupView = groupView;
    }

    public NameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        groupView.setTextHeight(getHeight());
    }
}
