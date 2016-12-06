package youga.viewapplication.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by YougaKing on 2016/11/28.
 */

public class CanvasPath extends View {

    Paint mPaint = new Paint();
    int mWidth, mHeight;

    public CanvasPath(Context context) {
        this(context, null);
    }

    public CanvasPath(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mWidth / 2, mHeight / 2);
//        canvas.scale(1, -1);
        Path path = new Path();
//        path.lineTo(mWidth / 2, 0);
////        path.moveTo(50, 50);
//        path.setLastPoint(100, 100);
//        path.lineTo(0, mHeight / 2);
//        path.close();

        path.addRect(-100, -100, 100, 100, Path.Direction.CCW);
        Path src = new Path();
        src.addCircle(0, 100, 100, Path.Direction.CW);
        path.addPath(src);
        canvas.drawPath(path, mPaint);

        canvas.translate(-(mWidth / 2), -(mHeight / 2));
        canvas.drawLine(0, 0, 200, 200, mPaint);
        RectF oval = new RectF(0, 0, 300, 300);
        path = new Path();
        path.addArc(oval, 0, 270);

        canvas.drawPath(path, mPaint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }
}
