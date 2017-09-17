package com.zhuimeng.zoomimage.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

/**
 * Created by zhuimeng on 2017/7/14.
 */

public class ZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private boolean mOnce = false;
    private float mInitScale;//初始化时缩放值
    private float mMidScale;//双击放大值
    private float mMaxScale;//放大最大值
    private Matrix mScaleMatrix;
    private ScaleGestureDetector mScaleGD;//捕获用户多点触控缩放比例值
    //-------------自由移动-------------
    private int mLastPointerCount;//记录上一次多点触控的数量
    private float mLastX;
    private float mLastY;
    private int mTouchSlop;
    private boolean isCanDrag;
    private RectF matrixRectF;
    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;
    //-------------双击放大和缩小--------
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrix = new Matrix();
        super.setScaleType(ScaleType.MATRIX);
        mScaleGD = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale){//正在自动缩放时，不再响应双击
                            return true;
                        }
                        float x = e.getX();
                        float y = e.getY();
                        if (getScale() < mMidScale){
//                            mScaleMatrix.postScale(mMidScale / getScale(), mMidScale / getScale(), x, y);
//                            setImageMatrix(mScaleMatrix);
                            postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
                            isAutoScale = true;
                        } else {
//                            mScaleMatrix.postScale(mInitScale / getScale(), mInitScale / getScale(), x, y);
//                            setImageMatrix(mScaleMatrix);
                            postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
                            isAutoScale = true;
                        }
                        return true;
                    }
                });
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mScaleMatrix = new Matrix();
        super.setScaleType(ScaleType.MATRIX);
        mScaleGD = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale){//正在自动缩放时，不再响应双击
                            return true;
                        }
                        float x = e.getX();
                        float y = e.getY();
                        if (getScale() < mMidScale){
//                            mScaleMatrix.postScale(mMidScale / getScale(), mMidScale / getScale(), x, y);
//                            setImageMatrix(mScaleMatrix);
                            postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
                            isAutoScale = true;
                        } else {
//                            mScaleMatrix.postScale(mInitScale / getScale(), mInitScale / getScale(), x, y);
//                            setImageMatrix(mScaleMatrix);
                            postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
                            isAutoScale = true;
                        }
                        return true;
                    }
                });
    }

    public ZoomImageView(Context context) {
        super(context, null);
        mScaleMatrix = new Matrix();
        super.setScaleType(ScaleType.MATRIX);
        mScaleGD = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale){//正在自动缩放时，不再响应双击
                            return true;
                        }
                        float x = e.getX();
                        float y = e.getY();
                        if (getScale() < mMidScale){
//                            mScaleMatrix.postScale(mMidScale / getScale(), mMidScale / getScale(), x, y);
//                            setImageMatrix(mScaleMatrix);
                            postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
                            isAutoScale = true;
                        } else {
//                            mScaleMatrix.postScale(mInitScale / getScale(), mInitScale / getScale(), x, y);
//                            setImageMatrix(mScaleMatrix);
                            postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
                            isAutoScale = true;
                        }
                        return true;
                    }
                });
    }

    /**
     * 自动放大与缩小
     */
    private class AutoScaleRunnable implements  Runnable{
        private float mTargetScale;//缩放的目标值
        private float x; //缩放的中心点
        private float y;
        private final float BIGGER = 1.07f;
        private final float SMALLER = 0.93f;
        private float tempScale;
        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale){
                tempScale = BIGGER;
            }
            if (getScale() > mTargetScale){
                tempScale = SMALLER;
            }
        }

        @Override
        public void run() {
            //进行缩放
            mScaleMatrix.postScale(tempScale, tempScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            float currentScale = getScale();
            if ((tempScale > 1.0f && currentScale < mTargetScale)
                    || (tempScale < 1.0f && currentScale > mTargetScale)){
                postDelayed(this, 16);//每16毫秒执行一次此方法
            } else {//设置为我们的目标值
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();;
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    //获取ImageView加载完成的图片
    @Override
    public void onGlobalLayout() {
        if (!mOnce) {
            //得到空间的宽和高
            int width = getWidth();
            int height = getHeight();
            //得到我们的图片，以及宽和高
            Drawable drawable = getDrawable();
            if (drawable == null)
                return;
            int dWidth = drawable.getIntrinsicWidth();
            int dHeight = drawable.getIntrinsicHeight();

            float scale = 1.0f;//默认放大倍数
            //图片很宽，但高度很小
            if (dWidth > width && dHeight < height) {
                scale = width * 1.0f / dWidth;
            }
            //图片很高，但是宽度很小
            if (dHeight > height && dWidth < width) {
                scale = height * 1.0f / dHeight;
            }
            //图片的宽高都超过空间的宽高，或者图片的宽高都小于控件的宽高
            if ((dWidth > width && dHeight > height) || (dWidth < width && dHeight < height)) {
                scale = Math.min(width * 1.0f / dWidth, height * 1.0f / dHeight);
            }

            //得到缩放值
            mInitScale = scale;
            mMidScale = mInitScale * 2;
            mMaxScale = mInitScale * 4;

            //将图片移动至控件的中心
            int dx = getWidth() / 2 - dWidth / 2;
            int dy = getHeight() / 2 - dHeight / 2;
            mScaleMatrix.postTranslate(dx, dy);
            mScaleMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);
            setImageMatrix(mScaleMatrix);

            mOnce = true;
        }
    }

    /**
     * 获取当前图片的缩放值
     *
     * @return
     */
    public float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        //缩放区间：[initScale  maxScale]
        float scale = getScale();//获取当前图片的缩放值
        float scaleFactor = detector.getScaleFactor();//缩放值

        if (getDrawable() == null)
            return true;
        //缩放范围的控制
        if ((scale < mMaxScale && scaleFactor > 1.0f)//想放大
                || (scale > mInitScale && scaleFactor < 1.0f)) {//想缩小
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;//缩至最小
            }
            if (scale * scaleFactor > mMaxScale) {
                scale = mMaxScale / scale;//放至最大
            }

            //缩放
//            mScaleMatrix.postScale(scaleFactor, scaleFactor,
//                    getWidth() / 2, getHeight() / 2);//此处的缩放总是在图片中间
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());//此处的缩放中心是触摸点
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    /**
     * 获得图片放大后的宽高，left, right, top, bottom
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    /**
     * 在缩放的时候进行边界和位置的控制
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();//控件宽高
        int height = getHeight();
        //缩放时进行边界检测，防止出现白边
        if (rect.width() >= width) {
            if (rect.left > 0) {//左边有空隙
                deltaX = -rect.left;
            }
            if (rect.right < width) {//右边有空隙
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {//顶部有空隙
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {//底部有空隙
                deltaY = height - rect.bottom;
            }
        }
        //如果图片的宽或者高，小于控件的宽或者高，让其居中
        if (rect.width() < width) {
            deltaX = width / 2 - rect.right + rect.width() / 2;
        }
        if (rect.height() < height) {
            deltaY = height / 2 - rect.bottom + rect.height() / 2;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        mScaleGD.onTouchEvent(event);
        float x = 0;
        float y = 0;
        int pointCount = event.getPointerCount();//拿到多点触控的数量
        for (int i = 0; i < pointCount; i++) {
            x += event.getX();
            y += event.getY();
        }
        x /= pointCount;
        y /= pointCount;
        if (mLastPointerCount != pointCount){
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointCount;
        RectF rectF = getMatrixRectF();//
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://处理结合viewPager产生的冲突
                if (rectF.width() > getWidth() + 0.01|| rectF.height() >getHeight() + 0.01){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //处理结合viewPager产生的冲突
                if (rectF.width() > getWidth() + 0.01|| rectF.height() >getHeight() + 0.01){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag){//不可移动
                    isCanDrag = isMoveAction(dx, dy);
                }
                if (isCanDrag){//可移动
//                    RectF rectF = getMatrixRectF();
                    if (getDrawable() != null){
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        //如果宽度小于控件宽度，不允许横向移动
                        if (rectF.width() < getWidth()){
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        //如果高度小于控件高度，不允许纵向移动
                        if (rectF.height() < getHeight()){
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }
                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
        }
        return true;
    }

    /**
     * 当移动时进行边界检查
     */
    private void checkBorderWhenTranslate() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();
        int height = getHeight();
        if (rectF.left > 0 && isCheckLeftAndRight){
            deltaX = -rectF.left;
        }
        if (rectF.right < width && isCheckLeftAndRight){
            deltaX = width - rectF.right;
        }
        if (rectF.top > 0 && isCheckTopAndBottom){
            deltaY = -rectF.top;
        }
        if (rectF.bottom < height && isCheckTopAndBottom){
            deltaY = height - rectF.bottom;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 判断是否是move
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;//勾股定理（sqrt开平方）
    }
}
