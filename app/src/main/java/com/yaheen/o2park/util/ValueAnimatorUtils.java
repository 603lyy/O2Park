package com.yaheen.o2park.util;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class ValueAnimatorUtils {

    /**
     * 在这里初始化一次只能运行一个动画
     */
//    private static PathMeasure mPathMeasure;
//
//    private static ValueAnimator valueAnimator;

    /**
     * 曲线中间过程的点的坐标
     */
    private static float[] mCurrentPosition = new float[2];

    public static void transferName(final View nameView, int[] startLoc, final int[] endLoc, final int[] parentSize) {

//        三、正式开始计算动画开始/结束的坐标
        //开始掉落的商品的起始点
        float startX = startLoc[0];
        float startY = startLoc[1];

        //商品掉落后的终点坐标
        float toX = endLoc[0];
        float toY = endLoc[1];

        int duration = 1000;

        // 四、计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(toX, toY);
        //mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
        final PathMeasure mPathMeasure = new PathMeasure(path, false);

        //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration((long) (duration*(mPathMeasure.getLength()/200)));
        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当插值计算进行时，获取中间的每个值，
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // ★★★★★获取当前点坐标封装到mCurrentPosition
                // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
                // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                mPathMeasure.getPosTan(value, mCurrentPosition, null);//mCurrentPosition此时就是中间距离点的坐标值
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                if (mCurrentPosition[0] + nameView.getWidth() > parentSize[0]) {
                    mCurrentPosition[0] = parentSize[0] - nameView.getWidth() - 10;
                }
                if (mCurrentPosition[1] + nameView.getHeight() > parentSize[1]) {
                    mCurrentPosition[1] = parentSize[1] - nameView.getHeight() - 10;
                }
                nameView.setTranslationX(mCurrentPosition[0]);
                nameView.setTranslationY(mCurrentPosition[1]);
            }
        });
//      五、 开始执行动画
        valueAnimator.start();

//      六、动画结束后的处理
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            //当动画结束后：
            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
