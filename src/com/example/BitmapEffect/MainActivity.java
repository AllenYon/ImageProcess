package com.example.BitmapEffect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.arcsoft.sample.widgets.EditTextScaleRotateView;
import com.link.widget.CameraPreview;
import com.link.widget.CropImageView;
import com.link.widget.HighlightView;
import com.link.widget.ScrollableBottomBar;
import net.margaritov.preference.colorpicker.ColorPickerDialog;
import net.micode.fileexplorer.FileExplorerTabActivity;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    Button mBtnSave;
    ToggleButton mTbPip;
    ToggleButton mTbToolbar;

    ColorPickerDialog mColorPickerDialog;


    private Uri mTakePhotoUri;
    private String mSelectedImgFilePath;

    ImageView mImgMain;
    RelativeLayout mLayoutEffectContain;
    RelativeLayout mLayoutBaseContain;

    // Effect
    MyView mDrawView;
    EditTextScaleRotateView mAnnotionView;
    CropImageView mHighlightView;

    CameraPreview mCameraPreview;
    Camera mCamera;


    ScrollableBottomBar mLeftScrollableLayout, mRightScrollableLayout;


    //擦写模式选择
    PopupMenu mEraseTypeMenu;


    public enum DrawState {
        None, Highlight, Conver, Annotion, Draw, Erase, Pip
    }

    private DrawState mCurrentDrawState;


    static public void show(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mLayoutEffectContain = (RelativeLayout) findViewById(R.id.layout_effect_contain);
        mLayoutBaseContain = (RelativeLayout) findViewById(R.id.layout_base_contain);

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
        mTbToolbar = (ToggleButton) findViewById(R.id.tbtn_toolbar);

        mBtnSave = (Button) findViewById(R.id.btn_save);

        mLeftScrollableLayout = (ScrollableBottomBar) findViewById(R.id.scrollable_left);
        mRightScrollableLayout = (ScrollableBottomBar) findViewById(R.id.scrollable_right);
        mLeftScrollableLayout.setScrollDirection(ScrollableBottomBar.Direction.Left);
        mRightScrollableLayout.setScrollDirection(ScrollableBottomBar.Direction.Right);

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
        mBtnSave.setOnClickListener(this);


        mAttacher = new PhotoViewAttacher(mImgMain);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);


        mTbPip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    mCameraPreview = (CameraPreview) mLayoutEffectContain.findViewById(R.id.camera_preview);
                    if (mCameraPreview == null) {
                        mLayoutEffectContain.removeAllViews();
                        mCameraPreview = new CameraPreview(MainActivity.this);
                        mCameraPreview.setId(R.id.camera_preview);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(200, 200);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        mLayoutEffectContain.addView(mCameraPreview, lp);
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
                    mLayoutEffectContain.removeAllViews();
                    mCameraPreview = null;
                }


            }
        });
        mTbToolbar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //ToDo
                mLeftScrollableLayout.toggle();
                mRightScrollableLayout.toggle();
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
//                Intent intent = new Intent(this, FileExplorerTabActivity.class);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
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

                mDrawView = (MyView) mLayoutEffectContain.findViewById(R.id.view_draw);
                if (mDrawView == null) {
                    mLayoutEffectContain.removeAllViews();
                    mDrawView = new MyView(this);
                    mDrawView.setId(R.id.view_draw);
                    mLayoutEffectContain.addView(mDrawView,
                            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                mDrawView.setMode(MyView.Mode.Draw);
                mCurrentDrawState = DrawState.Draw;
                break;
            case R.id.btn_erase:
                mDrawView = (MyView) mLayoutEffectContain.findViewById(R.id.view_draw);
                if (mDrawView == null) {
                    mLayoutEffectContain.removeAllViews();
                    mDrawView = new MyView(this);
                    mDrawView.setId(R.id.view_draw);
                    mLayoutEffectContain.addView(mDrawView,
                            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }

                //擦写模式

                showEraseTypeMenu(mBtnErase);
//                mDrawView.setMode(MyView.Mode.Erase);
                mCurrentDrawState = DrawState.Erase;
                break;
            case R.id.btn_rotate:
                mAttacher.rotate90();
                break;
            case R.id.btn_take_photo:
                takePhoto();
                break;
            case R.id.btn_highlight:
                mHighlightView = (CropImageView) mLayoutEffectContain.findViewById(R.id.view_highlight);
                if (mHighlightView == null) {
                    mLayoutEffectContain.removeAllViews();
                    mHighlightView = new CropImageView(this);
                    mHighlightView.setId(R.id.view_highlight);
                    mLayoutEffectContain.addView(mHighlightView,
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
                    mLayoutEffectContain.removeAllViews();
                    mAnnotionView = new EditTextScaleRotateView(this);
                    mAnnotionView.setId(R.id.view_annotion);
                    mLayoutEffectContain.addView(mAnnotionView,
                            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }

                if (mCurrentDrawState != DrawState.Annotion) {
                    mAnnotionView.setBackgroundColor(Color.BLACK);
                }

                mCurrentDrawState = DrawState.Annotion;

                break;
            case R.id.btn_conver:

                mHighlightView = (CropImageView) mLayoutEffectContain.findViewById(R.id.view_highlight);
                if (mHighlightView == null) {
                    mLayoutEffectContain.removeAllViews();
                    mHighlightView = new CropImageView(this);
                    mHighlightView.setId(R.id.view_highlight);
                    mLayoutEffectContain.addView(mHighlightView,
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
            case R.id.btn_save:
                mLayoutBaseContain.setDrawingCacheEnabled(true);
                mLayoutBaseContain.buildDrawingCache();
                Bitmap bitmap = mLayoutBaseContain.getDrawingCache();

                int d = bitmap.getHeight();

                File saveFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png");
                try {
                    FileOutputStream fos = new FileOutputStream(saveFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    private void showEraseTypeMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.erase_brush:
                        mDrawView.setMode(MyView.Mode.EraseBrush);
                        return true;

                    case R.id.erase_area:
                        mDrawView.setMode(MyView.Mode.EraseArea);
                        return true;

                    default:
                        return false;  //ToDo
                }
            }
        });
        popup.inflate(R.menu.erase_menu);
        popup.show();
    }

    private HighlightView makeHighligth() {
        HighlightView hv = new HighlightView(mHighlightView);
        Rect imageRect = new Rect(0, 0, mLayoutEffectContain.getWidth(), mLayoutEffectContain.getHeight());
        // make the default size about 4/5 of the width or height
//            int cropWidth = Math.min(width, height) * 4 / 5;
        int cropWidth = 300;
        int cropHeight = 200;
        int x = (mLayoutEffectContain.getWidth() - cropWidth) / 2;
        int y = (mLayoutEffectContain.getHeight() - cropHeight) / 2;
        RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
        hv.setup(new Matrix(), imageRect, cropRect, false, false);
        hv.setFocus(true);
        return hv;
    }

    private HighlightView makeConver() {
        HighlightView hv = new HighlightView(mHighlightView);
        Rect imageRect = new Rect(0, 0, mLayoutEffectContain.getWidth(), mLayoutEffectContain.getHeight());
        // make the default size about 4/5 of the width or height
//            int cropWidth = Math.min(width, height) * 4 / 5;
        int cropWidth = 300;
        int cropHeight = 200;
        int x = (mLayoutEffectContain.getWidth() - cropWidth) / 2;
        int y = (mLayoutEffectContain.getHeight() - cropHeight) / 2;
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
        Intent intent = new Intent(this, TakePhotoActivity.class);
        startActivityForResult(intent, 102);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
//                mSelectedImgFilePath = selectedImageUri.getPath();
//                Bitmap bitmap = BitmapFactory.decodeFile(mSelectedImgFilePath);

                try {
                    Bitmap bb = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    mImgMain.setImageBitmap(bb);
                    mAttacher.update();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 101) {
                try {
                    Toast.makeText(this, "uri " + mTakePhotoUri.toString(), Toast.LENGTH_LONG).show();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mTakePhotoUri);
                    mImgMain.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 102) {
                String saveFile = data.getStringExtra("savefile");
                Bitmap bitmap = BitmapFactory.decodeFile(saveFile);
                mImgMain.setImageBitmap(bitmap);
                mAttacher.update();
            }

        }
    }
}
