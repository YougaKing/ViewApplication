package youga.viewapplication.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by YougaKing on 2016/11/24.
 */

public class ClothView extends View {

    Paint mPaint = new Paint();
    private int mWidth, mHeight;

    public ClothView(Context context) {
        this(context, null);
    }

    public ClothView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2f);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        mPaint.setColor(Color.BLACK);
//        canvas.translate(200, 200);
//        canvas.drawCircle(0, 0, 100, mPaint);
//
//        // 在坐标原点绘制一个蓝色圆形
//        mPaint.setColor(Color.BLUE);
//        canvas.translate(200, 200);
//        canvas.drawCircle(0, 0, 100, mPaint);


//        RectF rectF = new RectF(-300, -300, 300, 300);
//        mPaint.setColor(Color.BLACK);           // 绘制黑色矩形
//        canvas.drawRect(rectF, mPaint);
//
//        for (int i = 0; i < 20; i++) {
//            canvas.scale(0.9f, 0.9f);                // 画布缩放
//            mPaint.setStrokeWidth(2f);
//            canvas.drawRect(rectF, mPaint);
//        }


//        canvas.translate(mWidth / 2, mHeight / 2);
//        RectF rectF = new RectF(0, -400, 400, 0);
//        mPaint.setColor(Color.BLACK);           // 绘制黑色矩形
//        canvas.drawRect(rectF, mPaint);
//
//        canvas.rotate(180, 200, 0);
//        mPaint.setColor(Color.BLUE);
//        canvas.drawRect(rectF, mPaint);

        canvas.translate(mWidth / 2, mHeight / 2);
        canvas.drawCircle(0, 0, 400, mPaint);
        canvas.drawCircle(0, 0, 380, mPaint);

        for (int i = 0; i < 36; i++) {
            canvas.drawLine(380, 0, 400, 0, mPaint);
            canvas.rotate(10);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }
}
