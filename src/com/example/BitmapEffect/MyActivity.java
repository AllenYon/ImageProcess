package com.example.BitmapEffect;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.link.widget.CameraPreview;

public class MyActivity extends Activity {

    private CameraPreview mPreview;
    Camera mCamera;
    int numberOfCameras;
    int cameraCurrentlyLocked;

    //The first rear facing camera
    int defaultCameraId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the window title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new CameraPreview(this);
        setContentView(mPreview);

        //Find the total number of cameras avaiable
        numberOfCameras = Camera.getNumberOfCameras();

        //Find the ID of the default camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        //Open the default i.e the first rear facing camera.
        mCamera = Camera.open();
        cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
    }


    @Override
    protected void onPause() {
        super.onPause();
        //Because the Camera object is a shared resource, it's very
        //important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }
}
