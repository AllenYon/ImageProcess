package com.example.BitmapEffect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.link.widget.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TakePhotoActivity extends Activity implements View.OnClickListener {

    private CameraPreview mPreview;
    Camera mCamera;

    Button mBtnCancel, mBtnOK;


    static public void show(Context ctx) {
        Intent intent = new Intent(ctx, TakePhotoActivity.class);
        ctx.startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_takephoto);

        mPreview = (CameraPreview) findViewById(R.id.camera_preview);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnOK = (Button) findViewById(R.id.btn_ok);


        mBtnOK.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Open the default i.e the first rear facing camera.
        mCamera = Camera.open();
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

    @Override
    public void onClick(View v) {
        //ToDo
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_ok:
                mCamera.takePicture(mShutterCallback, mRawCallback, mJPEGCallback);
                break;

        }
    }

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            //ToDo
        }
    };
    private Camera.PictureCallback mRawCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //ToDo
        }
    };
    private Camera.PictureCallback mJPEGCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //ToDo
            File saveFile = Utils.savePicture(data);
            Intent idata = new Intent(TakePhotoActivity.this, MainActivity.class);
            idata.putExtra("type", MainActivity.Type.Temp);
            idata.putExtra("savefile", saveFile.getAbsolutePath());
            startActivity(idata);

            setResult(RESULT_OK, idata);
            finish();


        }
    };


}
