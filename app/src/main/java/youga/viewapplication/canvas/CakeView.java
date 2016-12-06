package youga.viewapplication.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import youga.viewapplication.model.PieData;

/**
 * Created by YougaKing on 2016/11/24.
 */

public class CakeView extends View {

    // 颜色表 (注意: 此处定义颜色使用的是ARGB，带Alpha通道的)
    private int[] mColors = {0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
            0xFFE6B800, 0xFF7CFC00};
    // 饼状图初始绘制角度
    private float mStartAngle = 0;
    // 数据
    private List<PieData> mData = new ArrayList<>();
    // 宽高
    private int mWidth, mHeight;
    // 画笔
    private Paint mPaint = new Paint();

    public CakeView(Context context) {
        this(context, null);
    }

    public CakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        initData();
    }

    private void initData() {
        mData.add(new PieData("A", 100));
        mData.add(new PieData("B", 1900));
        mData.add(new PieData("C", 50));
        mData.add(new PieData("D", 800));
        mData.add(new PieData("E", 200));
        mData.add(new PieData("F", 100));
        mData.add(new PieData("G", 450));
        mData.add(new PieData("H", 500));
        mData.add(new PieData("I", 180));

        float count = 0f;
        for (PieData data : mData) {
            count += data.getValue();
        }

        for (PieData data : mData) {
            data.setColor(mColors[mData.indexOf(data)]);
            float per = data.getValue() / count;
            data.setPercentage(per);
            data.setAngle(per * 360);
        }
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

        float currentStartAngle = mStartAngle;                    // 当前起始角度
        canvas.translate(mWidth / 2, mHeight / 2);                // 将画布坐标原点移动到中心位置
        float r = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);  // 饼状图半径

        RectF rect = new RectF(-r, -r, r, r);                     // 饼状图绘制区域

        for (int i = 0; i < mData.size(); i++) {
            PieData pie = mData.get(i);
            mPaint.setColor(pie.getColor());
            canvas.drawArc(rect, currentStartAngle, pie.getAngle(), true, mPaint);
            currentStartAngle += pie.getAngle();
        }
    }
}
