package me.kareluo.imaging.core.clip;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import me.kareluo.imaging.core.util.IMGUtils;

/**
 * Created by felix on 2017/11/29 下午5:41.
 */
public class IMGClipWindow implements IMGClip {

    /**
     * 裁剪区域
     */
    private RectF mFrame = new RectF();

    private RectF mBaseFrame = new RectF();

    private RectF mTargetFrame = new RectF();

    /**
     * 裁剪窗口
     */
    private RectF mWinFrame = new RectF();

    private RectF mWin = new RectF();

    private float[] mCells = new float[16];

    private float[] mCorners = new float[32];

    private float[][] mBaseSizes = new float[2][4];

    /**
     * 是否在裁剪中
     */
    private boolean isClipping = false;

    private boolean isResetting = true;

    private boolean isShowShade = false;

    private boolean isHoming = false;

    private Matrix M = new Matrix();

    private Path mShadePath = new Path();

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 垂直窗口比例
     */
    private static final float VERTICAL_RATIO = 0.8f;

    private static final int COLOR_CELL = 0x80FFFFFF;

    private static final int COLOR_FRAME = Color.WHITE;

    private static final int COLOR_CORNER = Color.WHITE;

    private static final int COLOR_SHADE = 0xCC000000;

    {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
    }

    /**
     * Instantiates a new Img clip window.
     */
    public IMGClipWindow() {

    }

    /**
     * 计算裁剪窗口区域
     *
     * @param width  the width
     * @param height the height
     */
    public void setClipWinSize(float width, float height) {
        mWin.set(0, 0, width, height);
        mWinFrame.set(0, 0, width, height * VERTICAL_RATIO);

        if (!mFrame.isEmpty()) {
            IMGUtils.center(mWinFrame, mFrame);
            mTargetFrame.set(mFrame);
        }
    }

    /**
     * Reset.
     *
     * @param clipImage the clip image
     * @param rotate    the rotate
     */
    public void reset(RectF clipImage, float rotate) {
        RectF imgRect = new RectF();
        M.setRotate(rotate, clipImage.centerX(), clipImage.centerY());
        M.mapRect(imgRect, clipImage);
        reset(imgRect.width(), imgRect.height());
    }

    /**
     * 重置裁剪
     */
    private void reset(float clipWidth, float clipHeight) {
        setResetting(true);
        mFrame.set(0, 0, clipWidth, clipHeight);
        IMGUtils.fitCenter(mWinFrame, mFrame, CLIP_MARGIN);
        mTargetFrame.set(mFrame);
    }

    /**
     * Homing boolean.
     *
     * @return the boolean
     */
    public boolean homing() {
        mBaseFrame.set(mFrame);
        mTargetFrame.set(mFrame);
        IMGUtils.fitCenter(mWinFrame, mTargetFrame, CLIP_MARGIN);
        return isHoming = !mTargetFrame.equals(mBaseFrame);
    }

    /**
     * Homing.
     *
     * @param fraction the fraction
     */
    public void homing(float fraction) {
        if (isHoming) {
            mFrame.set(
                    mBaseFrame.left + (mTargetFrame.left - mBaseFrame.left) * fraction,
                    mBaseFrame.top + (mTargetFrame.top - mBaseFrame.top) * fraction,
                    mBaseFrame.right + (mTargetFrame.right - mBaseFrame.right) * fraction,
                    mBaseFrame.bottom + (mTargetFrame.bottom - mBaseFrame.bottom) * fraction
            );
        }
    }

    /**
     * Is homing boolean.
     *
     * @return the boolean
     */
    public boolean isHoming() {
        return isHoming;
    }

    /**
     * Sets homing.
     *
     * @param homing the homing
     */
    public void setHoming(boolean homing) {
        isHoming = homing;
    }

    /**
     * Is clipping boolean.
     *
     * @return the boolean
     */
    public boolean isClipping() {
        return isClipping;
    }

    /**
     * Sets clipping.
     *
     * @param clipping the clipping
     */
    public void setClipping(boolean clipping) {
        isClipping = clipping;
    }

    /**
     * Is resetting boolean.
     *
     * @return the boolean
     */
    public boolean isResetting() {
        return isResetting;
    }

    /**
     * Sets resetting.
     *
     * @param resetting the resetting
     */
    public void setResetting(boolean resetting) {
        isResetting = resetting;
    }

    /**
     * Gets frame.
     *
     * @return the frame
     */
    public RectF getFrame() {
        return mFrame;
    }

    /**
     * Gets win frame.
     *
     * @return the win frame
     */
    public RectF getWinFrame() {
        return mWinFrame;
    }

