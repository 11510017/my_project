package com.example.picart;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import android.util.DisplayMetrics;


import java.util.HashMap;
import java.util.Map;

/**
 * Provide some useful static methods
 */
final class Function {

    static final String KEY_ALBUM = "albumName";
    static final String KEY_PATH = "path";
    static final String KEY_TIMESTAMP = "timestamp";
    static final String KEY_TIME = "date";
    static final String KEY_COUNT = "count";

    private Function() {
    }

    /**
     * Check whether the app has permissions
     *
     * @param context     The activity the app is running
     * @param permissions Permissions that the app requires
     * @return True if the app has all the permission it requires, otherwise False
     */
    static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Create a hash map by the given parameters. The map will be treated as an element in data list
     * @param album Album name
     * @param path Path of an album or image

     * @param count Number of images in an album
     * @return Return a hash map containing keys and values above
     */
    static Map<String, String> mappingInbox(String album, String path, String count,String timestamp) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(KEY_ALBUM, album);
        map.put(KEY_PATH, path);
        map.put(KEY_TIMESTAMP, timestamp);
        //map.put(KEY_TIME, time);
        map.put(KEY_COUNT, count);
        return map;
    }

    /**
     * Get the number of images in an album folder
     *
     * @param context    Activity that calls the method
     * @param album_name Name of the album
     * @return Return a string with count appended with "Photos"
     */
    static String getCount(Context context, String album_name) {
        Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};
        Cursor cursorExternal = context.getContentResolver().query(uriExternal, projection, "bucket_display_name = \"" + album_name + "\"", null, null);
        Cursor cursorInternal = context.getContentResolver().query(uriInternal, projection, "bucket_display_name = \"" + album_name + "\"", null, null);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});


        return cursor.getCount() + " Photos";
    }

    /**
     * Convert timestamp to time
     *
     * @param timestamp Timestamp to be converted
     * @return Return a string with pattern "dd/MM HH:mm"
     */
  /*  static String convertToTime(String timestamp) {
        long datetime = Long.parseLong(timestamp);
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd/MM HH:mm", Locale.US);
        return formatter.format(date);
    }
    */

    /**
     * Convert the length unit from dp to px
     * @param dp The dp unit to be converted
     * @param context The activity that calls the method
     * @return Return converted px unit
     */
    static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

}
