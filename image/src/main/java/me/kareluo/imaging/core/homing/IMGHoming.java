package me.kareluo.imaging.core.homing;

/**
 * Created by felix on 2017/11/28 下午4:14.
 */

public class IMGHoming {

    public float x, y;

    public float scale;

    public float rotate;

    /**
     * Instantiates a new Img homing.
     *
     * @param x      the x
     * @param y      the y
     * @param scale  the scale
     * @param rotate the rotate
     */
    public IMGHoming(float x, float y, float scale, float rotate) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotate = rotate;
    }

    /**
     * Set.
     *
     * @param x      the x
     * @param y      the y
     * @param scale  the scale
     * @param rotate the rotate
     */
    public void set(float x, float y, float scale, float rotate) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotate = rotate;
    }

    /**
     * Concat.
     *
     * @param homing the homing
     */
    public void concat(IMGHoming homing) {
        this.scale *= homing.scale;
        this.x += homing.x;
        this.y += homing.y;
    }

    /**
     * R concat.
     *
     * @param homing the homing
     */
    public void rConcat(IMGHoming homing) {
        this.scale *= homing.scale;
        this.x -= homing.x;
        this.y -= homing.y;
    }

    /**
     * Is rotate boolean.
     *
     * @param sHoming the s homing
     * @param eHoming the e homing
     * @return the boolean
     */
    public static boolean isRotate(IMGHoming sHoming, IMGHoming eHoming) {
        return Float.compare(sHoming.rotate, eHoming.rotate) != 0;
    }

    @Override
    public String toString() {
        return "IMGHoming{" +
                "x=" + x +
                ", y=" + y +
                ", scale=" + scale +
                ", rotate=" + rotate +
                '}';
    }
}