    /**
     * Gets offset frame.
     *
     * @param offsetX the offset x
     * @param offsetY the offset y
     * @return the offset frame
     */
    public RectF getOffsetFrame(float offsetX, float offsetY) {
        RectF frame = new RectF(mFrame);
        frame.offset(offsetX, offsetY);
        return frame;
    }

    /**
     * Gets target frame.
     *
     * @return the target frame
     */
    public RectF getTargetFrame() {
        return mTargetFrame;
    }

    /**
     * Gets offset target frame.
     *
     * @param offsetX the offset x
     * @param offsetY the offset y
     * @return the offset target frame
     */
    public RectF getOffsetTargetFrame(float offsetX, float offsetY) {
        RectF targetFrame = new RectF(mFrame);
        targetFrame.offset(offsetX, offsetY);
        return targetFrame;
    }

    /**
     * Is show shade boolean.
     *
     * @return the boolean
     */
    public boolean isShowShade() {
        return isShowShade;
    }

    /**
     * Sets show shade.
     *
     * @param showShade the show shade
     */
    public void setShowShade(boolean showShade) {
        isShowShade = showShade;
    }

    /**
     * On draw.
     *
     * @param canvas the canvas
     */
    public void onDraw(Canvas canvas) {

        if (isResetting) {
            return;
        }

        float[] size = {mFrame.width(), mFrame.height()};
        for (int i = 0; i < mBaseSizes.length; i++) {
            for (int j = 0; j < mBaseSizes[i].length; j++) {
                mBaseSizes[i][j] = size[i] * CLIP_SIZE_RATIO[j];
            }
        }

        for (int i = 0; i < mCells.length; i++) {
            mCells[i] = mBaseSizes[i & 1][CLIP_CELL_STRIDES >>> (i << 1) & 3];
        }

        for (int i = 0; i < mCorners.length; i++) {
            mCorners[i] = mBaseSizes[i & 1][CLIP_CORNER_STRIDES >>> i & 1]
                    + CLIP_CORNER_SIZES[CLIP_CORNERS[i] & 3] + CLIP_CORNER_STEPS[CLIP_CORNERS[i] >> 2];
        }

        canvas.translate(mFrame.left, mFrame.top);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(COLOR_CELL);
        mPaint.setStrokeWidth(CLIP_THICKNESS_CELL);
        canvas.drawLines(mCells, mPaint);

        canvas.translate(-mFrame.left, -mFrame.top);
        mPaint.setColor(COLOR_FRAME);
        mPaint.setStrokeWidth(CLIP_THICKNESS_FRAME);
        canvas.drawRect(mFrame, mPaint);

        canvas.translate(mFrame.left, mFrame.top);
        mPaint.setColor(COLOR_CORNER);
        mPaint.setStrokeWidth(CLIP_THICKNESS_SEWING);
        canvas.drawLines(mCorners, mPaint);
    }

    /**
     * On draw shade.
     *
     * @param canvas the canvas
     */
    public void onDrawShade(Canvas canvas) {
        if (!isShowShade) return;

        // 计算遮罩图形
        mShadePath.reset();

        mShadePath.setFillType(Path.FillType.WINDING);
        mShadePath.addRect(mFrame.left + 100, mFrame.top + 100, mFrame.right - 100, mFrame.bottom - 100, Path.Direction.CW);

        mPaint.setColor(COLOR_SHADE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mShadePath, mPaint);
    }

    /**
     * Gets anchor.
     *
     * @param x the x
     * @param y the y
     * @return the anchor
     */
    public Anchor getAnchor(float x, float y) {
        if (Anchor.isCohesionContains(mFrame, -CLIP_CORNER_SIZE, x, y)
                && !Anchor.isCohesionContains(mFrame, CLIP_CORNER_SIZE, x, y)) {
            int v = 0;
            float[] cohesion = Anchor.cohesion(mFrame, 0);
            float[] pos = {x, y};
            for (int i = 0; i < cohesion.length; i++) {
                if (Math.abs(cohesion[i] - pos[i >> 1]) < CLIP_CORNER_SIZE) {
                    v |= 1 << i;
                }
            }

            Anchor anchor = Anchor.valueOf(v);
            if (anchor != null) {
                isHoming = false;
            }
            return anchor;
        }
        return null;
    }

    /**
     * On scroll.
     *
     * @param anchor the anchor
     * @param dx     the dx
     * @param dy     the dy
     */
    public void onScroll(Anchor anchor, float dx, float dy) {
        anchor.move(mWinFrame, mFrame, dx, dy);
    }
}
