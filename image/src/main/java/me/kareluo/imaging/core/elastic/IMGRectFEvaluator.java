package me.kareluo.imaging.core.elastic;

import android.animation.TypeEvaluator;
import android.graphics.RectF;

/**
 * Created by felix on 2017/11/27 下午6:11.
 */

public class IMGRectFEvaluator implements TypeEvaluator<RectF> {

    private RectF mRect;

    /**
     * Instantiates a new Img rect f evaluator.
     */
    public IMGRectFEvaluator() {

    }

    /**
     * Instantiates a new Img rect f evaluator.
     *
     * @param reuseRect the reuse rect
     */
    public IMGRectFEvaluator(RectF reuseRect) {
        mRect = reuseRect;
    }

    @Override
    public RectF evaluate(float fraction, RectF startValue, RectF endValue) {
        float left = startValue.left + (endValue.left - startValue.left) * fraction;
        float top = startValue.top + (endValue.top - startValue.top) * fraction;
        float right = startValue.right + (endValue.right - startValue.right) * fraction;
        float bottom = startValue.bottom + (endValue.bottom - startValue.bottom) * fraction;
        if (mRect == null) {
            return new RectF(left, top, right, bottom);
        } else {
            mRect.set(left, top, right, bottom);
            return mRect;
        }
    }
}
