package com.example.BitmapEffect;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import com.arcsoft.sample.widgets.EditTextScaleRotateView;
import com.link.widget.CameraPreview;
import com.link.widget.CropImageView;
import com.link.widget.HighlightView;
import net.margaritov.preference.colorpicker.ColorPickerDialog;
import net.micode.fileexplorer.FileExplorerTabActivity;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener, ColorPickerDialog.OnPaintChangedListener {
    PhotoViewAttacher mAttacher;
    Button mBtnPick,
            mBtnPaint,
            mBtnRotate,
            mBtnErase,
            mBtnTakePhoto,
            mBtnHighlight,
            mBtnConver,
            mBtnAnnotion,
            mBtnCompare;
    //            mBtnPip;
    ToggleButton mTbPip;

    ColorPickerDialog mColorPickerDialog;


    private Uri mTakePhotoUri;
    private String mSelectedImgFilePath;

    ImageView mImgMain;
    RelativeLayout mLayoutContain;

    // Effect
    MyView mDrawView;
    EditTextScaleRotateView mAnnotionView;
    CropImageView mHighlightView;

    CameraPreview mCameraPreview;
    Camera mCamera;


    public enum DrawState {
        None, Highlight, Conver, Annotion, Draw, Erase, Pip
    }

    private DrawState mCurrentDrawState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mLayoutContain = (RelativeLayout) findViewById(R.id.layout_contain);

        mImgMain = (ImageView) findViewById(R.id.img_main);
//        mView = (MyView) findViewById(R.id.view_draw);
//        mAnnotionView = (EditTextScaleRotateView) findViewById(R.id.view_annotion);
//        mHighlightView = (CropImageView) findViewById(R.id.view_highlight);


        mBtnPick = (Button) findViewById(R.id.btn_pick);
        mBtnPaint = (Button) findViewById(R.id.btn_paint);
        mBtnErase = (Button) findViewById(R.id.btn_erase);
        mBtnRotate = (Button) findViewById(R.id.btn_rotate);
        mBtnTakePhoto = (Button) findViewById(R.id.btn_take_photo);
        mBtnHighlight = (Button) findViewById(R.id.btn_highlight);
        mBtnConver = (Button) findViewById(R.id.btn_conver);
        mBtnAnnotion = (Button) findViewById(R.id.btn_annotation);
        mBtnCompare = (Button) findViewById(R.id.btn_compare);
//        mBtnPip = (Button) findViewById(R.id.btn_pip);
        mTbPip = (ToggleButton) findViewById(R.id.btn_pip);

        mBtnPick.setOnClickListener(this);
        mBtnPaint.setOnClickListener(this);
        mBtnRotate.setOnClickListener(this);
        mBtnErase.setOnClickListener(this);
        mBtnTakePhoto.setOnClickListener(this);
        mBtnHighlight.setOnClickListener(this);
        mBtnConver.setOnClickListener(this);
        mBtnAnnotion.setOnClickListener(this);
        mBtnCompare.setOnClickListener(this);
//        mBtnPip.setOnClickListener(this);


        mAttacher = new PhotoViewAttacher(mImgMain);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);


        mTbPip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    mCameraPreview = (CameraPreview) mLayoutContain.findViewById(R.id.camera_preview);
                    if (mCameraPreview == null) {
                        mLayoutContain.removeAllViews();
                        mCameraPreview = new CameraPreview(MainActivity.this);
                        mCameraPreview.setId(R.id.camera_preview);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(200, 200);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        mLayoutContain.addView(mCameraPreview, lp);
                    }
                    //Open the default i.e the first rear facing camera.
                    mCamera = Camera.open();
                    mCameraPreview.setCamera(mCamera);

                } else {
                    if (mCamera != null) {
                        mCameraPreview.setCamera(null);
                        mCamera.release();
                        mCamera = null;
                    }

                    // The toggle is disabled
                    mLayoutContain.removeAllViews();
                    mCameraPreview = null;
                }


            }
        });

        mCurrentDrawState = DrawState.None;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAttacher.cleanup();
    }

    @Override
    public void onClick(View v) {
        //ToDo
        switch (v.getId()) {
            case R.id.btn_pick:
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                startActivity(intent);
                Intent intent = new Intent(this, FileExplorerTabActivity.class);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,
//                        "Select Picture"), 1);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_paint:
                if (mColorPickerDialog == null) {
                    mColorPickerDialog = new ColorPickerDialog(this, Color.BLACK);
                }
//                mColorPickerDialog.setOnColorChangedListener(this);
                mColorPickerDialog.setOnPaintChangedListener(this);
                mColorPickerDialog.setAlphaSliderVisible(true);
//                mColorPickerDialog.setHexValueEnabled(true);
//                if (state != null) {
//                    mDialog.onRestoreInstanceState(state);
//                }
                mColorPickerDialog.show();

                mDrawView = (MyView) mLayoutContain.findViewById(R.id.view_draw);
                if (mDrawView == null) {
                    mLayoutContain.removeAllViews();
                    mDrawView = new MyView(this);
                    mDrawView.setId(R.id.view_draw);
                    mLayoutContain.addView(mDrawView,
                            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                mDrawView.setMode(MyView.Mode.Draw);
                mCurrentDrawState = DrawState.Draw;
                break;
            case R.id.btn_erase:
                mDrawView = (MyView) mLayoutContain.findViewById(R.id.view_draw);
                if (mDrawView == null) {
                    mLayoutContain.removeAllViews();
                    mDrawView = new MyView(this);
                    mDrawView.setId(R.id.view_draw);
                    mLayoutContain.addView(mDrawView,
                            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                mDrawView.setMode(MyView.Mode.Erase);
                mCurrentDrawState = DrawState.Erase;
                break;
            case R.id.btn_rotate:
                mAttacher.rotate90();
                break;
            case R.id.btn_take_photo:

//                takePhoto();
                break;
            case R.id.btn_highlight:
                mHighlightView = (CropImageView) mLayoutContain.findViewById(R.id.view_highlight);
                if (mHighlightView == null) {
                    mLayoutContain.removeAllViews();
                    mHighlightView = new CropImageView(this);
                    mHighlightView.setId(R.id.view_highlight);
                    mLayoutContain.addView(mHighlightView,
                            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }

                if (mCurrentDrawState != DrawState.Highlight) {
                    HighlightView hv = makeHighligth();
                    mHighlightView.mHighlightViews.clear(); // Thong added for rotate
                    mHighlightView.add(hv);
                }

                mCurrentDrawState = DrawState.Highlight;
                break;
            case R.id.btn_annotation:
                mAnnotionView = (EditTextScaleRotateView) findViewById(R.id.view_annotion);
                if (mAnnotionView == null) {
                    mLayoutContain.removeAllViews();
                    mAnnotionView = new EditTextScaleRotateView(this);
                    mAnnotionView.setId(R.id.view_annotion);
                    mLayoutContain.addView(mAnnotionView,
                            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }

                if (mCurrentDrawState != DrawState.Annotion) {
                    mAnnotionView.setBackgroundColor(Color.BLACK);
                }

                mCurrentDrawState = DrawState.Annotion;

                break;
            case R.id.btn_conver:

                mHighlightView = (CropImageView) mLayoutContain.findViewById(R.id.view_highlight);
                if (mHighlightView == null) {
                    mLayoutContain.removeAllViews();
                    mHighlightView = new CropImageView(this);
                    mHighlightView.setId(R.id.view_highlight);
                    mLayoutContain.addView(mHighlightView,
                            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                if (mCurrentDrawState != DrawState.Conver) {
                    HighlightView hv = makeConver();
                    mHighlightView.mHighlightViews.clear(); // Thong added for rotate
                    mHighlightView.add(hv);
                }

                mCurrentDrawState = DrawState.Conver;

                break;
            case R.id.btn_compare:
                if (!TextUtils.isEmpty(mSelectedImgFilePath)) {
                    CompareActivity.show(this, mSelectedImgFilePath);
                }

                break;
            case R.id.btn_pip:


                break;

        }
    }


    private HighlightView makeHighligth() {
        HighlightView hv = new HighlightView(mHighlightView);
        Rect imageRect = new Rect(0, 0, mLayoutContain.getWidth(), mLayoutContain.getHeight());
        // make the default size about 4/5 of the width or height
//            int cropWidth = Math.min(width, height) * 4 / 5;
        int cropWidth = 300;
        int cropHeight = 200;
        int x = (mLayoutContain.getWidth() - cropWidth) / 2;
        int y = (mLayoutContain.getHeight() - cropHeight) / 2;
        RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
        hv.setup(new Matrix(), imageRect, cropRect, false, false);
        hv.setFocus(true);
        return hv;
    }

    private HighlightView makeConver() {
        HighlightView hv = new HighlightView(mHighlightView);
        Rect imageRect = new Rect(0, 0, mLayoutContain.getWidth(), mLayoutContain.getHeight());
        // make the default size about 4/5 of the width or height
//            int cropWidth = Math.min(width, height) * 4 / 5;
        int cropWidth = 300;
        int cropHeight = 200;
        int x = (mLayoutContain.getWidth() - cropWidth) / 2;
        int y = (mLayoutContain.getHeight() - cropHeight) / 2;
        RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
        hv.setup(new Matrix(), imageRect, cropRect, false, false);
        hv.setFocus(false);
        return hv;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    public void onPaintChanged(int color, int strokeWidth) {
        //ToDo

        mDrawView.setPaintColorAndStrokeWidth(color, strokeWidth);
    }


    public void takePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        mTakePhotoUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri);
        startActivityForResult(intent, 101);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                mSelectedImgFilePath = selectedImageUri.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(mSelectedImgFilePath);
                mImgMain.setImageBitmap(bitmap);
                mAttacher.update();
            }
            if (requestCode == 101) {
                try {
                    Toast.makeText(this, "uri " + mTakePhotoUri.toString(), Toast.LENGTH_LONG).show();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mTakePhotoUri);
                    mImgMain.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
