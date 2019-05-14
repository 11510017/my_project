package me.kareluo.imaging.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Log;

import me.kareluo.imaging.core.clip.IMGClip;
import me.kareluo.imaging.core.clip.IMGClipWindow;
import me.kareluo.imaging.core.homing.IMGHoming;
import me.kareluo.imaging.core.sticker.IMGSticker;
import me.kareluo.imaging.core.util.IMGUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix on 2017/11/21 下午10:03.
 */
public class IMGImage {

    private static final String TAG = "IMGImage";

    private Bitmap mImage, mMosaicImage;

    /**
     * 完整图片边框
     */
    private RectF mFrame = new RectF();

    /**
     * 裁剪图片边框（显示的图片区域）
     */
    private RectF mClipFrame = new RectF();

    private RectF mTempClipFrame = new RectF();

    /**
     * 裁剪模式前状态备份
     */
    private RectF mBackupClipFrame = new RectF();

    private float mBackupClipRotate = 0;

    private float mRotate = 0, mTargetRotate = 0;

    private boolean isRequestToBaseFitting = false;

    private boolean isAnimCanceled = false;

    /**
     * 裁剪模式时当前触摸锚点
     */
    private IMGClip.Anchor mAnchor;

    private boolean isSteady = true;

    private Path mShade = new Path();

    /**
     * 裁剪窗口
     */
    private IMGClipWindow mClipWin = new IMGClipWindow();

    private boolean isDrawClip = false;

    /**
     * 编辑模式
     */
    private IMGMode mMode = IMGMode.NONE;

    private boolean isFreezing = mMode == IMGMode.CLIP;

    /**
     * 可视区域，无Scroll 偏移区域
     */
    private RectF mWindow = new RectF();

    /**
     * 是否初始位置
     */
    private boolean isInitialHoming = false;

    /**
     * 当前选中贴片
     */
    private IMGSticker mForeSticker;

    /**
     * 为被选中贴片
     */
    private List<IMGSticker> mBackStickers = new ArrayList<>();

    /**
     * 涂鸦路径
     */
    private List<IMGPath> mDoodles = new ArrayList<>();

    /**
     * 马赛克路径
     */
    private List<IMGPath> mMosaics = new ArrayList<>();

    private static final int MIN_SIZE = 500;

    private static final int MAX_SIZE = 10000;

    private Paint mPaint, mMosaicPaint, mShadePaint;

    private Matrix M = new Matrix();

    private static final boolean DEBUG = false;

    private static final Bitmap DEFAULT_IMAGE;

    private static final int COLOR_SHADE = 0xCC000000;

    static {
        DEFAULT_IMAGE = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    }

