package me.kareluo.imaging.core.elastic;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by felix on 2017/11/27 下午6:27.
 */

public class IMGPointFEvaluator implements TypeEvaluator<PointF> {

    private PointF mPoint;

    /**
     * Instantiates a new Img point f evaluator.
     */
    public IMGPointFEvaluator() {

    }

    /**
     * Instantiates a new Img point f evaluator.
     *
     * @param reuse the reuse
     */
    public IMGPointFEvaluator(PointF reuse) {
        mPoint = reuse;
    }

    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        float x = startValue.x + (fraction * (endValue.x - startValue.x));
        float y = startValue.y + (fraction * (endValue.y - startValue.y));

        if (mPoint != null) {
            mPoint.set(x, y);
            return mPoint;
        } else {
            return new PointF(x, y);
        }
    }
}
