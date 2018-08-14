package youga.viewapplication.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/07/17 15:22
 * description:
 */
public class ThumbAnimatorUtil {


    public static void startAnimator(View target) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(target, View.TRANSLATION_X, 0,200, 0);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, 0,200, 0);

        AnimatorSet animator = new AnimatorSet();
        animator.setInterpolator(new LinearInterpolator());
        animator.playTogether(translationX, translationY);
        animator.setTarget(target);

        animator.setDuration(3000);
        animator.start();
    }

}
