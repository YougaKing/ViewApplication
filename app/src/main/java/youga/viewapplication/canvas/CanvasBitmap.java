package youga.viewapplication.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import youga.viewapplication.R;

/**
 * Created by YougaKing on 2016/11/24.
 */

public class CanvasBitmap extends View {

    private final Bitmap mBitmap;
    int mWidth, mHeight;

    public CanvasBitmap(Context context) {
        this(context, null);
    }

    public CanvasBitmap(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, new Matrix(), new Paint());
        canvas.drawBitmap(mBitmap, mBitmap.getWidth(), 0, new Paint());

        canvas.translate(mWidth / 2, mHeight / 2);
        Rect rect = new Rect(0, 0, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        RectF rectF = new RectF(0, 0, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        canvas.drawBitmap(mBitmap, rect, rectF, new Paint());

        rect = new Rect(mBitmap.getWidth() / 2, 0, mBitmap.getWidth(), mBitmap.getHeight() / 2);
        rectF = new RectF(mBitmap.getWidth() / 2, 0, mBitmap.getWidth(), mBitmap.getHeight() / 2);
        canvas.drawBitmap(mBitmap, rect, rectF, new Paint());

        rect = new Rect(0, mBitmap.getHeight() / 2, mBitmap.getWidth() / 2, mBitmap.getHeight());
        rectF = new RectF(0, mBitmap.getHeight() / 2, mBitmap.getWidth() / 2, mBitmap.getHeight());
        canvas.drawBitmap(mBitmap, rect, rectF, new Paint());


    }
}
