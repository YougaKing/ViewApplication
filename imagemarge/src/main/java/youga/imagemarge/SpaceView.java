package youga.imagemarge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/12/1 0001.
 */

public class SpaceView extends View {


    private RectF rectF = new RectF();
    private Paint paint = new Paint();

    public SpaceView(Context context) {
        this(context, null);
    }

    public SpaceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rectF.set(0, 0, 200, 400);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.RED);
        canvas.translate(200, 200);
        canvas.drawRect(rectF, paint);

        paint.setColor(Color.GREEN);
        canvas.rotate(90, rectF.centerX(), rectF.centerY());
        canvas.drawRect(rectF, paint);

    }
}
