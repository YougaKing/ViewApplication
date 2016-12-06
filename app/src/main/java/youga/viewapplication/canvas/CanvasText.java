package youga.viewapplication.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by YougaKing on 2016/11/28.
 */

public class CanvasText extends View {

    Paint mPaint = new Paint();

    public CanvasText(Context context) {
        this(context, null);
    }

    public CanvasText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(24);
        mPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String abc = "ABCDEFG";
        canvas.drawText(abc, 20, 50, mPaint);
        canvas.drawText(abc, 2, 6, 20, 100, mPaint);
        char[] chars = abc.toCharArray();
        canvas.drawText(chars, 1, 4, 20, 150, mPaint);


        canvas.drawPosText(abc, new float[]{50, 50, 100, 100, 150, 150, 200, 200,
                250, 250, 300, 300, 350, 350}, mPaint);

    }
}
