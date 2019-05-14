package com.example.picart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Handle all the things when press the camera button
 */
class CameraHandler {
    private static final String TAG = "CameraHandler";
    private File photoFile;
    private String currentPhotoPath;
    private final MainActivity mainActivity;

    private static final int REQUEST_CODE = 2;

    /**
     * Constructor for camera handler
     * @param mainActivity The MainActivity that calls this handler
     */
    CameraHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * Make an intent to use camera
     */
    @SuppressLint("VisibleForTests")
    private void getPhotoFile() {
        // Create the File where the photo should go
        try {
            photoFile = ImageFileFactory.createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = photoFile.getAbsolutePath();

        }
        mainActivity.pSetter(photoFile);
    }

    /**
     * Create a camera intent then go to that intent
     */
    void dispatchTakePictureIntent() {
        getPhotoFile();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mainActivity.getPackageManager()) != null && photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(mainActivity,
                    "com.example.picart.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            mainActivity.startActivityForResult(takePictureIntent, REQUEST_CODE);
        }
    }

    /**
     * Add the captured picture to gallery
     */
    void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mainActivity.sendBroadcast(mediaScanIntent);
    }

    /**
     * Check whether user take a photo and confirm or not, if the file is empty, then delete it
     */
    void deleteEmptyImage() {
        try {
            File f = new File(currentPhotoPath);
            if (f.length() == 0) {
                if (f.delete()) {
                    Log.d(TAG, "deleteEmptyImage: Delete empty file");
                } else {
                    Log.e(TAG, "deleteEmptyImage: Fail to delete the empty file");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteEmptyImage: ", e);
        }
    }

    /**
     * After saving the image, make a toast to show the path of image
     */
    void showSavePath() {
        String toastMsg = String.format("Image is saved to %s", currentPhotoPath);
        Toast.makeText(mainActivity, toastMsg, Toast.LENGTH_LONG).show();
    }

    /**
     * Get the camera request code, which should be 2
     * @return The camera request code
     */
    int getRequestCode() {
        return REQUEST_CODE;
    }
}
