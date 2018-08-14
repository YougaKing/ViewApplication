package youga.imagemarge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import youga.imagemarge.util.TextMeasure;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/08/14 10:49
 * description: 文字贴纸
 */
public class TextStickerView extends View {

    private static final String TAG = "TextSticker";

    private int mMarginTop = 10, mMarginLeft = 10;
    @ColorInt
    private int mTextColor = Color.WHITE;
    private int mTextSize = 14;
    private TextPaint mTextPaint = new TextPaint(ANTI_ALIAS_FLAG);
    private Paint mBorderPaint = new Paint(ANTI_ALIAS_FLAG);
    private StaticLayout mTextLayout;
    private int mTextMaxWidth;
    private Bitmap mDeleteBitmap, mQuotesBitmap, mColorBitmap, mControlBitmap, mEditBitmap;
    private Matrix mMatrix = new Matrix(), mDeleteMatrix = new Matrix(), mQuotesMatrix = new Matrix(), mColorMatrix = new Matrix(), mControlMatrix = new Matrix(), mEditMatrix = new Matrix();
    private RectF mDeleteRectF = new RectF(), mColorRectF = new RectF(), mControlRectF = new RectF(), mEditRectF = new RectF();
    private float mLastPointX, mLastPointY;
    private RectF mEffectOriginRectF = new RectF(), mEffectRectF = new RectF();
    private RectF mMoveRectF = new RectF();
    private boolean mMove;
    private boolean mDeleteClick, mColorClick, mControl, mEditClick;
    private OnOptionClickListener mListener;
    private float mDegrees;
    private PointF mCenterPoint = new PointF();

    public TextStickerView(Context context) {
        this(context, null);
    }

