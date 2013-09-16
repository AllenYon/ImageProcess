//package com.example.BitmapEffect;
//
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.res.Resources;
//import android.content.res.TypedArray;
//import android.graphics.Bitmap;
//import android.graphics.ColorFilter;
//import android.graphics.Matrix;
//import android.graphics.RectF;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.View;
//import android.view.accessibility.AccessibilityEvent;
//import android.widget.ImageView;
//import android.widget.ImageView.ScaleType;
//
///**
// * Displays an arbitrary image , such as an icon . The ImageView class
// * can load images from various sources (such as resouces or content
// * providers), takes care of computing its measurement from the image so that
// * it can be used in any layout manager,and provides various display options
// * such as scaling and tining.
// */
//public class DemeImageView extends View {
//    // Settable by the client;
//    private Uri mUri;
//    private int mResource = 0;
//    private Matrix mMatrix;
//    private ImageView.ScaleType mScaleType;
//    private boolean mHaveFrame = false;
//    private boolean mAdjustViewBounds = false;
//    private int mMaxWidth = Integer.MAX_VALUE;
//    private int mMaxHeight = Integer.MAX_VALUE;
//
//    // these are applied to the drawable
//    private ColorFilter mColorFilter;
//    private int mAlpha = 255;
//    private int mViewAlphaScale = 256;
//    private boolean mColorMod = false;
//    private Drawable mDrawable = null;
//    private int[] mState = null;
//    private boolean mMergeState = false;
//    private int mLevel = 0;
//    private int mDrawableWidth;
//    private int mDrawableHeight;
//    private Matrix mDrawMatrix = null;
//
//    //Avoid allocations;
//    private RectF mTempSrc = new RectF();
//    private RectF mTempDst = new RectF();
//
//    private boolean mCropToPadding;
//    private int mBaseline = -1;
//    private boolean mBaselineAlignBottom = false;
//
//    private static final ScaleType[] sScaleTypeArray = {
//            ScaleType.MATRIX,
//            ScaleType.FIT_XY,
//            ScaleType.FIT_END,
//            ScaleType.FIT_START,
//            ScaleType.FIT_CENTER,
//            ScaleType.CENTER,
//            ScaleType.CENTER_CROP,
//            ScaleType.CENTER_INSIDE,
//
//    };
//
//
//    public DemeImageView(Context context) {
//        super(context);
//        initImageView();
//    }
//
//
//    public DemeImageView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public DemeImageView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        initImageView();
//        TypedArray a = context.obtainStyledAttributes(attrs, null, defStyle, 0);
//        Drawable d = a.getDrawable(0);
//
//        if (d != null) {
//            setImageDrawable(d);
//        }
//
//        mBaselineAlignBottom = a.getBoolean(0, false):
//        mBaseline = a.getDimensionPixelSize(0, -1);
//        setAdjustViewBounds(false);
//        setMaxWidth(Integer.MAX_VALUE);
//        setMaxHeight(Integer.MAX_VALUE);
//        int index = a.getInt(0, -1);
//        if (index >= 0) {
//            setScaleType(sScaleTypeArray[index]);
//        }
//
//        int tint = a.getInt(0, 255);
//        if (tint != 0) {
//            setColorFilter(tint);
//        }
//
//        int alpha = a.getInt(0, 255);
//        if (alpha != 255) {
//            setAlpha(alpha);
//        }
//        mCropToPadding = a.getBoolean(0, false);
//        a.recycle();
//
//    }
//
//    private void initImageView() {
//        mMatrix = new Matrix();
//        mScaleType = ImageView.ScaleType.FIT_CENTER;
//    }
//
//    @Override
//    protected boolean verifyDrawable(Drawable who) {
//        return super.verifyDrawable(who) || mDrawable == who;
//    }
//
//    @Override
//    public void jumpDrawablesToCurrentState() {
//        super.jumpDrawablesToCurrentState();
//        if (mDrawable != null) {
//            mDrawable.jumpToCurrentState();
//        }
//    }
//
//    @Override
//    public void invalidateDrawable(Drawable drawable) {
//        if (drawable == mDrawable) {
//            /**
//             * we invalidate the whole view in this case because it's very
//             * hard to know where the drawable actually is .This is made
//             * complicated because of the offsets and transformations that can be
//             * applied. In theory we could get the drawable's bounds
//             * and run them through the transformation and offsets,but this
//             * is probably not worth the effort.
//             */
//            invalidate();
//        } else {
//            super.invalidateDrawable(drawable);
//        }
//    }
//
//    @Override
//    public boolean hasOverlappingRendering() {
//        return (getBackground() != null);
//    }
//
//    public boolean getAdjustViewBounds() {
//        return mAdjustViewBounds;
//    }
//
//    public void setAdjustViewBounds(boolean adjustViewBounds) {
//        mAdjustViewBounds = adjustViewBounds;
//        if (adjustViewBounds) {
//            setScaleType(ScaleType.FIT_CENTER);
//        }
//    }
//
//    public int getMaxWidth() {
//        return mMaxWidth;
//    }
//
//    public void setMaxHeight(int maxHeight) {
//        mMaxHeight = maxHeight;
//    }
//
//    public Drawable getDrawable() {
//        return mDrawable;
//    }
//
//    public void setImageResouce(int resId) {
//        if (mUri != null || mResource != resId) {
//            updateDrawable(null);
//            mResource = resId;
//            mUri = null;
//            final int oldWidth = mDrawableWidth;
//            final int oldHeight = mDrawableHeight;
//            resolveUri();
//            if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
//                requestLayout();
//            }
//            invalidate();
//        }
//    }
//
//    public void setImageURI(Uri uri) {
//        if (mResource != 0 ||
//                (mUri != uri &&
//                        (uri == null || mUri == null || !uri.equals(mUri)))) {
//            updateDrawable(null);
//            mResource = 0;
//            mUri = uri;
//            final int oldWidth = mDrawableWidth;
//            final int oldHeight = mDrawableHeight;
//            resolveUri();
//            if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
//                requestLayout();
//            }
//            invalidate();
//        }
//    }
//
//    public void setImageDrawable(Drawable drawable) {
//        if (mDrawable != drawable) {
//            mResource = 0;
//            mUri = null;
//
//            final int oldWidth = mDrawableWidth;
//            final int oldHeight = mDrawableHeight;
//
//            updateDrawable(drawable);
//
//            if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
//                requestLayout();
//            }
//            invalidate();
//        }
//    }
//
//    public void setImageBitmap(Bitmap bm) {
//        // if this is used frequently, may handle bitmaps explicitly
//        // to reduce the intermediate drawable object
//        setImageDrawable(new BitmapDrawable(mContext.getResource(), bm));
//    }
//
//    public void setImageState(int[] state, boolean merge) {
//        mState = state;
//        mMergeState = merge;
//        if (mDrawable != null) {
//            refreshDrawableState();
//            resizeFromDrawable();
//        }
//    }
//
//    @Override
//    public void setSelected(boolean selected) {
//        super.setSelected(selected);
//        resizeFromDrawable();
//    }
//
//    public void setImageLevel(int level) {
//        mLevel = level;
//        if (mDrawable != null) {
//            mDrawable.setLevel(level);
//            resizeFromDrawable();
//        }
//    }
//
//
//    /**
//     * Controls how the image should be resized or moved to match the size
//     * of this ImageView
//     *
//     * @param scaleType The desired scaling mode
//     */
//    private void setScaleType(ScaleType scaleType) {
//        if (scaleType == null) {
//            throw new NullPointerException();
//        }
//        if (mScaleType != scaleType) {
//            mScaleType = scaleType;
//            setWillNotCacheDrawing(mScaleType == ScaleType.CENTER);
//            requestLayout();
//            invalidate();
//        }
//    }
//
//    public ScaleType getScaleType() {
//        return mScaleType;
//    }
//
//    /**
//     * Return the view's optional matrix. This is applied to the
//     * view's drawable when it is drawn. If there is not matrix,
//     * this method will return null.
//     * Do not change this matrix in place.If you want a different matrix
//     * applied to the drawable, be sure to call setImageMatrix().
//     *
//     * @return
//     */
//    public Matrix getImageMatrix() {
//        return mMatrix;
//    }
//
//    public void setImageMatrix(Matrix matrix) {
//        // collaps null and identity to just null
//        if (matrix != null && mDrawMatrix.isIdentity()) {
//            matrix = null;
//        }
//
//        //don't invalidate unless we're actually changing our matrix
//        if (matrix == null && !mMatrix.isIdentity() || matrix != null && !mMatrix.equals(matrix)) {
//            mMatrix.set(matrix);
//            configureBounds();
//            invalidate();
//        }
//    }
//
//    public boolean getCropToPadding() {
//        return mCropToPadding;
//    }
//
//    public void setCropToPadding(boolean cropToPadding) {
//        if (mCropToPadding != cropToPadding) {
//            mCropToPadding = cropToPadding;
//            requestLayout();
//            invalidate();
//        }
//    }
//
//    private void resolveUri() {
//        if (mDrawable != null) {
//            return;
//        }
//        Resources rsrc = getResources();
//        if (rsrc == null) {
//            return;
//        }
//        Drawable d = null;
//        if (mResource != 0) {
//            try {
//                d = rsrc.getDrawable(mResource);
//            } catch (Exception e) {
//                Log.w("ImageView", "Unable to find resource: " + mResource, e);
//                // Don't try again.
//                mUri = null;
//            }
//        } else if (mUri != null) {
//            String scheme = mUri.getScheme();
//            if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
//                try {
//                    ContentResolver.OpenResourceIdResult r =
//                            mContent.getContentResolver().getResourceId(mUri);
//                    d = r.r.getDrawable(r.id);
//                } catch (Exception e) {
//                    //
//                }
//            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme) || ContentResolver.SCHEME_FILE.equals(scheme)) {
//                d = Drawable.createFromResourceStream(mContent.getContentResolver().openInputStream(mUri), null);
//            } else {
//                d = Drawable.createFromPath(mUri.toString());
//            }
//
//            if (d == null) {
//                mUri = null;
//            }
//
//        } else {
//            return;
//        }
//        updateDrawable(d);
//    }
//
//    private void updateDrawable(Drawable d) {
//        if (mDrawable != null) {
//            mDrawable.setCallback(null);
//            unscheduleDrawable(mDrawable);
//        }
//
//        mDrawable = d;
//        if (d != null) {
//            d.setCallback(this);
//            if (d.isStateful()) {
//                d.setState(getDrawableState());
//            }
//
//            d.setLevel(mLevel);
//            d.setLayoutDirection(getLayoutDirection());
//            mDrawableWidth = d.getIntrinsicWidth();
//            mDrawableHeight = d.getIntrinsicHeight();
//            applyColorMod();
//            configureBounds();
//        } else {
//            mDrawableWidth = mDrawableHeight = -1;
//        }
//    }
//
//    private void resizeFromDrawable() {
//        Drawable d = mDrawable;
//        if (d != null) {
//            int w = d.getIntrinsicWidth();
//            if (w < 0) {
//                w = mDrawableWidth;
//            }
//            int h = d.getIntrinsicHeight();
//            if (h < 0) {
//                h = mDrawableHeight;
//            }
//
//            if (w != mDrawableWidth || h != mDrawableHeight) {
//                mDrawableWidth = w;
//                mDrawableHeight = h;
//                requestLayout();
//            }
//        }
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        resolveUri();
//        int w;
//        int h;
//
//        // Desired aspect ratio of the view's contents( not including padding)
//        float desiredAspect = 0.0f;
//
//        // We are allowed to change the view's width
//        boolean resizeWidth = false;
//        // We are allowed to change the view's height;
//        boolean resizeHeight = false;
//
//        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
//        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
//
//        if (mDrawable == = null) {
//            mDrawableWidth = -1;
//            mDrawableHeight = -1;
//            w = h = 0;
//        } else {
//            w = mDrawableWidth;
//            h = mDrawableHeight;
//            if (w <= 0) {
//                w = 1;
//            }
//            if (h <= 0) {
//                h = 1;
//            }
//            if (mAdjustViewBounds) {
//                resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;
//                resizeHeight = heightMeasureSpec != MeasureSpec.EXACTLY;
//                desiredAspect = (float) w / (float) h;
//            }
//        }
//        int pleft = getPaddingLeft();
//        int pright = getPaddingRight();
//        int ptop = 0;
//        int pbottom = 0;
//
//        int widthSize;
//        int heightSize;
//
//        if (resizeWidth || resizeHeight) {
//            /*
//            If we get here,it means we want to resize to match the
//            drawables aspect ratio,and we have the freedom to change at least one dimension
//             */
//            // Get the max possible width given our constraints
//            widthSize=resolveAdjustedSize(w+pleft+pright,mMaxWidth,widthMeasureSpec):
//
//        }
//    }
//
//
//}
