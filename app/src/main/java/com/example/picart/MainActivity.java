package com.example.picart;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Main activity of the app. Contain a menu, a camera button and a grid view that show all the
 * albums in the gallery
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static final int REQUEST_PERMISSION_KEY = 1;
    CameraHandler cameraHandler;
    GridView galleryGridView;
    List<Map<String, String>> albumList = new ArrayList<Map<String, String>>();

    @VisibleForTesting
    File photo;

    /**
     * Used for testing whether photo generated
     * @param p The generated photo
     */
    @VisibleForTesting
    void pSetter(File p){
        photo = p;
    }
    /**
     * Set up main scene widgets
     *
     * @param savedInstanceState Not used
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraHandler = new CameraHandler(this);
        galleryGridView = findViewById(R.id.galleryGridView);

        FloatingActionButton cameraFab = findViewById(R.id.cameraFab);
        cameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraHandler.dispatchTakePictureIntent();
            }
        });

        // handle those devices that has low resolution
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA };
        if (!Function.hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_KEY);
        }
    }

    /**
     * Check whether the permission is guaranteed
     * @param requestCode Same as superclass
     * @param permissions Same as superclass
     * @param grantResults Same as superclass
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_KEY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadAlbum();
            } else {
                Toast.makeText(this, "You must accept permissions.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Recheck permissions, then load album
     */
    @Override
    protected void onResume() {
        super.onResume();

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Function.hasPermissions(this, permissions)) {
            loadAlbum();
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_KEY);
        }
    }

    /**
     * After the camera intent return, toast the image save path
     * @param requestCode Permission request code
     * @param resultCode Same as superclass
     * @param data Not used
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == cameraHandler.getRequestCode() && resultCode == RESULT_OK) {
            cameraHandler.showSavePath();
            cameraHandler.galleryAddPic();
            loadAlbum();
        } else {
            cameraHandler.deleteEmptyImage();
        }
    }


    /**
     * The album grid view adapter, display a picture, number of photos and title of the album
     */
    class AlbumAdapter extends BaseAdapter {
        final private Activity activity;
        final private List<Map<String, String>> data;
        private static final String TAG = "AlbumAdapter";

        /**
         * Constructor of AlbumAdapter
         *
         * @param activity The activity using the adapter
         * @param data     The data to be adapted
         */
        public AlbumAdapter(Activity activity, List<Map<String, String>> data) {
            this.activity = activity;
            this.data = data;
        }

        /**
         * Get the size of the data
         * @return Return the size of data list
         */
        public int getCount() {
            return data.size();
        }

        /**
         * Not used now
         * @param position The position of an item in the grid view
         * @return Not used now
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Get the slot id of a slot, currently the same as position
         * @param position The position of an item in the grid view
         * @return Return slot id, same as position
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Set the image, # of image, and title of each slot of grid view
         * @param position Position of the slot
         * @param convertView Cached previous loaded view
         * @param parent Same as superclass
         * @return If convertView is null, create new one and return, otherwise return the cached view
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            AlbumViewHolder holder;
            View view;

            if (convertView == null) {
                holder = new AlbumViewHolder();
                view = LayoutInflater.from(activity).inflate(
                        R.layout.album_row, parent, false);

                holder.galleryImage = (ImageView) view.findViewById(R.id.galleryImage);
                holder.galleryCount = (TextView) view.findViewById(R.id.gallery_count);
                holder.galleryTitle = (TextView) view.findViewById(R.id.gallery_title);

                view.setTag(holder);
            } else {
                view = convertView;
                holder = (AlbumViewHolder) view.getTag();
            }
            holder.galleryImage.setId(position);
            holder.galleryCount.setId(position);
            holder.galleryTitle.setId(position);

            Map<String, String> song;
            song = data.get(position);
            try {
                holder.galleryTitle.setText(song.get(Function.KEY_ALBUM));
                holder.galleryCount.setText(song.get(Function.KEY_COUNT));

                Glide.with(activity)
                        .load(new File(song.get(Function.KEY_PATH))) // Uri of the picture
                        .into(holder.galleryImage);


            } catch (Exception e) {
                Log.e(TAG, "getView: ", e);
            }
            return view;
        }
    }

    /**
     * View holder of the album, used to speed up rendering
     */
    class AlbumViewHolder {
        ImageView galleryImage;
        TextView galleryCount, galleryTitle;
    }


    /**
     * Load album to grid view
     */
    private void loadAlbum() {

        albumList.clear();

        String path = null;
        String album = null;
        String timestamp = null;
        String countPhoto = null;
        Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;


        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};
        Cursor cursorExternal = getContentResolver().query(uriExternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                null, null);
        Cursor cursorInternal = getContentResolver().query(uriInternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                null, null);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});

        while (cursor.moveToNext()) {

            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
            countPhoto = Function.getCount(getApplicationContext(), album);

            albumList.add(Function.mappingInbox(album, path, countPhoto,timestamp));
        }
        cursor.close();
       Collections.sort(albumList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending


        AlbumAdapter adapter = new AlbumAdapter(this, albumList);
        galleryGridView.setAdapter(adapter);
        galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                intent.putExtra("name", albumList.get(+position).get(Function.KEY_ALBUM));
                startActivity(intent);
            }
        });
    }

}