    public TextStickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextStickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mMarginTop = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, mMarginTop, metrics);
        mMarginLeft = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, mMarginLeft, metrics);

        mTextSize = (int) TypedValue.applyDimension(COMPLEX_UNIT_SP, mTextSize, metrics);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mBorderPaint.setColor(Color.WHITE);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(TypedValue.applyDimension(COMPLEX_UNIT_DIP, 1, metrics));

        float[] dottedLine = new float[]{16, 8, 16, 8};
        DashPathEffect pathEffect = new DashPathEffect(dottedLine, 0);
        mBorderPaint.setPathEffect(pathEffect);

        mDeleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker_delete);
        mQuotesBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker_quotes_white);
        mColorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker_color);
        mControlBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker_control);
        mEditBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker_edit);
        setFocusable(false);
    }


    /**
     * @param width  图片的宽度
     * @param height 图片的高度
     */
    public void setDrawRange(int width, int height) {
        mMoveRectF.set(0, 0, width, height);
        mTextMaxWidth = (int) (width * 3F / 5F);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        requestLayout();
    }


    public void setText(final String text) {
        mMatrix.reset();
        mDeleteMatrix.reset();
        mQuotesMatrix.reset();
        mColorMatrix.reset();
        mControlMatrix.reset();
        mEditMatrix.reset();
        if (TextUtils.isEmpty(text)) {
            mTextLayout = null;
            invalidate();
            return;
        }
        RectF textRect = TextMeasure.measureText(mTextPaint, text, mTextMaxWidth);
        mTextLayout = new StaticLayout(text, mTextPaint, (int) textRect.right, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, true);


        float left = mMarginLeft + mDeleteBitmap.getWidth() * 0.5f;
        float top = mMarginTop + mDeleteBitmap.getHeight() * 0.5f;
        mEffectOriginRectF.set(left, top,
                left + mQuotesBitmap.getWidth() + textRect.right,
                top + mQuotesBitmap.getHeight() * 0.25f + textRect.bottom
        );

        mDeleteMatrix.postTranslate(mMarginLeft, mMarginTop);
        mQuotesMatrix.postTranslate(mEffectOriginRectF.left, mEffectOriginRectF.top);
        mColorMatrix.postTranslate(mEffectOriginRectF.right - mColorBitmap.getWidth() * 0.5f, mEffectOriginRectF.top - mColorBitmap.getHeight() * 0.5f);
        mControlMatrix.postTranslate(mEffectOriginRectF.right - mControlBitmap.getWidth() * 0.5f, mEffectOriginRectF.bottom - mControlBitmap.getHeight() * 0.5f);
        mEditMatrix.postTranslate(mEffectOriginRectF.left - mEditBitmap.getWidth() * 0.5f, mEffectOriginRectF.bottom - mEditBitmap.getHeight() * 0.5f);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mTextLayout == null) return;

        mMatrix.mapRect(mEffectRectF, mEffectOriginRectF);

        if (isFocusable()) {
            canvas.drawRect(mEffectRectF, mBorderPaint);
            canvas.drawBitmap(mDeleteBitmap, mDeleteMatrix, null);
            canvas.drawBitmap(mColorBitmap, mColorMatrix, null);
            canvas.drawBitmap(mControlBitmap, mControlMatrix, null);
            canvas.drawBitmap(mEditBitmap, mEditMatrix, null);
        }

        canvas.save();
        PointF pointF = calculateCenter();
        canvas.rotate(mDegrees, pointF.x, pointF.y);
        canvas.translate(mEffectRectF.left + mQuotesBitmap.getWidth(), mEffectRectF.top + mQuotesBitmap.getHeight() * 0.25f);
        mTextLayout.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.drawBitmap(mQuotesBitmap, mQuotesMatrix, null);
        canvas.restore();
    }


    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        postInvalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isDeleteContains(x, y)) {
                    mDeleteClick = true;
                    break;
                }
                if (isColorContains(x, y)) {
                    mColorClick = true;
                    break;
                }
                if (isControlContains(x, y)) {
                    mControl = true;
                    break;
                }
                if (isEditContains(x, y)) {
                    mEditClick = true;
                    break;
                }
                if (mEffectRectF.contains(x, y)) {
                    if (!isFocusable()) setFocusable(true);
                    mMove = true;
                    mLastPointX = x;
                    mLastPointY = y;
                } else {
                    if (isFocusable()) setFocusable(false);
                    return super.dispatchTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isDeleteContains(x, y) && mDeleteClick) onDeleteClick();
                if (isColorContains(x, y) && mColorClick) onColorClick();
                if (isEditContains(x, y) && mEditClick) onEditClick();
                mControl = mMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMove) {
                    float moveX = x - mLastPointX;
                    float moveY = y - mLastPointY;
                    if (Math.sqrt(moveX * moveX + moveY * moveY) > 2.0f && canMove(moveX, moveY)) {
                        mMatrix.postTranslate(moveX, moveY);
                        mDeleteMatrix.postTranslate(moveX, moveY);
                        mQuotesMatrix.postTranslate(moveX, moveY);
                        mColorMatrix.postTranslate(moveX, moveY);
                        mControlMatrix.postTranslate(moveX, moveY);
                        mEditMatrix.postTranslate(moveX, moveY);
                        postInvalidate();
                        mLastPointX = x;
                        mLastPointY = y;
                    }
                }

                if (mControl) {
                    PointF pointF = calculateCenter();
                    float centerX = pointF.x;
                    float centerY = pointF.y;
                    float degrees = rotation(event);
                    mMatrix.postRotate(degrees, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
                    mQuotesMatrix.postRotate(degrees, centerX, centerY);
                    mDeleteMatrix.postRotate(degrees, centerX, centerY);
                    mColorMatrix.postRotate(degrees, centerX, centerY);
                    mControlMatrix.postRotate(degrees, centerX, centerY);
                    mEditMatrix.postRotate(degrees, centerX, centerY);
                    mDegrees += degrees;
                    postInvalidate();
                    mLastPointX = x;
                    mLastPointY = y;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mDeleteClick = mColorClick = mEditClick = false;
                mLastPointX = mLastPointY = 0;
                mControl = mMove = false;
                break;
        }
        return true;
    }

    private void onDeleteClick() {
        setText(null);
    }

    private void onEditClick() {
        if (mListener != null) mListener.onEditClick();
    }

    private void onColorClick() {
        if (mListener != null) mListener.onColorClick();
        if (mTextColor == Color.WHITE) {
            mTextColor = Color.BLACK;
            mQuotesBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker_quotes_black);
        } else {
            mQuotesBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker_quotes_white);
            mTextColor = Color.WHITE;
        }
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    private boolean isDeleteContains(float x, float y) {
        mDeleteRectF.set(mEffectRectF.left - mDeleteBitmap.getWidth() * 0.5f,
                mEffectRectF.top - mDeleteBitmap.getHeight() * 0.5f,
                mEffectRectF.left + mDeleteBitmap.getWidth() * 0.5f,
                mEffectRectF.top + mDeleteBitmap.getHeight() * 0.5f);
        return mDeleteRectF.contains(x, y);
    }

    private boolean isColorContains(float x, float y) {
        mColorRectF.set(mEffectRectF.right - mColorBitmap.getWidth() * 0.5f,
                mEffectRectF.top - mColorBitmap.getHeight() * 0.5f,
                mEffectRectF.right + mColorBitmap.getWidth() * 0.5f,
                mEffectRectF.top + mColorBitmap.getHeight() * 0.5f);
        return mColorRectF.contains(x, y);
    }

    private boolean isControlContains(float x, float y) {
        mControlRectF.set(mEffectRectF.right - mControlBitmap.getWidth() * 0.5f,
                mEffectRectF.bottom - mControlBitmap.getHeight() * 0.5f,
                mEffectRectF.right + mControlBitmap.getWidth() * 0.5f,
                mEffectRectF.bottom + mControlBitmap.getHeight() * 0.5f);
        return mControlRectF.contains(x, y);
    }

    private boolean isEditContains(float x, float y) {
        mEditRectF.set(mEffectRectF.left - mEditBitmap.getWidth() * 0.5f,
                mEffectRectF.bottom - mEditBitmap.getHeight() * 0.5f,
                mEffectRectF.left + mEditBitmap.getWidth() * 0.5f,
                mEffectRectF.bottom + mEditBitmap.getHeight() * 0.5f);
        return mEditRectF.contains(x, y);
    }

    private boolean canMove(float cx, float cy) {
        float resultX, resultY;
        if (cx > 0) {
            resultX = mEffectRectF.right + cx;
        } else {
            resultX = mEffectRectF.left + cx;
        }
        if (cy > 0) {
            resultY = mEffectRectF.bottom + cy;
        } else {
            resultY = mEffectRectF.top + cy;
        }
        return mMoveRectF.contains(resultX, resultY);
    }

    private float rotation(MotionEvent event) {
        float originDegree = calculateDegree(mLastPointX, mLastPointY);
        float nowDegree = calculateDegree(event.getX(), event.getY());
        return nowDegree - originDegree;
    }

    private float calculateDegree(float x, float y) {
        PointF pointF = calculateCenter();
        double diffX = x - pointF.x;
        double diffY = y - pointF.y;
        double radians = Math.atan2(diffY, diffX);
        return (float) Math.toDegrees(radians);
    }

    private PointF calculateCenter() {
        Log.i(TAG, mEffectRectF.toString());
        float centerX = mEffectRectF.centerX();
        float centerY = mEffectRectF.centerY();
        mCenterPoint.set(centerX, centerY);
        Log.i(TAG, mCenterPoint.toString());
        return mCenterPoint;
    }

    public void setOnOptionClickListener(OnOptionClickListener listener) {
        mListener = listener;
    }

    public interface OnOptionClickListener {
        void onColorClick();

        void onEditClick();
    }
}
