package me.kareluo.imaging.core.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

/**
 * Created by felix on 2017/12/26 下午2:54.
 */
public abstract class IMGDecoder {

    private Uri uri;

    /**
     * Instantiates a new Img decoder.
     *
     * @param uri the uri
     */
    public IMGDecoder(Uri uri) {
        this.uri = uri;
    }

    /**
     * Gets uri.
     *
     * @return the uri
     */
    public Uri getUri() {
        return uri;
    }

    /**
     * Sets uri.
     *
     * @param uri the uri
     */
    public void setUri(Uri uri) {
        this.uri = uri;
    }

    /**
     * Decode bitmap.
     *
     * @return the bitmap
     */
    public Bitmap decode() {
        return decode(null);
    }

    /**
     * Decode bitmap.
     *
     * @param options the options
     * @return the bitmap
     */
    public abstract Bitmap decode(BitmapFactory.Options options);

}
