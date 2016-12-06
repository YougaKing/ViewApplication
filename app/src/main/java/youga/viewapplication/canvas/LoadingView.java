package youga.viewapplication.canvas;

import android.content.Context;
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
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import youga.viewapplication.R;

/**
 * Created by YougaKing on 2016/11/28.
 */

public class LoadingView extends View {

    private static final String TAG = "LoadingView";
    private Bitmap mLeaf;
    private Paint mBitmapPaint;
    Paint mPaint = new Paint();
    int mWidth, mHeight;
    int background = Color.parseColor("#FDCE4F");
    int orange = Color.parseColor("#FFA800");
    int mProgressHeight, mTotalWidth, mProgressWidth;
    int mProgress, TOTAL_PROGRESS = 100;
    // 叶子飘动一个周期所花的时间
    private static final long LEAF_FLOAT_TIME = 3000;
    // 叶子旋转一周需要的时间
    private static final long LEAF_ROTATE_TIME = 2000;
    // 叶子飘动一个周期所花的时间
    private long mLeafFloatTime = LEAF_FLOAT_TIME;
    // 叶子旋转一周需要的时间
    private long mLeafRotateTime = LEAF_ROTATE_TIME;
    // 用于产生叶子信息
    private LeafFactory mLeafFactory;
    // 产生出的叶子信息
    private List<Leaf> mLeafInfos;
    // 用于控制随机增加的时间不抱团
    private int mAddTime;
    // 中等振幅大小
    private static final int MIDDLE_AMPLITUDE = 13;
    // 不同类型之间的振幅差距
    private static final int AMPLITUDE_DISPARITY = 5;
    // 中等振幅大小
    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;
    // 振幅差
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;
    int mProgressPosition;
    Bitmap mFengShan;


    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setAntiAlias(true);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mLeafFactory = new LeafFactory();
        mLeafInfos = mLeafFactory.generateLeafs();
        mFengShan = BitmapFactory.decodeResource(getResources(), R.drawable.fengshan);
        mLeaf = BitmapFactory.decodeResource(getResources(), R.drawable.leaf);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(background);
        canvas.translate((mWidth - mTotalWidth) / 2, mHeight / 2);

        mPaint.setColor(Color.parseColor("#FCE498"));
        mPaint.setStyle(Paint.Style.FILL);

        RectF rectF = new RectF(0, -(mProgressHeight / 2), mTotalWidth, mProgressHeight / 2);
        canvas.drawRoundRect(rectF, mProgressHeight / 2, mProgressHeight / 2, mPaint);

        int proHeight = (int) (mProgressHeight * 0.8);
        mPaint.setColor(orange);
        mPaint.setStyle(Paint.Style.FILL);
        mProgressPosition = mProgressWidth / TOTAL_PROGRESS * mProgress;


        mPaint.setColor(orange);
        int radius = proHeight / 2;
        float startDiff = (mProgressHeight - proHeight) / 2;
        drawLeafs(canvas);
        if (mProgressPosition <= radius) {//前面圆角
            float side = radius - mProgressPosition;
            float cos = side / radius;
            // 单边角度
            float angle = (float) Math.toDegrees(Math.acos(cos));
            // 起始的位置
            float startAngle = 180 - angle;
            // 扫过的角度
            float sweepAngle = 2 * angle;
            RectF oval = new RectF(startDiff, -radius, proHeight, radius);
            canvas.drawArc(oval, startAngle, sweepAngle, false, mPaint);
        } else {
            RectF oval = new RectF(startDiff, -radius, proHeight, radius);
            canvas.drawArc(oval, 90, 180, false, mPaint);

            rectF = new RectF(radius, -radius, mProgressPosition, radius);
            canvas.drawRect(rectF, mPaint);
        }

        drawEddy(canvas);

        postInvalidate();
    }

    private void drawLeafs(Canvas canvas) {
        mLeafRotateTime = mLeafRotateTime <= 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mLeafInfos.size(); i++) {
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                // 绘制叶子－－根据叶子的类型和当前时间得出叶子的（x，y）
                getLeafLocation(leaf, currentTime);
                // 根据时间计算旋转角度
                canvas.save();
                // 通过Matrix控制叶子旋转
                Matrix matrix = new Matrix();
                float transX = leaf.x;
                float transY = leaf.y;
//                Log.i(TAG, "left.x = " + leaf.x + "--leaf.y=" + leaf.y);
                matrix.postTranslate(transX, transY);
                // 通过时间关联旋转角度，则可以直接通过修改LEAF_ROTATE_TIME调节叶子旋转快慢
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime)
                        / (float) mLeafRotateTime;
                int angle = (int) (rotateFraction * 360);
                // 根据叶子旋转方向确定叶子旋转角度
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : -angle
                        + leaf.rotateAngle;
