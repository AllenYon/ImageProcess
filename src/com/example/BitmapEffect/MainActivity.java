package com.example.BitmapEffect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.*;
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
    //Left
    Button mBtnHome,
            mBtnPaint,
            mBtnRotate,
            mBtnErase,
            mBtnHighlight,
            mBtnConver,
            mBtnAnnotion,
            mBtnCompare,
            mBtnFileExplore,
            mBtnSave,
            mBtnSwitch,
            mBtnRecord, //录像
            mBtnTakePhoto; //连拍

    //Right


    //Bottom bar
    Button mBtnPre, mBtnNext, mBtnZoomin, mBtnZoomout;
    ToggleButton mTbDrag;


    ToggleButton mTbPip;
    ToggleButton mTbToolbar;

    ColorPickerDialog mColorPickerDialog;


    private Uri mTakePhotoUri;
    private Uri mSelectedImgUri;

    ImageView mImgMain;
    RelativeLayout mLayoutEffectContain;
    RelativeLayout mLayoutBaseContain;

    // Effect
    MyView mDrawView;
    EditTextScaleRotateView mAnnotionView;
    CropImageView mHighlightView;
    CropImageView mConverView;

    CameraPreview mCameraPreview;
    Camera mCamera;

    int mCurrentDrawID;


    ScrollableBottomBar mLeftScrollableLayout, mRightScrollableLayout;
    ViewGroup mLayoutBottomBar;


    //擦写模式选择
    PopupMenu mEraseTypeMenu;


    public enum DrawState {
        None, Highlight, Conver, PreAnnotion, Annotion, Draw, Erase, Pip
    }

    public enum Type {
        Temp, RealTime, Normal
    }

    private DrawState mCurrentDrawState;
    private Type mType;


    static public void show(Context ctx, Type type) {
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.putExtra("type", type);
        ctx.startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mType = (Type) getIntent().getSerializableExtra("type");


        mLayoutEffectContain = (RelativeLayout) findViewById(R.id.layout_effect_contain);
        mLayoutBaseContain = (RelativeLayout) findViewById(R.id.layout_base_contain);
        mLayoutBottomBar = (ViewGroup) findViewById(R.id.layout_bottom_bar);

        mImgMain = (ImageView) findViewById(R.id.img_main);
        mBtnPaint = (Button) findViewById(R.id.btn_paint);
        mBtnErase = (Button) findViewById(R.id.btn_erase);
        mBtnRotate = (Button) findViewById(R.id.btn_rotate);
        mBtnHighlight = (Button) findViewById(R.id.btn_highlight);
        mBtnConver = (Button) findViewById(R.id.btn_conver);
        mBtnAnnotion = (Button) findViewById(R.id.btn_annotation);
        mBtnCompare = (Button) findViewById(R.id.btn_compare);
        mTbPip = (ToggleButton) findViewById(R.id.btn_pip);
        mTbToolbar = (ToggleButton) findViewById(R.id.tbtn_toolbar);


        mBtnSave = (Button) findViewById(R.id.btn_save);

        //连拍
        mBtnTakePhoto = (Button) findViewById(R.id.btn_takephoto);
        mBtnRecord = (Button) findViewById(R.id.btn_record);

        //文件浏览
        mBtnFileExplore = (Button) findViewById(R.id.btn_file_expolore);
        mBtnFileExplore.setOnClickListener(this);

        //Bottom Bar
        mBtnPre = (Button) findViewById(R.id.btn_pre);
        mBtnNext = (Button) findViewById(R.id.btn_next);
        mBtnZoomin = (Button) findViewById(R.id.btn_zoomin_bottom);
        mBtnZoomout = (Button) findViewById(R.id.btn_zoomout_bottom);


        mLeftScrollableLayout = (ScrollableBottomBar) findViewById(R.id.scrollable_left);
        mRightScrollableLayout = (ScrollableBottomBar) findViewById(R.id.scrollable_right);
        mLeftScrollableLayout.setScrollDirection(ScrollableBottomBar.Direction.Left);
        mRightScrollableLayout.setScrollDirection(ScrollableBottomBar.Direction.Right);


        mBtnPaint.setOnClickListener(this);
        mBtnRotate.setOnClickListener(this);
        mBtnErase.setOnClickListener(this);
        mBtnHighlight.setOnClickListener(this);
        mBtnConver.setOnClickListener(this);
        mBtnAnnotion.setOnClickListener(this);
        mBtnCompare.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        //连拍
        mBtnTakePhoto.setOnClickListener(this);

        mBtnZoomin.setOnClickListener(this);
        mBtnZoomout.setOnClickListener(this);

        //Home
        findViewById(R.id.btn_home).setOnClickListener(this);


        updateType(mType);

        mAttacher = new PhotoViewAttacher(mImgMain);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);


        mTbPip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    mCameraPreview = (CameraPreview) mLayoutEffectContain.findViewById(R.id.camera_preview);
                    if (mCameraPreview == null) {
//                        mLayoutEffectContain.removeAllViews();
                        mCameraPreview = new CameraPreview(MainActivity.this);
                        mCameraPreview.setId(R.id.camera_preview);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
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

        //拖动
        mTbDrag= (ToggleButton) findViewById(R.id.btn_drag);
        mTbDrag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //ToDo
                if (isChecked) {
                    mLayoutEffectContain.setVisibility(View.GONE);
                } else {
                    mLayoutEffectContain.setVisibility(View.VISIBLE);
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mCurrentDrawState == DrawState.PreAnnotion) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                float x = ev.getX();
                float y = ev.getY();

                mAnnotionView = new EditTextScaleRotateView(this, (int) x, (int) y);
                mAnnotionView.setId(R.id.view_annotion);
                mLayoutEffectContain.addView(mAnnotionView,
                        new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                mCurrentDrawState = DrawState.Annotion;
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        //ToDo
        switch (v.getId()) {
            case R.id.btn_home:
                finish();
                break;
            case R.id.btn_paint:
                checkState(mCurrentDrawState);
                doDraw();
                break;
            case R.id.btn_erase:
                checkState(mCurrentDrawState);
                doErase();
                break;
            case R.id.btn_rotate:
                mLayoutEffectContain.setRotation(mLayoutEffectContain.getRotation() + 90);
                mAttacher.rotate90();
                break;
            case R.id.btn_highlight:
                doHighlight();
                break;
            case R.id.btn_annotation:
                checkState(mCurrentDrawState);
                doAnnotation();
                break;
            case R.id.btn_conver:
                doConver();
                break;
            case R.id.btn_compare:
                if (mSelectedImgUri == null) {
                    Toast.makeText(this, "选择一张图片后,才能使用对比功能", Toast.LENGTH_LONG).show();
                } else {
                    CompareActivity.show(this, mSelectedImgUri);
                }
                break;
            case R.id.btn_save:
                doSave();
                break;

            case R.id.btn_takephoto:
                //连拍
                takePhoto();
                break;
            case R.id.btn_file_expolore:
                doPick();
                break;
            case R.id.btn_zoomout_bottom:
                mAttacher.zoomTo(0.5f, mImgMain.getWidth() / 2, mImgMain.getHeight() / 2);
                break;
            case R.id.btn_zoomin_bottom:
                mAttacher.zoomTo(1.5f, mImgMain.getWidth() / 2, mImgMain.getHeight() / 2);
                break;
        }
    }

    private void checkState(DrawState curretnState) {
        if (curretnState == DrawState.Conver) {
            mLayoutEffectContain.removeView(mConverView);
            mConverView = null;
        }
    }

    private void doSave() {
        mLayoutBaseContain.setDrawingCacheEnabled(true);
        mLayoutBaseContain.buildDrawingCache();
        final Bitmap bitmap = mLayoutBaseContain.getDrawingCache();
        final File saveFile = new File(Utils.getDir(), System.currentTimeMillis() + ".png");
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        AlertDialog dialog = b.setTitle("保存图片")
                .setMessage("图片将保存到\n" + saveFile.getAbsolutePath())
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ToDo
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ToDo
                        try {
                            Utils.saveBitmap(bitmap, saveFile);
                            Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "操作异常", Toast.LENGTH_LONG).show();
                        }
                    }
                }).create();
        b.show();
    }

    private void doConver() {
        mConverView = (CropImageView) mLayoutEffectContain.findViewWithTag("conver");
        if (mConverView == null) {
            mConverView = new CropImageView(this);
            mConverView.setTag("conver");
            mLayoutEffectContain.addView(mConverView, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if (mCurrentDrawState != DrawState.Conver) {
            HighlightView hv = makeConver();
            mConverView.mHighlightViews.clear(); // Thong added for rotate
            mConverView.add(hv);
        }
        mCurrentDrawState = DrawState.Conver;
    }

    private void doHighlight() {
        mHighlightView = (CropImageView) mLayoutEffectContain.findViewById(R.id.view_highlight);
        if (mHighlightView == null) {
            mHighlightView = new CropImageView(this);
            mHighlightView.setId(R.id.view_highlight);
            mLayoutEffectContain.addView(mHighlightView, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (mCurrentDrawState != DrawState.Highlight) {
            HighlightView hv = makeHighligth();
            mHighlightView.mHighlightViews.clear(); // Thong added for rotate
            mHighlightView.add(hv);
        }
        mCurrentDrawState = DrawState.Highlight;
    }

    private void doAnnotation() {
        mCurrentDrawState = DrawState.PreAnnotion;

//        mAnnotionView = new EditTextScaleRotateView(this);
//        mAnnotionView.setId(R.id.view_annotion);
//        mLayoutEffectContain.addView(mAnnotionView,
//                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT));
//        mCurrentDrawState = DrawState.Annotion;
    }

    private void doErase() {
//        mDrawView = (MyView) mLayoutEffectContain.findViewById(R.id.view_draw);
        mDrawView = (MyView) mLayoutEffectContain.findViewWithTag(mCurrentDrawID);
        if (mDrawView != null) {
            //擦写模式
            mDrawView.bringToFront();
            showEraseTypeMenu(mBtnErase);
            mCurrentDrawState = DrawState.Erase;
        }
    }

    private void doDraw() {
        if (mColorPickerDialog == null) {
            mColorPickerDialog = new ColorPickerDialog(this, Color.BLACK);
        }
        mColorPickerDialog.setOnPaintChangedListener(this);
        mColorPickerDialog.setAlphaSliderVisible(true);
        mColorPickerDialog.show();

//        mDrawView = (MyView) mLayoutEffectContain.findViewById(R.id.view_draw);
//        if (mDrawView == null) {
//            mLayoutEffectContain.removeAllViews();
//            mDrawView = new MyView(this);
//            mDrawView.setId(R.id.view_draw);
//            mLayoutEffectContain.addView(mDrawView,
//                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        }

        mDrawView = new MyView(this);
        mCurrentDrawID++;
//        mDrawView.setId(R.id.view_draw);
        mDrawView.setTag(mCurrentDrawID);
        mLayoutEffectContain.addView(mDrawView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mDrawView.setMode(MyView.Mode.Draw);
        mCurrentDrawState = DrawState.Draw;
    }

    private void doPick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
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
        HighlightView hv = new HighlightView(mConverView);
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

    private void updateType(Type type) {
        this.mType = type;
        switch (mType) {
            case Normal:
                mRightScrollableLayout.setVisibility(View.GONE);
                mLayoutBottomBar.setVisibility(View.VISIBLE);
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
                mBtnRecord.setClickable(false);
                mBtnTakePhoto.setClickable(false);
                break;
            case Temp:
                mRightScrollableLayout.setVisibility(View.GONE);
                mLayoutBottomBar.setVisibility(View.VISIBLE);
                mBtnPre.setVisibility(View.GONE);
                mBtnNext.setVisibility(View.GONE);
                mBtnRecord.setClickable(false);
                mBtnTakePhoto.setClickable(false);
                break;

            case RealTime:
            default:
                mRightScrollableLayout.setVisibility(View.VISIBLE);
                mLayoutBottomBar.setVisibility(View.GONE);
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);

                mBtnRecord.setClickable(true);
                mBtnTakePhoto.setClickable(true);

                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                mSelectedImgUri = data.getData();
                try {
                    Bitmap bb = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedImgUri);
                    mImgMain.setImageBitmap(bb);
                    mAttacher.update();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                updateType(Type.Normal);

            } else if (requestCode == 101) {
                try {
                    Toast.makeText(this, "uri " + mTakePhotoUri.toString(), Toast.LENGTH_LONG).show();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mTakePhotoUri);
                    mImgMain.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 102) {
//                String saveFile = data.getStringExtra("savefile");
//                Bitmap bitmap = BitmapFactory.decodeFile(saveFile);
//                mImgMain.setImageBitmap(bitmap);
//                mAttacher.update();

            }

        }
    }
}
