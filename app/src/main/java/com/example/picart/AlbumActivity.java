package com.example.picart;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Album activity. Contain a grid view showing all images in an album
 */
public class AlbumActivity extends AppCompatActivity {
    static final private int GET_CODE = 2;
    GridView galleryGridView;
    List<Map<String, String>> imageList = new ArrayList<Map<String, String>>();
    String albumName = "";
    LoadAlbumImages loadAlbumTask;

    /**
     * Set activity title to the album name, load album images
     *
     * @param savedInstanceState Not used
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * The album grid view adapter, display images
     */
    class SingleAlbumAdapter extends BaseAdapter {
        final private Activity activity;
        final private List<Map<String, String>> data;
        private static final String TAG = "SingleAlbumAdapter";

        /**
         * Constructor of SingleAlbumAdapter
         *
         * @param activity The activity using the adapter
         * @param data     The data to be adapted
         */
        public SingleAlbumAdapter(Activity activity, List<Map<String, String>> data) {
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
         * @return Not used
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
         * Set the image of each slot of grid view
         * @param position Position of the slot
         * @param convertView Cached previous loaded view
         * @param parent Same as superclass
         * @return If convertView is null, create new one and return, otherwise return the cached view
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            SingleAlbumViewHolder holder;
            View view;

            if (convertView == null) {
                holder = new SingleAlbumViewHolder();
                view = LayoutInflater.from(activity).inflate(R.layout.single_album_row, parent, false);
                holder.galleryImage = (ImageView) view.findViewById(R.id.galleryImage);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (SingleAlbumViewHolder) view.getTag();
            }
            holder.galleryImage.setId(position);

            Map<String, String> song;
            song = data.get(position);
            try {

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
     * Single album view holder, used to speed up rendering
     */
    class SingleAlbumViewHolder {
        ImageView galleryImage;
    }


    /**
     * Load album images in the background
     */
    class LoadAlbumImages extends AsyncTask<String, Void, String> {

        /**
         * Clean image list before execution
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageList.clear();
        }

        /**
         * Construct the image list used by grid view
         * @param args Not used
         * @return Not used
         */
        protected String doInBackground(String... args) {
            String xml = "";

            String path = null;
            String album = null;
            String timestamp = null;
            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};

            Cursor cursorExternal = getContentResolver().query(uriExternal, projection, "bucket_display_name = \"" + albumName + "\"", null, null);
            Cursor cursorInternal = getContentResolver().query(uriInternal, projection, "bucket_display_name = \"" + albumName + "\"", null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});
            while (cursor.moveToNext()) {

                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));

                imageList.add(Function.mappingInbox(album, path, null,timestamp));
            }
            cursor.close();
            Collections.sort(imageList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            return xml;
        }

        /**
         * Set adapter and listener for grid view
         * @param xml Not used
         */
        @Override
        protected void onPostExecute(String xml) {

            SingleAlbumAdapter adapter = new SingleAlbumAdapter(AlbumActivity.this, imageList);
            galleryGridView.setAdapter(adapter);
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    Intent intent = new Intent(AlbumActivity.this, GalleryPreview.class);
                    intent.putExtra("path", imageList.get(+position).get(Function.KEY_PATH));
                    startActivity(intent);
                }
            });
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_album);
        Intent intent = getIntent();
        albumName = intent.getStringExtra("name");
        setTitle(albumName);


        galleryGridView = (GridView) findViewById(R.id.galleryGridView);
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }


        loadAlbumTask = new LoadAlbumImages();
        loadAlbumTask.execute();
    }
}

