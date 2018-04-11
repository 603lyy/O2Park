package com.yaheen.o2park.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yaheen.o2park.util.ValueAnimatorUtils;

import java.util.Random;

public class NameGroupView extends RelativeLayout {

    private NameView nameView;

    public NameGroupView(Context context) {
        super(context);
        init();
    }

    public NameGroupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NameGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    public void addName(String name) {
        nameView = new NameView(getContext(), name);
        Random random = new Random();

        int index = random.nextInt(120);
        while (index == 44 || index == 45 || index == 50 || index == 51 || index == 56
                || index == 57 || index == 62 || index == 63) {
            index = random.nextInt(120);
        }
        int row = index / 6 + 1;
        int column = index % 6;
        int endX = getWidth() / 6 * column ;
        int endY = getHeight() / 20 * row - getHeight() / 40;

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(nameView, params);

        //二、计算动画开始/结束点的坐标的准备工作
        //得到父布局的长宽
        int[] parentLocation = new int[2];
        parentLocation[0] = getWidth();
        parentLocation[1] = getHeight();

        //得到名字起始的坐标（用于计算动画开始的坐标）
        int startLoc[] = new int[2];
        startLoc[0] = getWidth() / 2;
        startLoc[1] = getHeight() / 2;

        //得到名字结束的坐标(用于计算动画结束后的坐标)
        int endLoc[] = new int[2];
        endLoc[0] = endX;
        endLoc[1] = endY;

        ValueAnimatorUtils.transferName(nameView, startLoc, endLoc, parentLocation);
    }
}
