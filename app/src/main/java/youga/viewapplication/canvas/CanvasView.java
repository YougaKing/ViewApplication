package youga.viewapplication.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by YougaKing on 2016/11/24.
 */

public class CanvasView extends View {

    Paint mPaint = new Paint();

    public CanvasView(Context context) {
        super(context);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint.setColor(Color.BLUE);
        /**
         * STROKE                //描边
         FILL                  //填充
         FILL_AND_STROKE       //描边加填充
         */
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(10F);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.RED);

//        canvas.drawPoint(200, 200, mPaint);
//        canvas.drawPoints(new float[]{500, 500,
//                500, 600,
//                500, 700
//        }, mPaint);

//        canvas.drawLine(0, 20, 500, 300, mPaint);

//        canvas.drawRect(0, 0, 300, 400, mPaint);
//
//        Rect rect = new Rect(0, 500, 300, 600);
//        canvas.drawRect(rect, mPaint);
//
//        RectF rectf = new RectF(0, 700, 300, 1000);
//        canvas.drawRoundRect(rectf, 30, 30, mPaint);


//        RectF rectf = new RectF(200, 100, 400, 400);
//        canvas.drawOval(rectf,mPaint);

        canvas.drawCircle(100, 100, 100, mPaint);

        RectF rectF = new RectF(100, 200, 300, 300);
        canvas.drawArc(rectF, 0, 90, false, mPaint);

        RectF oval = new RectF(100, 400, 300, 500);
        canvas.drawArc(oval, 0, 90, true, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(100, 600, 100, mPaint);
    }
}
