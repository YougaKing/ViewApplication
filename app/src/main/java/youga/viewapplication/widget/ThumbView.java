package youga.viewapplication.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;

import youga.viewapplication.R;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/07/13 11:36
 * description:
 */
public class ThumbView extends FrameLayout {

    private static final int MAX_SLICE = 7;
    private Interpolator[] mInterpolator;
    private LayoutParams mLayoutParams;

//    private ImageView mImageView;
    private int mImageWidth, mImageHeight;
    private int mLayoutWidth, mLayoutHeight;

    public ThumbView(Context context) {
        this(context, null);
    }

    public ThumbView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        Point image = measureImageSize(getContext());
        mImageWidth = image.x;
        mImageHeight = image.y;

        Point layout = measureLayoutSize(image);
        mLayoutWidth = layout.x;
        mLayoutHeight = layout.y;

        //底部 并且 水平居中
        mLayoutParams = new LayoutParams(mImageWidth, mImageHeight);
        mLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        // 初始化插补器
        mInterpolator = new Interpolator[]{
                new LinearInterpolator(),//线性
                new AccelerateInterpolator(),//加速
                new DecelerateInterpolator(),//减速
                new AccelerateDecelerateInterpolator(),//先加速后减速
        };
//        mImageView = new ImageView(getContext());
//        mImageView.setLayoutParams(mLayoutParams);
//        addView(mImageView);
//        setThumbResource(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(mLayoutWidth, mLayoutHeight);
    }

    public static Point measureImageSize(Context context) {
        Drawable drawable = context.getResources().getDrawable(R.drawable.thumb_light);
        return new Point(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static Point measureLayoutSize(Point image) {
        return new Point(image.x * 3, image.y * 4);
    }

    public void setThumbResource(boolean thumb) {
//        mImageView.setImageResource(thumb ? R.drawable.thumb_light : R.drawable.thumb_normal);
    }

    public void startThumbAnimator(boolean thumb) {
        if (thumb) {
            for (int i = 0; i < MAX_SLICE; i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.drawable.thumb_light);
                imageView.setLayoutParams(mLayoutParams);
                addView(imageView);
                Animator set = createAnimator(imageView, i);
                set.addListener(new AnimationEndListener(imageView));
                set.start();
            }
//            mImageView.setImageResource(R.drawable.thumb_light);
        } else {
            setThumbResource(false);
        }
    }

    private Animator createAnimator(View target, int index) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.2f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.2f, 1f);

        AnimatorSet animator = new AnimatorSet();
        animator.setInterpolator(new LinearInterpolator());
        animator.playTogether(alpha, scaleX, scaleY);
        animator.setTarget(target);

        ValueAnimator bezierValueAnimator = createBezierValueAnimator(target, index);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animator);
        animatorSet.playSequentially(animator, bezierValueAnimator);
        animatorSet.setInterpolator(mInterpolator[new Random().nextInt(mInterpolator.length)]);
        animatorSet.setTarget(target);
        return animatorSet;
    }

    private ValueAnimator createBezierValueAnimator(View target, int index) {
        //初始化一个贝塞尔计算器- - 传入
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(index), getPointF(index));
        int offsetX = mLayoutWidth / MAX_SLICE;

        //这里最好画个图 理解一下 传入了起点 和 终点
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF((mLayoutWidth - mImageWidth) / 2, mLayoutHeight - mImageHeight), new PointF(offsetX * index, 0));
        animator.addUpdateListener(new BezierListener(target));
        animator.setTarget(target);
        animator.setDuration(1500);
        return animator;
    }

    private PointF getPointF(int index) {
        PointF pointF = new PointF();
        int offsetX = mLayoutWidth / MAX_SLICE;
        pointF.x = offsetX * index;
        pointF.y = new Random().nextInt(mLayoutHeight - mImageHeight);
        return pointF;
    }

    private class BezierListener implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        private BezierListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            // 这里顺便做一个alpha动画
            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }


    private class AnimationEndListener extends AnimatorListenerAdapter {
        private View target;

        private AnimationEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
        }
    }

    private static class BezierEvaluator implements TypeEvaluator<PointF> {


        private PointF pointF1;
        private PointF pointF2;

        private BezierEvaluator(PointF pointF1, PointF pointF2) {
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }

        @Override
        public PointF evaluate(float time, PointF startValue,
                               PointF endValue) {

            float timeLeft = 1.0f - time;
            PointF point = new PointF();//结果

            point.x = timeLeft * timeLeft * timeLeft * (startValue.x)
                    + 3 * timeLeft * timeLeft * time * (pointF1.x)
                    + 3 * timeLeft * time * time * (pointF2.x)
                    + time * time * time * (endValue.x);

            point.y = timeLeft * timeLeft * timeLeft * (startValue.y)
                    + 3 * timeLeft * timeLeft * time * (pointF1.y)
                    + 3 * timeLeft * time * time * (pointF2.y)
                    + time * time * time * (endValue.y);
            return point;
        }
    }
}