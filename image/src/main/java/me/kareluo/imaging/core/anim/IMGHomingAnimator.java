package me.kareluo.imaging.core.anim;

import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import me.kareluo.imaging.core.homing.IMGHoming;
import me.kareluo.imaging.core.homing.IMGHomingEvaluator;

/**
 * Created by felix on 2017/11/28 下午12:54.
 */

public class IMGHomingAnimator extends ValueAnimator {

    private boolean isRotate = false;

    private IMGHomingEvaluator mEvaluator;

    /**
     * Instantiates a new Img homing animator.
     */
    public IMGHomingAnimator() {
        setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    public void setObjectValues(Object... values) {
        super.setObjectValues(values);
        if (mEvaluator == null) {
            mEvaluator = new IMGHomingEvaluator();
        }
        setEvaluator(mEvaluator);
    }

    /**
     * Sets homing values.
     *
     * @param sHoming the s homing
     * @param eHoming the e homing
     */
    public void setHomingValues(IMGHoming sHoming, IMGHoming eHoming) {
        setObjectValues(sHoming, eHoming);
        isRotate = IMGHoming.isRotate(sHoming, eHoming);
    }

    /**
     * Is rotate boolean.
     *
     * @return the boolean
     */
    public boolean isRotate() {
        return isRotate;
    }
}
