package com.example.picart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.tensorflow.demo.StylizeActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.kareluo.imaging.IMGEditActivity;

/**
 * Gallery preview activity. Entered when click an image in album activity.
 */
public class GalleryPreview extends AppCompatActivity {
    static final private int GET_CODE = 2;
    ImageView galleryPreviewImg;
    String path;
    boolean deleteFlag = false;
    /**
     * Set up an image view to the content of clicked image
     *
     * @param savedInstanceState Not used
     */





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.gallery_preview);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        galleryPreviewImg = (ImageView) findViewById(R.id.GalleryPreviewImg);
        Glide.with(this)
                .load(new File(path)) // Uri of the picture
                .into(galleryPreviewImg);
    }

    /**
     * edit image: plot;massaic;clip
     */
    public void editingPhoto(){
        Intent intent = new Intent(GalleryPreview.this, IMGEditActivity.class);
        intent.putExtra(IMGEditActivity.EXTRA_IMAGE_URI,  Uri.fromFile(new File(path)));
        intent.putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH, path);
        //startActivity(intent);
        startActivityForResult(intent,GET_CODE);
        System.out.println("启动结束");
    }

    /*
    * alart window when delete image
    * */
    public boolean deleteAlert(){
        // 2、带按钮的AlertDialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("DELETE");
        dialog.setMessage("Sure to delete the photo？");

        // 设置“确定”按钮,使用DialogInterface.OnClickListener接口参数
        dialog.setPositiveButton("delete",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePhoto();
//                        mainActivity.startActivityForResult(takePictureIntent, REQUEST_CODE);
                        GalleryPreview.this.finish();
                    }
                });

        // 设置“取消”按钮,使用DialogInterface.OnClickListener接口参数
        dialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFlag = false;
                    }
                });
        dialog.show();
        return deleteFlag;
    }

    /*
    *
    * */
    public static void updateFileFromDatabase(Context context,File file){
        String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
        MediaScannerConnection.scanFile(context, paths, null, null);
        MediaScannerConnection.scanFile(context, new String[] {
                        file.getAbsolutePath()},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri)
                    {
                    }
                });
    }
    public boolean deletePhoto(){
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                // update MediaStore:
                getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{path});//删除系统缩略图
                Toast.makeText(getApplicationContext(), "删除文件:" + path , Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "删除单个文件" + path + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "删除单个文件失败：" + path + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public void transferphoto(){
        Intent intent = new Intent(GalleryPreview.this, StylizeActivity.class);
        System.out.println("取得："+path);
        intent.putExtra("path",  path);
        //startActivity(intent);
        startActivityForResult(intent,GET_CODE);
        System.out.println("启动结束");
    }
    /** * 从Assets中读取图片 */
    private Bitmap getImageFromAssetsFile(String fileName){
//        Bitmap image = null;
//        AssetManager am = getResources().getAssets();
//        try {
//            InputStream is=am.open(fileName);
//            image= BitmapFactory.decodeStream(is);
//            is.close();
//        } catch (IOException e) {
//            e.printStackTrace();    }
        FileInputStream in;
        try {
             in = new FileInputStream(fileName);
             Bitmap bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    /** * 将图片存到本地 */
    private static Uri saveBitmap(Bitmap bm, String picName) {
        try {
            String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/renji/"+picName+".jpg";
            File f = new File(dir);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            System.out.println("路径"+f.getAbsolutePath());
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Uri uri = Uri.fromFile(f);
            return uri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();    }
        return null;
    }

    public void sharePhoto(){
//        Intent intent=new Intent(Intent.ACTION_SEND);
//        intent.setType("image/*");
//
//        File file = new File(path);//这里share.jpg是sd卡根目录下的一个图片文件
//        Uri imageUri = Uri.fromFile(file);
//        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
//        startActivity(Intent.createChooser(intent, "share to"));
        /** * 分享图片 */
//        Bitmap bgimg0 = getImageFromAssetsFile(path);
//        Intent share_intent = new Intent();
//        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
//        share_intent.setType("image/*");  //设置分享内容的类型
//        share_intent.putExtra(Intent.EXTRA_STREAM, saveBitmap(bgimg0,"img"));
//        //创建分享的Dialog
//        share_intent = Intent.createChooser(share_intent, "share to");
//        GalleryPreview.this.startActivity(share_intent);

        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.setType("image/jpeg");
        imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        startActivity(Intent.createChooser(imageIntent, "分享"));
    }

    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.rb_edit) {
            editingPhoto();
        } else if (vid == R.id.btn_filter) {
         //   Toast.makeText(getApplicationContext(), "unfinished", Toast.LENGTH_SHORT).show();
            transferphoto();
        }
        else if (vid == R.id.btn_back) {
            super.onBackPressed();
        }
        else if (vid == R.id.btn_share) {
            sharePhoto();
        }
        else if (vid == R.id.btn_delete) {
            if(deleteAlert()){

                deleteFlag = false;
                super.onBackPressed();
            }
//            Toast.makeText(getApplicationContext(), "删除结果：" +deleteFlag, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Toast.makeText(getApplicationContext(), "request Code:"+requestCode, Toast.LENGTH_SHORT).show();
        if(requestCode == GET_CODE)
        {
            if(resultCode == RESULT_CANCELED)
            {
                //Toast.makeText("点击了返回");
                Toast.makeText(getApplicationContext(), "点击了返回", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (data != null)
                {
                    Glide.with(this)
                            .load(new File(data.getAction())) // Uri of the picture
                            .into(galleryPreviewImg);
                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{path});//删除系统缩略图
                    path = data.getAction();
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File f = new File(path);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
//                    text2.setText("得到第二个activity返回的结果:\n"+data.getAction());
                    Toast.makeText(getApplicationContext(), "得到第二个activity返回的结果"+data, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