//                Log.i(TAG, "rotate = " + rotate);
                matrix.postRotate(rotate, transX
                        + mLeaf.getWidth() / 2, transY + mLeaf.getHeight() / 2);
                canvas.drawBitmap(mLeaf, matrix, mBitmapPaint);
                canvas.restore();
            }
        }

    }

    private void getLeafLocation(Leaf leaf, long currentTime) {
        long intervalTime = currentTime - leaf.startTime;
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            leaf.startTime = System.currentTimeMillis() + new Random().nextInt((int) mLeafFloatTime);
        }

        float fraction = (float) intervalTime / mLeafFloatTime;
        leaf.x = (int) (mProgressWidth - mProgressWidth * fraction);
        leaf.y = getLocationY(leaf);
    }

    // 通过叶子信息获取当前叶子的Y值
    private int getLocationY(Leaf leaf) {
        // y = A(wx+Q)+h
        float w = (float) ((float) 2 * Math.PI / mProgressWidth);
        float a = mMiddleAmplitude;
        switch (leaf.type) {
            case LITTLE:
                // 小振幅 ＝ 中等振幅 － 振幅差
                a = mMiddleAmplitude - mAmplitudeDisparity;
                break;
            case MIDDLE:
                a = mMiddleAmplitude;
                break;
            case BIG:
                // 小振幅 ＝ 中等振幅 + 振幅差
                a = mMiddleAmplitude + mAmplitudeDisparity;
                break;
            default:
                break;
        }
//        Log.i(TAG, "---a = " + a + "---w = " + w + "--leaf.x = " + leaf.x);
        return (int) (a * Math.sin(w * leaf.x)) + mProgressHeight / 3;
    }

    float degrees;
    Random mRandom = new Random();

    private void drawEddy(Canvas canvas) {
        mPaint.setColor(orange);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mTotalWidth - mProgressHeight / 2, 0, mProgressHeight / 2, mPaint);


        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(6);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mTotalWidth - mProgressHeight / 2, 0, mProgressHeight / 2 - 3, mPaint);

        for (int i = 0; i < 2; i++) {
            canvas.save();
            canvas.translate(mTotalWidth - mProgressHeight / 2 - mFengShan.getWidth() / 2, -mFengShan.getHeight() / 2);
            Matrix matrix = new Matrix();
            degrees += mRandom.nextInt(10);
            if (degrees > 360) {
                degrees = 0;
            }
            matrix.setRotate(degrees, mFengShan.getWidth() / 2, mFengShan.getHeight() / 2);
            // 设置移动的距离
            canvas.drawBitmap(mFengShan, matrix, mPaint);
            canvas.restore();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mProgressHeight = mHeight / 5;
        mTotalWidth = mWidth / 4 * 3;

        mProgressWidth = (int) (mTotalWidth - (mProgressHeight * 0.2) * 2);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        if (mProgress > TOTAL_PROGRESS) {
            return;
        }
        postInvalidate();
    }


    private enum StartType {
        LITTLE, MIDDLE, BIG
    }

    private class Leaf {
        // 在绘制部分的位置
        float x, y;
        // 控制叶子飘动的幅度
        StartType type;
        // 旋转角度
        int rotateAngle;
        // 旋转方向--0代表顺时针，1代表逆时针
        int rotateDirection;
        // 起始时间(ms)
        long startTime;
    }

    private class LeafFactory {
        private static final int MAX_LEAFS = 8;
        Random random = new Random();

        // 生成一个叶子信息
        public Leaf generateLeaf() {
            Leaf leaf = new Leaf();
            int randomType = random.nextInt(3);
            // 随时类型－ 随机振幅
            StartType type = StartType.MIDDLE;
            switch (randomType) {
                case 0:
                    break;
                case 1:
                    type = StartType.LITTLE;
                    break;
                case 2:
                    type = StartType.BIG;
                    break;
                default:
                    break;
            }
            leaf.type = type;
            // 随机起始的旋转角度
            leaf.rotateAngle = random.nextInt(360);
            // 随机旋转方向（顺时针或逆时针）
            leaf.rotateDirection = random.nextInt(2);
            // 为了产生交错的感觉，让开始的时间有一定的随机性
            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            mAddTime += random.nextInt((int) (mLeafFloatTime * 2));
            leaf.startTime = System.currentTimeMillis() + mAddTime;
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
