package youga.viewapplication.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.drawable.PictureDrawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by YougaKing on 2016/11/24.
 */

public class CanvasPicture extends View {

    Picture mPicture = new Picture();
    int mWidth, mHeight;

    public CanvasPicture(Context context) {
        this(context, null);
    }

    public CanvasPicture(Context context, AttributeSet attrs) {
        super(context, attrs);
        recording();
    }

    private void recording() {
        Canvas canvas = mPicture.beginRecording(500, 500);
        // 创建一个画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        // 在Canvas中具体操作
        // 位移
        canvas.translate(250, 250);
        // 绘制一个圆
        canvas.drawCircle(0, 0, 100, paint);

        mPicture.endRecording();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawPicture(mPicture);
//
//        canvas.translate(mWidth / 2, mHeight / 2);
//        RectF rectF = new RectF(0, 0, mPicture.getWidth(), 200);
//        canvas.drawPicture(mPicture, rectF);

        PictureDrawable drawable = new PictureDrawable(mPicture);
        drawable.setBounds(0, 0, 250, drawable.getIntrinsicHeight());
        drawable.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }
}
