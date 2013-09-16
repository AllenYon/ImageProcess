//package com.arcsoft.sample;
//
//import android.content.Context;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.AttributeSet;
//import android.view.GestureDetector;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.TextView;
//import com.arcsoft.sample.graphics.TextDrawable;
//import com.arcsoft.sample.widgets.DrawableHighlightView;
//import com.arcsoft.sample.widgets.EditTextScaleRotateView;
//
//public class DemoETSR extends EditText {
//    DrawableHighlightView mHighlightView;
//    TextDrawable mTextDraw;
//    GestureDetector mGestureDetector;
//    ScaleRotateListener mScaleRotateListener;
//    TextWatcher mTextWatcher;
//    OnEditorActionListener mOnEditorActionListener;
//    InputMethodManager mInputMethodManager;
//    int mMotionEdge;
//
//    static public class TextState {
//        public String mText;
//        public float mTextSize;
//        public int mTextColor;
//        public int mPadding;
//        public float mRectCenterX;
//        public float mRectCenterY;
//        public float mDegree;
//        public float mStrokeWidth;
//        public int mOutlineEllipse;
//        public int mOutlineStrokeColor;
//
//        public TextState() {
//            mText = "";
//            mTextColor = 0xffff0000;
//            mTextSize = 24;
//            mRectCenterX = 0;
//            mRectCenterY = 0;
//            mDegree = 0;
//            mOutlineEllipse = 12;
//            mOutlineStrokeColor = 0xff00FF00;
//            mStrokeWidth = 5;
//            mPadding = 15;
//        }
//    }
//
//    public DemoETSR(Context context) {
//        super(context);
//    }
//
//    public DemoETSR(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public DemoETSR(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        init();
//    }
//
//    private void init() {
//        mScaleRotateListener = new ScaleRotateListener();
//        mGestureDetector=new GestureDetector(getContext(),mScaleRotateListener);
//        mMotionEdge=DrawableHighlightView.GROW_NONE;
//        mTextWatcher=new MyTextWatcher();
//        mOnEditorActionListener=new MyOnEditorActionListener();
//        mInputMethodManager= (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
//        TextState state=new TextState();
//        state.mText="";
//        state.mRectCenterX=400;
//        state.mRectCenterY=200;
//        initBy(state);
//
//    }
//
//    public int initBy(TextState state) {
//        if (null==state){
//            return -1;
//        }
//        if (null!=mTextDraw){
//            mTextDraw=null;
//        }
//        mTextDraw=new TextDrawable(state.mText,state.mTextSize);
//        mTextDraw.setTextColor(state.mTextColor);
//        String hint= "Enter text here";
//        mTextDraw.setTextHint(hint);
//        mTextDraw.setTextHint(state.mText);
//        mTextDraw.setTextSize(state.mTextSize);
//
//
//
//    }
//
//    public class ScaleRotateListener extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            return super.onSingleTapUp(e);
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            return super.onScroll(e1, e2, distanceX, distanceY);
//        }
//    }
//
//    public class MyTextWatcher implements TextWatcher{
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            //ToDo
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            //ToDo
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            //ToDo
//        }
//    }
//
//    public class MyOnEditorActionListener implements OnEditorActionListener{
//
//        @Override
//        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//            return false;  //ToDo
//        }
//    }
//}
