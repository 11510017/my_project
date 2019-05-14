package me.kareluo.imaging.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import me.kareluo.imaging.R;

/**
 * Created by felix on 2017/12/21 下午10:58.
 */

public class IMGStickerImageView extends IMGStickerView {

    private ImageView mImageView;

    /**
     * Instantiates a new Img sticker image view.
     *
     * @param context the context
     */
    public IMGStickerImageView(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Img sticker image view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public IMGStickerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new Img sticker image view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public IMGStickerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View onCreateContentView(Context context) {
        mImageView = new ImageView(context);
        mImageView.setImageResource(R.mipmap.ic_launcher);
        return mImageView;
    }
}
