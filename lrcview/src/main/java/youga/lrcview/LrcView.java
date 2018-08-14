package youga.lrcview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/4 0004.
 */

public class LrcView extends View {


    private final DisplayMetrics mMetrics;
    private float mLTTextSize = 20F;
    private float mDKTextSize = 16F;
    private int mDKColor = Color.DKGRAY;
    private int mLTColor = Color.LTGRAY;
    private List<String> mStringList;
    private int mCurrentPosition;

    private Paint mLTPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDKPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mWidth, mHeight;

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        mMetrics = getResources().getDisplayMetrics();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LrcView);
        mLTColor = array.getColor(R.styleable.LrcView_LTColor, mLTColor);
        mDKColor = array.getColor(R.styleable.LrcView_DKColor, mDKColor);


        mLTTextSize = array.getDimension(R.styleable.LrcView_LTTextSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mLTTextSize, mMetrics));
        mDKTextSize = array.getDimension(R.styleable.LrcView_DKTextSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mDKTextSize, mMetrics));
        array.recycle();

        mLTPaint.setColor(mLTColor);
        mLTPaint.setTextSize(mLTTextSize);

        mDKPaint.setColor(mDKColor);
        mDKPaint.setTextSize(mDKTextSize);


        mStringList = new ArrayList<>();

        mStringList.add("首先谴责打人者。看描述");
        mStringList.add("这里我想讲一下");
        mStringList.add("日本人通常都是无视你的存在不会看你一眼的");
        mStringList.add("有两个日本人坐在我斜对面看我");

        mStringList.add("他们马上盯着车外面强行假装不是看我");
        mStringList.add("不准别人瞅自己，双重标准。");
        mStringList.add("类似的还有日本人不给");
        mStringList.add("还有不按喇叭哔别人但");
        mStringList.add("所以中国人在治安再");
        mStringList.add("但是，题主所说的右翼？");
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mStringList == null || mStringList.isEmpty()) {
            return;
        }


        drawLrc(canvas);
//        String start = mStringList.get(mCurrentPosition);
//        float v = (currentMillis - start) > 500 ? currentPosition * 80 : lastPosition * 80 + (currentPosition - lastPosition) * 80 * ((currentMillis - start) / 500f);
//        setScrollY((int) v);
//        if (getScrollY() == currentPosition * 80) {
//            lastPosition = currentPosition;
//        }
//        postInvalidateDelayed(100);
    }


    private float mCurrentTextHeight;

    private void drawLrc(Canvas canvas) {
        for (int i = 0; i < mStringList.size(); i++) {
            if (i == mCurrentPosition) {
                mCurrentTextHeight += measureRowHeight(mLTPaint);
                canvas.drawText(mStringList.get(i), 0, mCurrentTextHeight, mLTPaint);
            } else {
                mCurrentTextHeight += measureRowHeight(mDKPaint);
                canvas.drawText(mStringList.get(i), 0, mCurrentTextHeight, mDKPaint);
            }
        }
    }


    public float measureRowHeight(Paint paint) {
        float height = TextMeasure.measureRowHeight(paint);
        return height + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mMetrics);
    }

}
