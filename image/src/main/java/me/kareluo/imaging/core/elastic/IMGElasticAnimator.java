package me.kareluo.imaging.core.elastic;

import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by felix on 2017/11/27 下午5:22.
 */

public class IMGElasticAnimator extends ValueAnimator {

    private IMGElastic mElastic;

    /**
     * Instantiates a new Img elastic animator.
     */
    public IMGElasticAnimator() {
        setEvaluator(new IMGPointFEvaluator());
        setInterpolator(new AccelerateDecelerateInterpolator());
    }

    /**
     * Instantiates a new Img elastic animator.
     *
     * @param elastic the elastic
     */
    public IMGElasticAnimator(IMGElastic elastic) {
        this();
        setElastic(elastic);
    }

    /**
     * Sets elastic.
     *
     * @param elastic the elastic
     */
    public void setElastic(IMGElastic elastic) {
        mElastic = elastic;

        if (mElastic == null) {
            throw new IllegalArgumentException("IMGElastic cannot be null.");
        }
    }

    /**
     * Start.
     *
     * @param x the x
     * @param y the y
     */
    public void start(float x, float y) {
        setObjectValues(new PointF(x, y), mElastic.getPivot());
        start();
    }
}
