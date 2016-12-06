package youga.viewapplication.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import youga.viewapplication.R;

/**
 * Created by YougaKing on 2016/11/24.
 */

public class CheckView extends View {

    private static final String TAG = "CheckView";
    Paint mPaint = new Paint();
    private final Bitmap mBitmap;
    int mWidth, mHeight;
    int mCount = 13;
    int mCurrent = -1;
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            return false;
        }
    });

    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.checkmark);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCurrent == -1) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mCurrent < 12) {
                                mCurrent++;
                                invalidate();
                                mHandler.postDelayed(this, 50);
                            }
                        }
                    }, 100);
                } else if (mCurrent == 12) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mCurrent > -1) {
                                mCurrent--;
                                invalidate();
                                mHandler.postDelayed(this, 50);
                            }
                        }
                    }, 50);
                }
            }
        });
    }

    public CheckView(Context context) {
        this(context, null);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawBitmap(mBitmap, 0, 0, null);

        int width = mBitmap.getWidth() / mCount;

        mPaint.setColor(Color.GRAY);
        canvas.translate(mWidth / 2, mHeight / 2);
        canvas.drawCircle(0, 0, (float) (Math.max(width, mBitmap.getHeight()) * 0.8), mPaint);

        //mCurrent = 0, mCount = 13;
        //width 等于图片的宽度除以13
        //所以图片位置是0,0,100,100
        Rect rect = new Rect(mCurrent * width, 0, (mCurrent + 1) * width, mBitmap.getHeight());

        RectF rectF = new RectF(-(width / 2), -(mBitmap.getHeight() / 2), width / 2, mBitmap.getHeight() / 2);


        canvas.drawBitmap(mBitmap, rect, rectF, null);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

}
