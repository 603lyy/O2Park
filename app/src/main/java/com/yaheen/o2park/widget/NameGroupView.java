package com.yaheen.o2park.widget;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yaheen.o2park.util.ValueAnimatorUtils;

import java.util.Random;

import static android.text.Layout.getDesiredWidth;

public class NameGroupView extends RelativeLayout {

    /**
     * 背景图上边白色区域的高度
     */
//    private float upBlock = 378;
    private float upBlock = 372;

    /**
     * 背景图上圆区域的高度
     */
    private float upCircle = 314;

    /**
     * 背景图內圆区域的高度
     */
    private float innerCircle = 521;

    /**
     * 背景图下半圆区域的高度
     */
    private float downCircle = 314;

    /**
     * 背景图下边白色区域的高度
     */
//    private float downnBlock = 362;
    private float downnBlock = 372;

    /**
     * 背景图右边白色区域的高度
     */
//    private float rightBlock = 390;
    private float rightBlock = 373;

    /**
     * 背景图左边白色区域的高度
     */
//    private float leftBlock = 350;
    private float leftBlock = 372;

    /**
     * 背景图右边小圆的高度
     */
    private float rightSmallCircle = 627;

    /**
     * 背景图整体的高度
     */
    private float totalHeight = 1889;

    /**
     * 背景图整体的宽度
     */
    private float totalwidth = 1889;

    /**
     * 文字的高度
     */
    private float textHeight = 0;

    /**
     * 两行文字的间隔
     */
    private float textIndividual = 10;

    /**
     * 文字内容的长度
     */
    private float textLength = 0;

    /**
     * 文字内容的长度
     */
    private float lastY = 0;

    /**
     * 文字内容的长度
     */
    private float lastX = 0;

    /**
     * 圆半径缩减的举例
     */
    private float circleIndance = 0;

    /**
     * 文字view的数量
     */
    private int viewNum = 0;

    /**
     * 圈数
     */
    private int time = 1;

    /**
     * x轴方向移动的次数
     */
    private int xIndex = 1;

    /**
     * y轴移动的次数
     */
    private int yIndex = 1;

    /**
     * 判断文字显示位置,1右边，2左边，3下中，4上中
     */
    private int textLocation = 1;

    /**
     *
     */
    private int circleIndex = 1;

    /**
     * 文字起始点对于圆所对应的角度
     */
    private int circleDegree = 90;

    /**
     * 文字间的间隔角度
     */
    private int circleDistance = 10;

    private NameView nameView;

    private Animator.AnimatorListener animatorListener;

    public NameGroupView(Context context) {
        super(context);
    }

    public NameGroupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NameGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(Animator.AnimatorListener listener) {
        this.animatorListener = listener;
    }

    public void addCircleName(SpannableString name) {

        if (viewNum > 100) {
            return;
        }

        nameView = new NameView(getContext(), name, this);

        textLength = getDesiredWidth(nameView.getText(), nameView.getPaint());

        //圆心的坐标和圆的半径
        float circleY = getHeight() / 2 - 25;
        float circleX = getWidth() / 2 + 5;
        float circleR = (totalHeight - upBlock - downnBlock) / totalHeight * getHeight() / 2;
        circleR = circleR * (6 - time) / 4;

        float endY = (float) (Math.sin(Math.toRadians(circleDegree)) * circleR) - circleY;
        float endX = (float) (Math.cos(Math.toRadians(circleDegree)) * circleR) + circleX - textLength / 2;
        endY = Math.abs(endY);

        if (circleDegree % 360 == -90) {
            endY = endY + 20;
            endX = endX - 10;
        }else if(circleDegree % 360 == 90){
            endY = endY - 20;
            endX = endX + 5;
        }

        //每段字间隔15度
        circleDegree = circleDegree - circleDistance;
        if (circleDegree % 360 == -270) {
            time++;
            if (time == 2) {
                circleDistance = 15;
            } else if (time == 3) {
                circleDistance = 20;
            } else {
                circleDistance = 25;
            }
        }

        viewNum++;

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(nameView, params);

        //二、计算动画开始/结束点的坐标的准备工作
        //得到父布局的长宽
        float[] parentLocation = new float[2];
        parentLocation[0] = getWidth();
        parentLocation[1] = getHeight();

        //得到名字起始的坐标（用于计算动画开始的坐标）
        float startLoc[] = new float[2];
        startLoc[0] = getWidth() / 2;
        startLoc[1] = getHeight() / 2;

        //得到名字结束的坐标(用于计算动画结束后的坐标)
        float endLoc[] = new float[2];
        endLoc[0] = endX;
        endLoc[1] = endY;

        ValueAnimatorUtils.transferName(nameView, startLoc, endLoc, parentLocation, animatorListener);
    }

    public void addName(SpannableString name) {

        if (viewNum > 100) {
            return;
        }

        nameView = new NameView(getContext(), name, this);

        textLength = getDesiredWidth(nameView.getText(), nameView.getPaint());

//        //圆心的坐标和圆的半径
        float circleY = getHeight() / 2 - 25;
        float circleX = getWidth() / 2 + 5;
        float endY = 0;
        float endX = 0;

        endY = circleY - 80 + (yIndex - time) * 43;
        endX = circleX + (xIndex - 2) * 105;

        if (textLocation == 1) {
            if (xIndex == time + 2) {
                textLocation = 2;
                yIndex++;
            } else {
                xIndex++;
            }
        } else if (textLocation == 2) {
            if (yIndex == time * 2 + 2) {
                textLocation = 3;
                xIndex--;
            } else {
                yIndex++;
            }
        } else if (textLocation == 3) {
            if (xIndex == 1 - time) {
                textLocation = 4;
                yIndex--;
            } else {
                xIndex--;
            }
        } else {
            if (yIndex == 1) {
                textLocation = 1;
                time++;
            } else {
                yIndex--;
            }
        }

//        endY = circleY - 80 + (time - 1) * 43;
//        endX = circleX + (time-2)*105;

        lastY = endY;
        lastX = endX;
        viewNum++;

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(nameView, params);

        //二、计算动画开始/结束点的坐标的准备工作
        //得到父布局的长宽
        float[] parentLocation = new float[2];
        parentLocation[0] = getWidth();
        parentLocation[1] = getHeight();

        //得到名字起始的坐标（用于计算动画开始的坐标）
        float startLoc[] = new float[2];
        startLoc[0] = getWidth() / 2;
        startLoc[1] = getHeight() / 2;

        //得到名字结束的坐标(用于计算动画结束后的坐标)
        float endLoc[] = new float[2];
        endLoc[0] = endX;
        endLoc[1] = endY;

        ValueAnimatorUtils.transferName(nameView, startLoc, endLoc, parentLocation, animatorListener);
    }

    /*
     * dp转换成px
     */
    private int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setTextHeight(float textHeight) {
        this.textHeight = textHeight;
    }
}
