package com.example.BitmapEffect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.ImageView;
import com.link.widget.CameraPreview;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created with IntelliJ IDEA.
 * User: Link
 * Date: 13-9-16
 * Time: PM3:52
 * To change this template use File | Settings | File Templates.
 */
public class CompareActivity extends Activity {

    CameraPreview mCameraPreview;
    Camera mCamera;
    ImageView mImg;

    int defaultCameraId;

    PhotoViewAttacher mAttacher;

    String mImgFilePath;

    static public void show(Context ctx,String imgFilePath){
        Intent intent=new Intent(ctx,CompareActivity.class);
        intent.putExtra("img.file.path",imgFilePath);
        ctx.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_compare);

        mImgFilePath=getIntent().getStringExtra("img.file.path");

        mImg = (ImageView) findViewById(R.id.img_main);
        mCameraPreview = (CameraPreview) findViewById(R.id.camera_preview);

        mAttacher = new PhotoViewAttacher(mImg);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);

        //Find the ID of the default camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
            }
        }


        mImg.setImageBitmap(BitmapFactory.decodeFile(mImgFilePath));

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Open the default i.e the first rear facing camera.
        mCamera = Camera.open();
        mCameraPreview.setCamera(mCamera);
    }


    @Override
    protected void onPause() {
        super.onPause();
        //Because the Camera object is a shared resource, it's very
        //important to release it when the activity is paused.
        if (mCamera != null) {
            mCameraPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }
}
