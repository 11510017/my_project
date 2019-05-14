package me.kareluo.imaging.core.sticker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * Created by felix on 2017/12/11 下午2:48.
 */

public class IMGStickerX {

    private float mBaseScale = 1f, mScale = 1f;

    private float mBaseRotate = 0f, mRotate = 0f;

    private float mX = 0f, mY = 0f;

    protected float[] mPivotXY = {0, 0};

    private StickerEvent mTouchEvent;

    private boolean isActivated = true;

    /**
     * isActivated 为true时，其坐标相对于屏幕左上角
     * isActivated 为false时，其坐标相对Image，切为单位坐标
     */
    protected RectF mFrame = new RectF();

    private RectF mRemoveFrame = new RectF();

    private RectF mAdjustFrame = new RectF();

    private final static float SIZE_ANCHOR = 60;

    private final static float STROKE_FRAME = 6f;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    {
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(STROKE_FRAME);
        mPaint.setStyle(Paint.Style.STROKE);

        mFrame.set(0, 0, SIZE_ANCHOR * 2, SIZE_ANCHOR * 2);
        mRemoveFrame.set(0, 0, SIZE_ANCHOR, SIZE_ANCHOR);
        mAdjustFrame.set(0, 0, SIZE_ANCHOR, SIZE_ANCHOR);
    }

    /**
     * Is activated boolean.
     *
     * @return the boolean
     */
    public boolean isActivated() {
        return isActivated;
    }

    /**
     * Sets activated.
     *
     * @param activated the activated
     */
    public void setActivated(boolean activated) {
        isActivated = activated;

    }

    /**
     * On measure.
     *
     * @param width  the width
     * @param height the height
     */
    public void onMeasure(float width, float height) {
        mFrame.set(0, 0, width, height);
        mFrame.offset(mPivotXY[0] - mFrame.centerX(), mPivotXY[1] - mFrame.centerY());
    }

    /**
     * On draw.
     *
     * @param canvas the canvas
     */
    public void onDraw(Canvas canvas) {
        if (isActivated) {
            canvas.save();

            canvas.rotate(mRotate, mPivotXY[0], mPivotXY[1]);

            canvas.drawRect(mFrame, mPaint);

            canvas.translate(mFrame.left, mFrame.top);

            canvas.drawRect(mRemoveFrame, mPaint);

            canvas.translate(mFrame.width() - mAdjustFrame.width(), mFrame.height() - mAdjustFrame.height());

            canvas.drawRect(mAdjustFrame, mPaint);

            canvas.restore();
        }

        canvas.rotate(mRotate, mPivotXY[0], mPivotXY[1]);

//        canvas.scale(mBaseScale * mScale, mBaseScale * mScale, mPivotXY[0], mPivotXY[1]);
    }

    /**
     * Sets scale.
     *
     * @param scale the scale
     */
    public void setScale(float scale) {
        mScale = scale;
    }

    /**
     * Sets rotate.
     *
     * @param rotate the rotate
     */
    public void setRotate(float rotate) {
        mRotate = rotate;
    }

    /**
     * Sets base scale.
     *
     * @param baseScale the base scale
     */
    public void setBaseScale(float baseScale) {
        mBaseScale = baseScale;
    }

    /**
     * Sets base rotate.
     *
     * @param baseRotate the base rotate
     */
    public void setBaseRotate(float baseRotate) {
        mBaseRotate = baseRotate;
    }

    /**
     * Offset.
     *
     * @param dx the dx
     * @param dy the dy
     */
    public void offset(float dx, float dy) {
        mPivotXY[0] += dx;
        mPivotXY[1] += dy;
        mFrame.offset(mPivotXY[0] - mFrame.centerX(), mPivotXY[1] - mFrame.centerY());
    }

    /**
     * On touch sticker event.
     *
     * @param event the event
     * @return the sticker event
     */
    public StickerEvent onTouch(MotionEvent event) {
        int action = event.getActionMasked();

        if (mTouchEvent == null && action != MotionEvent.ACTION_DOWN) {
            return null;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                mTouchEvent = getTouchEvent(mX, mY);

                return mTouchEvent;
            case MotionEvent.ACTION_MOVE:
                if (mTouchEvent == StickerEvent.BODY) {
                    offset(event.getX() - mX, event.getY() - mY);
                    mX = event.getX();
                    mY = event.getY();
                }
            default:
                return mTouchEvent;
        }
    }

    private StickerEvent getTouchEvent(float x, float y) {
        float[] xy = {x, y};
        Matrix matrix = new Matrix();
        matrix.setRotate(mRotate, mFrame.centerX(), mFrame.centerY());
        matrix.mapPoints(xy);

        if (mFrame.contains(xy[0], xy[1])) {
            if (isInsideRemove(xy[0], xy[1])) {
                // 触摸到删除按钮
                return mTouchEvent = StickerEvent.REMOVE;
            } else if (isInsideAdjust(xy[0], xy[1])) {
                // 触摸到调整按钮
                return mTouchEvent = StickerEvent.ADJUST;
            }
            return StickerEvent.BODY;
        }
        return null;
    }

    /**
     * Sets touch event.
     *
     * @param touchEvent the touch event
     */
    public void setTouchEvent(StickerEvent touchEvent) {
        mTouchEvent = touchEvent;
    }

    /**
     * Is inside remove boolean.
     *
     * @param x the x
     * @param y the y
     * @return the boolean
     */
    public boolean isInsideRemove(float x, float y) {
        return mRemoveFrame.contains(x - mFrame.left, y - mFrame.top);
    }

    /**
     * Is inside adjust boolean.
     *
     * @param x the x
     * @param y the y
     * @return the boolean
     */
    public boolean isInsideAdjust(float x, float y) {
        return mAdjustFrame.contains(
                x - mFrame.right + mAdjustFrame.width(),
                y - mFrame.bottom + mAdjustFrame.height()
        );
    }

    /**
     * Is inside boolean.
     *
     * @param x the x
     * @param y the y
     * @return the boolean
     */
    public boolean isInside(float x, float y) {
        float[] xy = {x, y};
        Matrix matrix = new Matrix();
        matrix.setRotate(mRotate, mFrame.centerX(), mFrame.centerY());
        matrix.mapPoints(xy);

        return mFrame.contains(xy[0], xy[1]);
    }

    /**
     * Transform.
     *
     * @param matrix the matrix
     */
    public void transform(Matrix matrix) {
        matrix.mapPoints(mPivotXY);
        mFrame.offset(mPivotXY[0] - mFrame.centerX(), mPivotXY[1] - mFrame.centerY());
    }

    public enum StickerEvent {
        REMOVE,
        BODY,
        ADJUST
    }
}