    {
        mShade.setFillType(Path.FillType.WINDING);

        // Doodle&Mosaic 's paint
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(IMGPath.BASE_DOODLE_WIDTH);
        mPaint.setColor(Color.RED);
        mPaint.setPathEffect(new CornerPathEffect(IMGPath.BASE_DOODLE_WIDTH));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    /**
     * Instantiates a new Img image.
     */
    public IMGImage() {
        mImage = DEFAULT_IMAGE;

        if (mMode == IMGMode.CLIP) {
            initShadePaint();
        }
    }

    /**
     * Sets bitmap.
     *
     * @param bitmap the bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }

        this.mImage = bitmap;

        // 清空马赛克图层
        if (mMosaicImage != null) {
            mMosaicImage.recycle();
        }
        this.mMosaicImage = null;

        makeMosaicBitmap();

        onImageChanged();
    }

    /**
     * Gets mode.
     *
     * @return the mode
     */
    public IMGMode getMode() {
        return mMode;
    }

    /**
     * Sets mode.
     *
     * @param mode the mode
     */
    public void setMode(IMGMode mode) {

        if (this.mMode == mode) return;

        moveToBackground(mForeSticker);

        if (mode == IMGMode.CLIP) {
            setFreezing(true);
        }

        this.mMode = mode;

        if (mMode == IMGMode.CLIP) {

            // 初始化Shade 画刷
            initShadePaint();

            // 备份裁剪前Clip 区域
            mBackupClipRotate = getRotate();
            mBackupClipFrame.set(mClipFrame);

            float scale = 1 / getScale();
            M.setTranslate(-mFrame.left, -mFrame.top);
            M.postScale(scale, scale);
            M.mapRect(mBackupClipFrame);

            // 重置裁剪区域
            mClipWin.reset(mClipFrame, getTargetRotate());
        } else {

            if (mMode == IMGMode.MOSAIC) {
                makeMosaicBitmap();
            }

            mClipWin.setClipping(false);
        }
    }

    // TODO
    private void rotateStickers(float rotate) {
        M.setRotate(rotate, mClipFrame.centerX(), mClipFrame.centerY());
        for (IMGSticker sticker : mBackStickers) {
            M.mapRect(sticker.getFrame());
            sticker.setRotation(sticker.getRotation() + rotate);
            sticker.setX(sticker.getFrame().centerX() - sticker.getPivotX());
            sticker.setY(sticker.getFrame().centerY() - sticker.getPivotY());
        }
    }

    private void initShadePaint() {
        if (mShadePaint == null) {
            mShadePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mShadePaint.setColor(COLOR_SHADE);
            mShadePaint.setStyle(Paint.Style.FILL);
        }
    }

    /**
     * Is mosaic empty boolean.
     *
     * @return the boolean
     */
    public boolean isMosaicEmpty() {
        return mMosaics.isEmpty();
    }

    /**
     * Is doodle empty boolean.
     *
     * @return the boolean
     */
    public boolean isDoodleEmpty() {
        return mDoodles.isEmpty();
    }

    /**
     * Undo doodle.
     */
    public void undoDoodle() {
        if (!mDoodles.isEmpty()) {
            mDoodles.remove(mDoodles.size() - 1);
        }
    }

    /**
     * Undo mosaic.
     */
    public void undoMosaic() {
        if (!mMosaics.isEmpty()) {
            mMosaics.remove(mMosaics.size() - 1);
        }
    }

    /**
     * Gets clip frame.
     *
     * @return the clip frame
     */
    public RectF getClipFrame() {
        return mClipFrame;
    }

    /**
     * 裁剪区域旋转回原始角度后形成新的裁剪区域，旋转中心发生变化，
     * 因此需要将视图窗口平移到新的旋转中心位置。
     *
     * @param scrollX the scroll x
     * @param scrollY the scroll y
     * @return the img homing
     */
    public IMGHoming clip(float scrollX, float scrollY) {
        RectF frame = mClipWin.getOffsetFrame(scrollX, scrollY);

        M.setRotate(-getRotate(), mClipFrame.centerX(), mClipFrame.centerY());
        M.mapRect(mClipFrame, frame);

        return new IMGHoming(
                scrollX + (mClipFrame.centerX() - frame.centerX()),
                scrollY + (mClipFrame.centerY() - frame.centerY()),
                getScale(), getRotate()
        );
    }

    /**
     * To backup clip.
     */
    public void toBackupClip() {
        M.setScale(getScale(), getScale());
        M.postTranslate(mFrame.left, mFrame.top);
        M.mapRect(mClipFrame, mBackupClipFrame);
        setTargetRotate(mBackupClipRotate);
        isRequestToBaseFitting = true;
    }

    /**
     * Reset clip.
     */
    public void resetClip() {
        // TODO 就近旋转
        setTargetRotate(getRotate() - getRotate() % 360);
        mClipFrame.set(mFrame);
        mClipWin.reset(mClipFrame, getTargetRotate());
    }

    private void makeMosaicBitmap() {
        if (mMosaicImage != null || mImage == null) {
            return;
        }

        if (mMode == IMGMode.MOSAIC) {

            int w = Math.round(mImage.getWidth() / 64f);
            int h = Math.round(mImage.getHeight() / 64f);

            w = Math.max(w, 8);
            h = Math.max(h, 8);

            // 马赛克画刷
            if (mMosaicPaint == null) {
                mMosaicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mMosaicPaint.setFilterBitmap(false);
                mMosaicPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            }

            mMosaicImage = Bitmap.createScaledBitmap(mImage, w, h, false);
        }
    }

    private void onImageChanged() {
        isInitialHoming = false;
        onWindowChanged(mWindow.width(), mWindow.height());

        if (mMode == IMGMode.CLIP) {
            mClipWin.reset(mClipFrame, getTargetRotate());
        }
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
     * On clip homing boolean.
     *
     * @return the boolean
     */
    public boolean onClipHoming() {
        return mClipWin.homing();
    }

    /**
     * Gets start homing.
     *
     * @param scrollX the scroll x
     * @param scrollY the scroll y
     * @return the start homing
     */
    public IMGHoming getStartHoming(float scrollX, float scrollY) {
        return new IMGHoming(scrollX, scrollY, getScale(), getRotate());
    }

    /**
     * Gets end homing.
     *
     * @param scrollX the scroll x
     * @param scrollY the scroll y
     * @return the end homing
     */
    public IMGHoming getEndHoming(float scrollX, float scrollY) {
        IMGHoming homing = new IMGHoming(scrollX, scrollY, getScale(), getTargetRotate());

        if (mMode == IMGMode.CLIP) {
            RectF frame = new RectF(mClipWin.getTargetFrame());
            frame.offset(scrollX, scrollY);
            if (mClipWin.isResetting()) {

                RectF clipFrame = new RectF();
                M.setRotate(getTargetRotate(), mClipFrame.centerX(), mClipFrame.centerY());
                M.mapRect(clipFrame, mClipFrame);

                homing.rConcat(IMGUtils.fill(frame, clipFrame));
            } else {
                RectF cFrame = new RectF();

                // cFrame要是一个暂时clipFrame
                if (mClipWin.isHoming()) {
//
//                    M.mapRect(cFrame, mClipFrame);

//                    mClipWin
                    // TODO 偏移中心

                    M.setRotate(getTargetRotate() - getRotate(), mClipFrame.centerX(), mClipFrame.centerY());
                    M.mapRect(cFrame, mClipWin.getOffsetFrame(scrollX, scrollY));

                    homing.rConcat(IMGUtils.fitHoming(frame, cFrame, mClipFrame.centerX(), mClipFrame.centerY()));


                } else {
                    M.setRotate(getTargetRotate(), mClipFrame.centerX(), mClipFrame.centerY());
                    M.mapRect(cFrame, mFrame);
                    homing.rConcat(IMGUtils.fillHoming(frame, cFrame, mClipFrame.centerX(), mClipFrame.centerY()));
                }

            }
        } else {
            RectF clipFrame = new RectF();
            M.setRotate(getTargetRotate(), mClipFrame.centerX(), mClipFrame.centerY());
            M.mapRect(clipFrame, mClipFrame);

            RectF win = new RectF(mWindow);
            win.offset(scrollX, scrollY);
            homing.rConcat(IMGUtils.fitHoming(win, clipFrame, isRequestToBaseFitting));
            isRequestToBaseFitting = false;
        }

        return homing;
    }

    /**
     * Add sticker.
     *
     * @param <S>     the type parameter
     * @param sticker the sticker
     */
    public <S extends IMGSticker> void addSticker(S sticker) {
        if (sticker != null) {
            moveToForeground(sticker);
        }
    }

    /**
     * Add path.
     *
     * @param path the path
     * @param sx   the sx
     * @param sy   the sy
     */
    public void addPath(IMGPath path, float sx, float sy) {
        if (path == null) return;

        float scale = 1f / getScale();

        M.setTranslate(sx, sy);
        M.postRotate(-getRotate(), mClipFrame.centerX(), mClipFrame.centerY());
        M.postTranslate(-mFrame.left, -mFrame.top);
        M.postScale(scale, scale);
        path.transform(M);

        switch (path.getMode()) {
            case DOODLE:
                mDoodles.add(path);
                break;
            case MOSAIC:
                path.setWidth(path.getWidth() * scale);
                mMosaics.add(path);
                break;
        }
    }

    private void moveToForeground(IMGSticker sticker) {
        if (sticker == null) return;

        moveToBackground(mForeSticker);

        if (sticker.isShowing()) {
            mForeSticker = sticker;
            // 从BackStickers中移除
            mBackStickers.remove(sticker);
        } else sticker.show();
    }

    private void moveToBackground(IMGSticker sticker) {
        if (sticker == null) return;

        if (!sticker.isShowing()) {
            // 加入BackStickers中
            if (!mBackStickers.contains(sticker)) {
                mBackStickers.add(sticker);
            }

            if (mForeSticker == sticker) {
                mForeSticker = null;
            }
        } else sticker.dismiss();
    }

    /**
     * Stick all.
     */
    public void stickAll() {
        moveToBackground(mForeSticker);
    }

    /**
     * On dismiss.
     *
     * @param sticker the sticker
     */
    public void onDismiss(IMGSticker sticker) {
        moveToBackground(sticker);
    }

    /**
     * On showing.
     *
     * @param sticker the sticker
     */
    public void onShowing(IMGSticker sticker) {
        if (mForeSticker != sticker) {
            moveToForeground(sticker);
        }
    }

    /**
     * On remove sticker.
     *
     * @param sticker the sticker
     */
    public void onRemoveSticker(IMGSticker sticker) {
        if (mForeSticker == sticker) {
            mForeSticker = null;
        } else {
            mBackStickers.remove(sticker);
        }
    }

    /**
     * On window changed.
     *
     * @param width  the width
     * @param height the height
     */
    public void onWindowChanged(float width, float height) {
        if (width == 0 || height == 0) {
            return;
        }

        mWindow.set(0, 0, width, height);

        if (!isInitialHoming) {
            onInitialHoming(width, height);
        } else {

            // Pivot to fit window.
            M.setTranslate(mWindow.centerX() - mClipFrame.centerX(), mWindow.centerY() - mClipFrame.centerY());
            M.mapRect(mFrame);
            M.mapRect(mClipFrame);
        }

        mClipWin.setClipWinSize(width, height);
    }

    private void onInitialHoming(float width, float height) {
        mFrame.set(0, 0, mImage.getWidth(), mImage.getHeight());
        mClipFrame.set(mFrame);
        mClipWin.setClipWinSize(width, height);

        if (mClipFrame.isEmpty()) {
            return;
        }

        toBaseHoming();

        isInitialHoming = true;
        onInitialHomingDone();
    }

    private void toBaseHoming() {
        if (mClipFrame.isEmpty()) {
            // Bitmap invalidate.
            return;
        }

        float scale = Math.min(
                mWindow.width() / mClipFrame.width(),
                mWindow.height() / mClipFrame.height()
        );

        // Scale to fit window.
        M.setScale(scale, scale, mClipFrame.centerX(), mClipFrame.centerY());
        M.postTranslate(mWindow.centerX() - mClipFrame.centerX(), mWindow.centerY() - mClipFrame.centerY());
        M.mapRect(mFrame);
        M.mapRect(mClipFrame);
    }

    private void onInitialHomingDone() {
        if (mMode == IMGMode.CLIP) {
            mClipWin.reset(mClipFrame, getTargetRotate());
        }
    }

    /**
     * On draw image.
     *
     * @param canvas the canvas
     */
    public void onDrawImage(Canvas canvas) {

        // 裁剪区域
        canvas.clipRect(mClipWin.isClipping() ? mFrame : mClipFrame);

        // 绘制图片
        canvas.drawBitmap(mImage, null, mFrame, null);

        if (DEBUG) {
            // Clip 区域
            mPaint.setColor(Color.RED);
            mPaint.setStrokeWidth(6);
            canvas.drawRect(mFrame, mPaint);
            canvas.drawRect(mClipFrame, mPaint);
        }
    }

    /**
     * On draw mosaics path int.
     *
     * @param canvas the canvas
     * @return the int
     */
    public int onDrawMosaicsPath(Canvas canvas) {
        int layerCount = canvas.saveLayer(mFrame, null, Canvas.ALL_SAVE_FLAG);

        if (!isMosaicEmpty()) {
            canvas.save();
            float scale = getScale();
            canvas.translate(mFrame.left, mFrame.top);
            canvas.scale(scale, scale);
            for (IMGPath path : mMosaics) {
                path.onDrawMosaic(canvas, mPaint);
            }
            canvas.restore();
        }

        return layerCount;
    }

    /**
     * On draw mosaic.
     *
     * @param canvas     the canvas
     * @param layerCount the layer count
     */
    public void onDrawMosaic(Canvas canvas, int layerCount) {
        canvas.drawBitmap(mMosaicImage, null, mFrame, mMosaicPaint);
        canvas.restoreToCount(layerCount);
    }

    /**
     * On draw doodles.
     *
     * @param canvas the canvas
     */
    public void onDrawDoodles(Canvas canvas) {
        if (!isDoodleEmpty()) {
            canvas.save();
            float scale = getScale();
            canvas.translate(mFrame.left, mFrame.top);
            canvas.scale(scale, scale);
            for (IMGPath path : mDoodles) {
                path.onDrawDoodle(canvas, mPaint);
            }
            canvas.restore();
        }
    }

    /**
     * On draw sticker clip.
     *
     * @param canvas the canvas
     */
    public void onDrawStickerClip(Canvas canvas) {
        M.setRotate(getRotate(), mClipFrame.centerX(), mClipFrame.centerY());
        M.mapRect(mTempClipFrame, mClipWin.isClipping() ? mFrame : mClipFrame);
        canvas.clipRect(mTempClipFrame);
    }

    /**
     * On draw stickers.
     *
     * @param canvas the canvas
     */
    public void onDrawStickers(Canvas canvas) {
        if (mBackStickers.isEmpty()) return;
        canvas.save();
        for (IMGSticker sticker : mBackStickers) {
            if (!sticker.isShowing()) {
                float tPivotX = sticker.getX() + sticker.getPivotX();
                float tPivotY = sticker.getY() + sticker.getPivotY();

                canvas.save();
                M.setTranslate(sticker.getX(), sticker.getY());
                M.postScale(sticker.getScale(), sticker.getScale(), tPivotX, tPivotY);
                M.postRotate(sticker.getRotation(), tPivotX, tPivotY);

                canvas.concat(M);
                sticker.onSticker(canvas);
                canvas.restore();
            }
        }
        canvas.restore();
    }

    /**
     * On draw shade.
     *
     * @param canvas the canvas
     */
    public void onDrawShade(Canvas canvas) {
        if (mMode == IMGMode.CLIP && isSteady) {
            mShade.reset();
            mShade.addRect(mFrame.left - 2, mFrame.top - 2, mFrame.right + 2, mFrame.bottom + 2, Path.Direction.CW);
            mShade.addRect(mClipFrame, Path.Direction.CCW);
            canvas.drawPath(mShade, mShadePaint);
        }
    }

    /**
     * On draw clip.
     *
     * @param canvas  the canvas
     * @param scrollX the scroll x
     * @param scrollY the scroll y
     */
    public void onDrawClip(Canvas canvas, float scrollX, float scrollY) {
        if (mMode == IMGMode.CLIP) {
            mClipWin.onDraw(canvas);
        }
    }

    /**
     * On touch down.
     *
     * @param x the x
     * @param y the y
     */
    public void onTouchDown(float x, float y) {
        isSteady = false;
        moveToBackground(mForeSticker);
        if (mMode == IMGMode.CLIP) {
            mAnchor = mClipWin.getAnchor(x, y);
        }
    }

    /**
     * On touch up.
     *
     * @param scrollX the scroll x
     * @param scrollY the scroll y
     */
    public void onTouchUp(float scrollX, float scrollY) {
        if (mAnchor != null) {
            mAnchor = null;
        }
    }

    /**
     * On steady.
     *
     * @param scrollX the scroll x
     * @param scrollY the scroll y
     */
    public void onSteady(float scrollX, float scrollY) {
        isSteady = true;
        onClipHoming();
        mClipWin.setShowShade(true);
    }

    /**
     * On scale begin.
     */
    public void onScaleBegin() {

    }

    /**
     * On scroll img homing.
     *
     * @param scrollX the scroll x
     * @param scrollY the scroll y
     * @param dx      the dx
     * @param dy      the dy
     * @return the img homing
     */
    public IMGHoming onScroll(float scrollX, float scrollY, float dx, float dy) {
        if (mMode == IMGMode.CLIP) {
            mClipWin.setShowShade(false);
            if (mAnchor != null) {
                mClipWin.onScroll(mAnchor, dx, dy);

                RectF clipFrame = new RectF();
                M.setRotate(getRotate(), mClipFrame.centerX(), mClipFrame.centerY());
                M.mapRect(clipFrame, mFrame);

                RectF frame = mClipWin.getOffsetFrame(scrollX, scrollY);
                IMGHoming homing = new IMGHoming(scrollX, scrollY, getScale(), getTargetRotate());
                homing.rConcat(IMGUtils.fillHoming(frame, clipFrame, mClipFrame.centerX(), mClipFrame.centerY()));
                return homing;
            }
        }
        return null;
    }

    /**
     * Gets target rotate.
     *
     * @return the target rotate
     */
    public float getTargetRotate() {
        return mTargetRotate;
    }

    /**
     * Sets target rotate.
     *
     * @param targetRotate the target rotate
     */
    public void setTargetRotate(float targetRotate) {
        this.mTargetRotate = targetRotate;
    }

    /**
     * 在当前基础上旋转
     *
     * @param rotate the rotate
     */
    public void rotate(int rotate) {
        mTargetRotate = Math.round((mRotate + rotate) / 90f) * 90;
        mClipWin.reset(mClipFrame, getTargetRotate());
    }

    /**
     * Gets rotate.
     *
     * @return the rotate
     */
    public float getRotate() {
        return mRotate;
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
     * Gets scale.
     *
     * @return the scale
     */
    public float getScale() {
        return 1f * mFrame.width() / mImage.getWidth();
    }

    /**
     * Sets scale.
     *
     * @param scale the scale
     */
    public void setScale(float scale) {
        setScale(scale, mClipFrame.centerX(), mClipFrame.centerY());
    }

    /**
     * Sets scale.
     *
     * @param scale  the scale
     * @param focusX the focus x
     * @param focusY the focus y
     */
    public void setScale(float scale, float focusX, float focusY) {
        onScale(scale / getScale(), focusX, focusY);
    }

    /**
     * On scale.
     *
     * @param factor the factor
     * @param focusX the focus x
     * @param focusY the focus y
     */
    public void onScale(float factor, float focusX, float focusY) {

        if (factor == 1f) return;

        if (Math.max(mClipFrame.width(), mClipFrame.height()) >= MAX_SIZE
                || Math.min(mClipFrame.width(), mClipFrame.height()) <= MIN_SIZE) {
            factor += (1 - factor) / 2;
        }

        M.setScale(factor, factor, focusX, focusY);
        M.mapRect(mFrame);
        M.mapRect(mClipFrame);

        // 修正clip 窗口
        if (!mFrame.contains(mClipFrame)) {
            // TODO
//            mClipFrame.intersect(mFrame);
        }

        for (IMGSticker sticker : mBackStickers) {
            M.mapRect(sticker.getFrame());
            float tPivotX = sticker.getX() + sticker.getPivotX();
            float tPivotY = sticker.getY() + sticker.getPivotY();
            sticker.addScale(factor);
            sticker.setX(sticker.getX() + sticker.getFrame().centerX() - tPivotX);
            sticker.setY(sticker.getY() + sticker.getFrame().centerY() - tPivotY);
        }
    }

    /**
     * On scale end.
     */
    public void onScaleEnd() {

    }

    /**
     * On homing start.
     *
     * @param isRotate the is rotate
     */
    public void onHomingStart(boolean isRotate) {
        isAnimCanceled = false;
        isDrawClip = true;
    }

    /**
     * On homing.
     *
     * @param fraction the fraction
     */
    public void onHoming(float fraction) {
        mClipWin.homing(fraction);
    }

    /**
     * On homing end boolean.
     *
     * @param scrollX  the scroll x
     * @param scrollY  the scroll y
     * @param isRotate the is rotate
     * @return the boolean
     */
    public boolean onHomingEnd(float scrollX, float scrollY, boolean isRotate) {
        isDrawClip = true;
        if (mMode == IMGMode.CLIP) {
            // 开启裁剪模式

            boolean clip = !isAnimCanceled;

            mClipWin.setHoming(false);
            mClipWin.setClipping(true);
            mClipWin.setResetting(false);

            return clip;
        } else {
            if (isFreezing && !isAnimCanceled) {
                setFreezing(false);
            }
        }
        return false;
    }

    /**
     * Is freezing boolean.
     *
     * @return the boolean
     */
    public boolean isFreezing() {
        return isFreezing;
    }

    private void setFreezing(boolean freezing) {
        if (freezing != isFreezing) {
            rotateStickers(freezing ? -getRotate() : getTargetRotate());
            isFreezing = freezing;
        }
    }

    /**
     * On homing cancel.
     *
     * @param isRotate the is rotate
     */
    public void onHomingCancel(boolean isRotate) {
        isAnimCanceled = true;
        Log.d(TAG, "Homing cancel");
    }

    /**
     * Release.
     */
    public void release() {
        if (mImage != null && !mImage.isRecycled()) {
            mImage.recycle();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (DEFAULT_IMAGE != null) {
            DEFAULT_IMAGE.recycle();
        }
    }
}
