package youga.viewapplication.canvas;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import youga.viewapplication.R;

/**
 * Created by YougaKing on 2016/11/29.
 */

public class LeafView extends View {

    private static final String TAG = "LeafView";
    static final int TOTAL_PROGRESS = 100;
    Paint mPaint = new Paint();
    Paint mBitmapPaint = new Paint();
    int mWidth, mHeight;
    float mOutRadius, mProgressRadius;
    Bitmap mFanBitmap, mLeafBitmap;
    int mOuterColor = 0xFFFCE498;
    int mOrange = 0xFFFFA800;
    int mProgress;
    int mDegrees;
    Random mRandom = new Random();
    List<Leaf> mLeafs;
    // 叶子飘动一个周期所花的时间
    private static final long LEAF_FLOAT_TIME = 3000;
    // 叶子飘动一个周期所花的时间
    private long mLeafFloatTime = LEAF_FLOAT_TIME;

    public LeafView(Context context) {
        super(context);
    }

    public LeafView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setAntiAlias(true);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mFanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fengshan);
        mLeafBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.leaf);

        mLeafs = new LeafFactory().generateLeafs();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(0, mOutRadius);

        canvasBackground(canvas);

        canvasLeaf(canvas);

        canvasProgress(canvas);

        canvasFan(canvas);

        postInvalidate();
    }

    private void canvasLeaf(Canvas canvas) {

        long currentTime = System.currentTimeMillis();
        for (Leaf leaf : mLeafs) {
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                // 绘制叶子－－根据叶子的类型和当前时间得出叶子的（x，y）
                getLeafLocation(leaf, currentTime);

            }

        }

    }

    private void getLeafLocation(Leaf leaf, long currentTime) {
        long intervalTime = currentTime - leaf.startTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            leaf.startTime = System.currentTimeMillis() + mRandom.nextInt((int) mLeafFloatTime);
        }
//
//        float fraction = (float) intervalTime / mLeafFloatTime;
//        leaf.x = (int) (mProgressWidth - mProgressWidth * fraction);
//        leaf.y = getLocationY(leaf);
    }

    private void canvasProgress(Canvas canvas) {
        mPaint.setColor(mOrange);
        mPaint.setStyle(Paint.Style.FILL);
        float startDiff = mOutRadius - mProgressRadius;
        float progressWidth = mProgress * (mWidth - mOutRadius - startDiff) / TOTAL_PROGRESS;
        RectF rectF = new RectF(startDiff, -mProgressRadius, startDiff + mProgressRadius * 2, mProgressRadius);
        if (progressWidth > mProgressRadius) {
            canvas.drawArc(rectF, 90, 180, false, mPaint);
            canvas.drawRect(new RectF(mOutRadius, -mProgressRadius, progressWidth, mProgressRadius), mPaint);
        } else {
            float angle = (float) Math.toDegrees(Math.acos((mProgressRadius - progressWidth) / mProgressRadius));
            float startAngle = 180 - angle;
            float sweepAngle = angle * 2;
            canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaint);
        }
    }

    private void canvasFan(Canvas canvas) {
        float strokeWidth = (float) (mOutRadius * 0.1);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(mWidth - mOutRadius, 0, mOutRadius - strokeWidth / 2, mPaint);

        if (mProgress == TOTAL_PROGRESS) {
            canvas.save();
            mPaint.setColor(Color.WHITE);
            Rect bounds = new Rect();
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(1);
            mPaint.getTextBounds("100%", 0, "100%".length(), bounds);
            mPaint.setTextSize((float) (mProgressRadius * 0.8));
            Matrix matrix = new Matrix();
            matrix.setTranslate(mWidth - mOutRadius - bounds.width() / 2, mOutRadius + bounds.height() / 2);
//                matrix.preRotate(i, bounds.width() / 2, bounds.height() / 2);
            canvas.setMatrix(matrix);
            canvas.drawText("100%", 0, 0, mPaint);
            canvas.restore();
        } else {
            for (int i = 0; i < 2; i++) {
                canvas.save();
                Matrix matrix = new Matrix();
                float height = (float) (mHeight * 0.8);
                float width = mFanBitmap.getWidth() * height / mFanBitmap.getHeight();
                if (mFanBitmap.getHeight() > height) {
                    matrix.setScale(width / mFanBitmap.getWidth(), height / mFanBitmap.getHeight());
                } else {
                    height = mFanBitmap.getHeight();
                    width = mFanBitmap.getWidth();
                }
                canvas.translate(mWidth - mOutRadius - width / 2, -height / 2);
                mDegrees += mRandom.nextInt(10);
                if (mDegrees > 360) mDegrees = mRandom.nextInt(10);
                matrix.preRotate(mDegrees, mFanBitmap.getWidth() / 2, mFanBitmap.getHeight() / 2);
                canvas.drawBitmap(mFanBitmap, matrix, mBitmapPaint);
                canvas.restore();
            }
        }
    }

    private void canvasBackground(Canvas canvas) {
        mPaint.setColor(mOuterColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(0, -mOutRadius, mWidth, mOutRadius), mOutRadius, mOutRadius, mPaint);

        mPaint.setColor(mOrange);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mWidth - mOutRadius, 0, mOutRadius, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mOutRadius = mHeight / 2;
        mProgressRadius = (float) (mOutRadius * 0.8);
    }

    public void setProgress(int progress) {
        mProgress = progress > TOTAL_PROGRESS ? TOTAL_PROGRESS : progress;
        if (progress > TOTAL_PROGRESS) return;
        postInvalidate();
    }

    private class Leaf {
        float x, y;
        int rotateAngle;
        // 起始时间(ms)
        long startTime;
    }

    private class LeafFactory {
        private static final int MAX_LEAFS = 6;
        Random random = new Random();

        // 生成一个叶子信息
        public Leaf generateLeaf() {
            Leaf leaf = new Leaf();
            // 随机起始的旋转角度
            leaf.rotateAngle = random.nextInt(360);
            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            leaf.startTime = System.currentTimeMillis() + mRandom.nextInt((int) (mLeafFloatTime * 2));
            return leaf;
        }

        // 根据最大叶子数产生叶子信息
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        // 根据传入的叶子数量产生叶子信息
        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }

    }

}
