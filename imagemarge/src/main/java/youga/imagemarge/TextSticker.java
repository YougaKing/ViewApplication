package youga.imagemarge;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TextSticker extends View {

    public static final int COLOR_VALUES = -100;

    private float paddingLeft = 20f;
    private float paddingRight = 20f;
    private float paddingTop = 10f;
    private float paddingBottom = 10f;

    private TextPaint mPaint;
    private TextPaint mFirstPaint;
    private Paint mBorderPaint;
    private String mText;
    private StaticLayout sl;
    private int mColor = Color.WHITE;
    private int counter = 0;
    private Bitmap mControllerBitmap, mTextColorBitmap, mTextEditBitmap, mTextBitmap;
    private float mControllerWidth, mControllerHeight, mTextColorWidth, mTextColorHeight, mTextEditWidth, mTextEditHeight, mTextBitmapWidth, mTextBitmapHeight;
    private float startX, startY;
    private float[] mOriginPoints, mTextOriginPoints;
    private float[] mPoints, mTextPoints;

    private RectF mOriginContentRect, mContentRect, mViewRect;
    private Matrix mMatrix, mBitmapMatrix, mColorMatrix, mEditMatrix, mControllerMatrix;
    private Path path;
    private PathEffect effects;
    private float px, py;
    private float degrees = 0.0f;
    private float textWidth;
    private boolean isInColor, isInEdit, isInController, isMove;
    private float mLastPointX, mLastPointY;
    private OnTextEditListener mOnTextEditListener;
    private Bitmap mDeleteBitmap;
    private int mDeleteWidth, mDeleteHeight;
    private Matrix mDeleteMatrix;
    private boolean mDelete;
    private boolean mIsInDelete;


    public TextSticker(Context context) {
        this(context, null);
    }

    public TextSticker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextSticker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mFirstPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(dp2px(16));
        mFirstPaint.setTextSize(dp2px(26));
        mPaint.setColor(mColor);
        mFirstPaint.setColor(mColor);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setFilterBitmap(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2.0f);
        mBorderPaint.setColor(Color.WHITE);

        mControllerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sticker_txt_adjust);
        mControllerWidth = mControllerBitmap.getWidth();
        mControllerHeight = mControllerBitmap.getHeight();

        mTextColorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sticker_txt_black);
        mTextColorWidth = mTextColorBitmap.getWidth();
        mTextColorHeight = mTextColorBitmap.getHeight();

        mTextEditBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sticker_txt_edit);
        mTextEditWidth = mTextEditBitmap.getWidth();
        mTextEditHeight = mTextEditBitmap.getHeight();

        mDeleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sticker_img_delete);
        mDeleteWidth = mDeleteBitmap.getWidth();
        mDeleteHeight = mDeleteBitmap.getHeight();

        mTextBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_water_mark_black);
        mTextBitmapWidth = mTextBitmap.getWidth();
        mTextBitmapHeight = mTextBitmap.getHeight();
        paddingLeft += mTextBitmapWidth;
        paddingTop += mTextBitmapHeight;

        // ui要求初始位置 此处因为沉浸状态栏设置，需要Y轴部分需要加上状态栏高度
        startX = dp2px(18);
        startY = dp2px(6) + getInternalDimensionSize(getResources(), "status_bar_height");
        // 设置虚线间隔
        float[] dottedLine = new float[]{16, 8, 16, 8};
        effects = new DashPathEffect(dottedLine, 0);
    }

    public void setTextWidth(float width) {
        if (width != 0) {
            textWidth = width;
        } else {
            textWidth = getMeasuredWidth() * 3 / 5;
        }
    }

    public void updateText(String text, boolean delete) {
        mDelete = delete;
        mText = text;
        if (!TextUtils.isEmpty(text)) {
            // 设置边框的长和高
            if (getTextWidth(mPaint, mText) < textWidth) {
                px = getTextWidth(mPaint, mText);
            } else {
                px = textWidth;
            }

            if (mText.contains("\n")) {
                //　文本中有回车换行
                float height = 0;
                counter = stringNumbers(mText);
                String[] strs = mText.split("\n");
                for (String str : strs) {
                    height += setHeight(str);
                }
                //如果 换行次数 = 截取的字符串数组长度-1 说明没有连续换行或者结尾没有换行... 如果多出来的换行符，则补对应的行高
                height += (counter - (strs.length - 1)) * getTxtHeight(mPaint);
                py = height;
            } else {
                //　文本中没有有回车换行
                setTextHeight(mText);
            }
            // 文本框四点和中心点坐标

            mOriginContentRect = new RectF(0 + paddingLeft, 0 + paddingTop, px + paddingLeft, py + paddingTop);

            float[] points = new float[]{0, 0, px + paddingLeft + paddingRight, 0, px + paddingLeft + paddingRight, py + paddingTop + paddingBottom, 0, py + paddingTop + paddingBottom, (px + paddingLeft + paddingRight) / 2, (py + paddingTop + paddingBottom) / 2};

            mColorMatrix.postTranslate(points[2] - mOriginPoints[2], points[3] - mOriginPoints[3]);

            mControllerMatrix.postTranslate(points[4] - mOriginPoints[4], points[5] - mOriginPoints[5]);

            mEditMatrix.postTranslate(points[6] - mOriginPoints[6], points[7] - mOriginPoints[7]);

            mOriginPoints = points;
        }
        postInvalidate();
    }


    public void setText(String text, int color) {
        setText(text, color, false);
    }

    public void setText(String text, int color, boolean delete) {
        mDelete = delete;
        mText = text;
        // 不为空
        if (!TextUtils.isEmpty(text)) {
            counter = 0;
            // 自定义编辑文本
            if (color != COLOR_VALUES) {
                mColor = color;
            }
            degrees = 0.0f;
            mPaint.setColor(mColor);
            mFirstPaint.setColor(mColor);

            // 设置边框的长和高
            if (getTextWidth(mPaint, mText) < textWidth) {
                px = getTextWidth(mPaint, mText);
            } else {
                px = textWidth;
            }

            if (mText.contains("\n")) {
                //　文本中有回车换行
                float height = 0;
                counter = stringNumbers(mText);
                String[] strs = mText.split("\n");
                for (String str : strs) {
                    height += setHeight(str);
                }
                //如果 换行次数 = 截取的字符串数组长度-1 说明没有连续换行或者结尾没有换行... 如果多出来的换行符，则补对应的行高
                height += (counter - (strs.length - 1)) * getTxtHeight(mPaint);
                py = height;
            } else {
                //　文本中没有有回车换行
                setTextHeight(mText);
            }
            // 文本框四点和中心点坐标
            mOriginPoints = new float[]{0, 0, px + paddingLeft + paddingRight, 0, px + paddingLeft + paddingRight, py + paddingTop + paddingBottom, 0, py + paddingTop + paddingBottom, (px + paddingLeft + paddingRight) / 2, (py + paddingTop + paddingBottom) / 2};
            mPoints = new float[10];
            // 文本矩阵位置
            mOriginContentRect = new RectF(0 + paddingLeft, 0 + paddingTop, px + paddingLeft, py + paddingTop);
            mContentRect = new RectF();
            // 新建专门用于确定文本挪动位置的初始坐标(复用框的初始坐标在旋转过程中会有x,y位置的算法错误)
            mTextOriginPoints = new float[]{0 + paddingLeft, 0 + paddingTop};
            mTextPoints = new float[2];
            // 矩阵平移
            mMatrix = new Matrix();
            mMatrix.postTranslate(startX, startY);
            // bitmap矩阵平移
            mBitmapMatrix = new Matrix();
            mBitmapMatrix.postTranslate(startX + 10, startY + mTextBitmapHeight);

            mDeleteMatrix = new Matrix();
            mDeleteMatrix.postTranslate(mOriginPoints[6] + startX - mDeleteWidth / 2, mOriginPoints[3] + startY - mDeleteWidth / 2);

            mColorMatrix = new Matrix();
            mColorMatrix.postTranslate(mOriginPoints[2] + startX - mTextColorWidth / 2, mOriginPoints[3] + startY - mTextColorHeight / 2);

            mControllerMatrix = new Matrix();
            mControllerMatrix.postTranslate(mOriginPoints[4] + startX - mControllerWidth / 2, mOriginPoints[5] + startY - mControllerHeight / 2);

            mEditMatrix = new Matrix();
            mEditMatrix.postTranslate(mOriginPoints[6] + startX - mTextEditWidth / 2, mOriginPoints[7] + startY - mTextEditHeight / 2);
        }
        postInvalidate();
    }

    public String getText() {
        return mText;
    }

    public int getTextColor() {
        return mColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(mText)) {
            // 非空文本
            mMatrix.mapPoints(mPoints, mOriginPoints);
            mMatrix.mapRect(mContentRect, mOriginContentRect);
            mMatrix.mapPoints(mTextPoints, mTextOriginPoints);
            // 绘制目标图像(边框和三个按钮)
            if (isFocusable()) {
                path = new Path();
                path.moveTo(mPoints[0], mPoints[1]);
                path.lineTo(mPoints[2], mPoints[3]);
                mBorderPaint.setPathEffect(effects);

                canvas.drawPath(path, mBorderPaint);

                path.moveTo(mPoints[2], mPoints[3]);
                path.lineTo(mPoints[4], mPoints[5]);
                mBorderPaint.setPathEffect(effects);
                canvas.drawPath(path, mBorderPaint);

                path.moveTo(mPoints[4], mPoints[5]);
                path.lineTo(mPoints[6], mPoints[7]);
                mBorderPaint.setPathEffect(effects);
                canvas.drawPath(path, mBorderPaint);

                path.moveTo(mPoints[6], mPoints[7]);
                path.lineTo(mPoints[0], mPoints[1]);
                mBorderPaint.setPathEffect(effects);
                canvas.drawPath(path, mBorderPaint);
                if (mDelete) canvas.drawBitmap(mDeleteBitmap, mDeleteMatrix, mBorderPaint);
                canvas.drawBitmap(mTextColorBitmap, mColorMatrix, mBorderPaint);
                canvas.drawBitmap(mControllerBitmap, mControllerMatrix, mBorderPaint);
                canvas.drawBitmap(mTextEditBitmap, mEditMatrix, mBorderPaint);
            }


            // 文本长度大于0
            if (mText.length() > 0) {
                // 文本折行设置
                sl = new StaticLayout(mText, mPaint, getWidth() * 3 / 5, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
                canvas.save();
                canvas.rotate(degrees, mTextPoints[0], mTextPoints[1]);
                canvas.translate(mTextPoints[0], mTextPoints[1]);
                sl.draw(canvas);
                canvas.restore();

                canvas.save();
                canvas.drawBitmap(mTextBitmap, mBitmapMatrix, mBorderPaint);
                canvas.restore();
            }

        }
    }

    /**
     * 获取文本框范围
     *
     * @return RectF
     */
    public RectF getRectF() {
        return mContentRect;
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        postInvalidate();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!isFocusable()) {
            return super.dispatchTouchEvent(event);
        }
        if (mViewRect == null) {
            mViewRect = new RectF(0, 0, textWidth * 2, getMeasuredHeight());
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mDelete && isInDelete(x, y)) {
                    mIsInDelete = true;
                    break;
                }
                if (isInColor(x, y)) {
                    isInColor = true;
                    break;
                }
                if (isInEdit(x, y)) {
                    isInEdit = true;
                    break;
                }
                if (isInController(x, y)) {
                    isInController = true;
                    mLastPointX = x;
                    mLastPointY = y;
                    break;
                }
                if (mContentRect.contains(x, y)) {
                    isMove = true;
                    mLastPointX = x;
                    mLastPointY = y;
                    break;
                }
                setFocusable(false);
                break;
            case MotionEvent.ACTION_UP:
                if (isInColor(x, y) && isInColor) changeTextColor();
                if (isInEdit(x, y) && isInEdit) changeText();
                if (mDelete && isInDelete(x, y) && mIsInDelete) deleteText();
                isMove = false;
                isInController = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isInController) {
                    // 旋转操作
                    mMatrix.postRotate(rotation(event), mPoints[8], mPoints[9]);
                    mBitmapMatrix.postRotate(rotation(event), mPoints[8], mPoints[9]);
                    if (mDelete) mDeleteMatrix.postRotate(rotation(event), mPoints[8], mPoints[9]);
                    mColorMatrix.postRotate(rotation(event), mPoints[8], mPoints[9]);
                    mControllerMatrix.postRotate(rotation(event), mPoints[8], mPoints[9]);
                    mEditMatrix.postRotate(rotation(event), mPoints[8], mPoints[9]);
                    degrees += rotation(event);
                    invalidate();
                    mLastPointX = x;
                    mLastPointY = y;
                    break;
                }
                if (isMove) {
                    // 拖动操作
                    float cX = x - mLastPointX;
                    float cY = y - mLastPointY;
                    isInController = false;
                    if (Math.sqrt(cX * cX + cY * cY) > 2.0f && canStickerMove(cX, cY)) {
                        mMatrix.postTranslate(cX, cY);
                        if (mDelete) mDeleteMatrix.postTranslate(cX, cY);
                        mBitmapMatrix.postTranslate(cX, cY);
                        mColorMatrix.postTranslate(cX, cY);
                        mControllerMatrix.postTranslate(cX, cY);
                        mEditMatrix.postTranslate(cX, cY);
                        postInvalidate();
                        mLastPointX = x;
                        mLastPointY = y;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mLastPointX = 0;
                mLastPointY = 0;
                mIsInDelete = false;
                isInController = false;
                isInColor = false;
                isInEdit = false;
                isMove = false;
                break;
        }
        return true;
    }

    private void deleteText() {
        setText(null, mColor, true);
    }

    private boolean isInDelete(float x, float y) {
        float rx = mPoints[6];
        float ry = mPoints[3];
        RectF rectF = new RectF(rx - mDeleteWidth / 2, ry - mDeleteHeight / 2, rx + mDeleteWidth / 2, ry + mDeleteHeight / 2);
        return rectF.contains(x, y);
    }

    /**
     * 点击区域是否在字色图标区域
     *
     * @param x x坐标
     * @param y y坐标
     * @return true(包含) false(不包含)
     */
    private boolean isInColor(float x, float y) {
        float rx = mPoints[2];
        float ry = mPoints[3];
        RectF rectF = new RectF(rx - mTextColorWidth / 2, ry - mTextColorHeight / 2, rx + mTextColorWidth / 2, ry + mTextColorHeight / 2);
        return rectF.contains(x, y);
    }

    /**
     * 点击区域是否在自定义编辑文字图标区域
     *
     * @param x x坐标
     * @param y y坐标
     * @return true(包含) false(不包含)
     */
    private boolean isInEdit(float x, float y) {
        float rx = mPoints[6];
        float ry = mPoints[7];
        RectF rectF = new RectF(rx - mTextEditWidth / 2, ry - mTextEditHeight / 2, rx + mTextEditHeight / 2, ry + mTextEditHeight / 2);
        return rectF.contains(x, y);
    }

    /**
     * 点击区域是否在缩放旋转图标区域(因为点击区域有点小,宽高各增加10px)
     *
     * @param x x坐标
     * @param y y坐标
     * @return true(包含) false(不包含)
     */
    private boolean isInController(float x, float y) {
        float rx = mPoints[4];
        float ry = mPoints[5];
        RectF rectF = new RectF(rx - mControllerWidth / 2 - 5, ry - mControllerHeight / 2 - 5, rx + mControllerWidth / 2 + 5, ry + mControllerHeight / 2 + 5);
        return rectF.contains(x, y);
    }

    /**
     * 拖动判断
     *
     * @param cx x距离
     * @param cy y距离
     * @return 是否包含
     */
    private boolean canStickerMove(float cx, float cy) {
        float px = cx + mPoints[8];
        float py = cy + mPoints[9];
        return mViewRect.contains(px, py);
    }

    private float rotation(MotionEvent event) {
        float originDegree = calculateDegree(mLastPointX, mLastPointY);
        float nowDegree = calculateDegree(event.getX(), event.getY());
        return nowDegree - originDegree;
    }


    private float calculateDegree(float x, float y) {
        double delta_x = x - mPoints[8];
        double delta_y = y - mPoints[9];
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 更换字体颜色
     */
    private void changeTextColor() {
        isInColor = false;
        if (mPaint.getColor() == getResources().getColor(R.color.cn_textview_theme_color)) {
            mTextBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_water_mark_white);
            mTextColorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sticker_txt_black);
            mPaint.setColor(Color.WHITE);
            mFirstPaint.setColor(Color.WHITE);
            mColor = Color.WHITE;
        } else {
            mTextBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_water_mark_black);
            mTextColorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sticker_txt_white);
            mPaint.setColor(getResources().getColor(R.color.cn_textview_theme_color));
            mFirstPaint.setColor(getResources().getColor(R.color.cn_textview_theme_color));
            mColor = getResources().getColor(R.color.cn_textview_theme_color);
        }
        mTextColorWidth = mTextColorBitmap.getWidth();
        mTextColorHeight = mTextColorBitmap.getHeight();
        postInvalidate();
    }

    /**
     * 自定义编辑文本
     */
    private void changeText() {
        if (mOnTextEditListener != null) {
            mOnTextEditListener.onEdit(mText);
        }
    }


    public float dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    public float getTxtHeight(Paint mPaint) {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        return Float.parseFloat(String.valueOf(Math.ceil(fm.descent - fm.ascent)));
    }

    public float getTextWidth(TextPaint paint, String str) {
        return paint.measureText(str) + 20;
    }

    /**
     * 其实主要是为了获取状态栏的高度
     *
     * @param res 资源id
     * @param key name
     */
    private int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public interface OnTextEditListener {
        void onEdit(String str);
    }

    public void setOnTextEditListener(OnTextEditListener listener) {
        mOnTextEditListener = listener;
    }


    private int stringNumbers(String str) {
        if (str.contains("\n")) {
            counter++;
            stringNumbers(str.substring(str.indexOf("\n") + 1));
        }
        return counter;
    }

    private float setTextHeight(String mText) {
        if (getTextWidth(mPaint, mText) < textWidth) {
            py = getTxtHeight(mPaint);
        } else if (getTextWidth(mPaint, mText) % textWidth == 0) {
            py = (int) (getTextWidth(mPaint, mText) / textWidth) * getTxtHeight(mPaint);
        } else {
            py = (int) (getTextWidth(mPaint, mText) / textWidth) * getTxtHeight(mPaint) + getTxtHeight(mPaint);
        }
        return py;
    }

    private float setHeight(String mText) {
        if (getTextWidth(mPaint, mText) < textWidth) {
            py = getTxtHeight(mPaint);
        } else if (getTextWidth(mPaint, mText) % textWidth == 0) {
            py = (int) (getTextWidth(mPaint, mText) / textWidth) * getTxtHeight(mPaint);
        } else {
            py = (int) (getTextWidth(mPaint, mText) / textWidth) * getTxtHeight(mPaint) + getTxtHeight(mPaint);
        }
        return py;
    }
}
