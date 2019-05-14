package me.kareluo.imaging.core.elastic;

import android.graphics.PointF;

/**
 * Created by felix on 2017/11/27 下午6:43.
 */

public class IMGElastic {

    private float width, height;

    private PointF pivot = new PointF();

    /**
     * Instantiates a new Img elastic.
     */
    public IMGElastic() {

    }

    /**
     * Instantiates a new Img elastic.
     *
     * @param x the x
     * @param y the y
     */
    public IMGElastic(float x, float y) {
        pivot.set(x, y);
    }

    /**
     * Instantiates a new Img elastic.
     *
     * @param x      the x
     * @param y      the y
     * @param width  the width
     * @param height the height
     */
    public IMGElastic(float x, float y, float width, float height) {
        pivot.set(x, y);
        this.width = width;
        this.height = height;
    }

    /**
     * Gets width.
     *
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets width.
     *
     * @param width the width
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Gets height.
     *
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets height.
     *
     * @param height the height
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public float getX() {
        return pivot.x;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(float x) {
        pivot.x = x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public float getY() {
        return pivot.y;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(float y) {
        pivot.y = y;
    }

    /**
     * Sets xy.
     *
     * @param x the x
     * @param y the y
     */
    public void setXY(float x, float y) {
        pivot.set(x, y);
    }

    /**
     * Gets pivot.
     *
     * @return the pivot
     */
    public PointF getPivot() {
        return pivot;
    }

    /**
     * Sets size.
     *
     * @param width  the width
     * @param height the height
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Set.
     *
     * @param x      the x
     * @param y      the y
     * @param width  the width
     * @param height the height
     */
    public void set(float x, float y, float width, float height) {
        pivot.set(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "IMGElastic{" +
                "width=" + width +
                ", height=" + height +
                ", pivot=" + pivot +
                '}';
    }
}
