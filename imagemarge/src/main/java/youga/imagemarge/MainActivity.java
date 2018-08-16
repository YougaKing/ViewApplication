package youga.imagemarge;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.textSticker)
    TextSticker mTextSticker;
    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.editText)
    EditText mEditText;
    @BindView(R.id.rootView)
    LinearLayout mRootView;
    @BindView(R.id.fl_container)
    FrameLayout mFlContainer;
    int mHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        final String text = "轻笑一伪装成国家的矿山可吧，整个国家的GDP对比内蒙古……差距是整整一百倍（笑）党的政策亚克西啊~至于讨不讨厌中国";

        mImageView.getLayoutParams().height = getResources().getDisplayMetrics().heightPixels;
        mImageView.requestLayout();

        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mHeight != 0) return;
                mHeight = mRootView.getHeight();
                DisplayMetrics metrics = getResources().getDisplayMetrics();

                ViewGroup.LayoutParams params = mFlContainer.getLayoutParams();
                params.height = (int) (mHeight - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, metrics));
                params.width = metrics.widthPixels;
                mFlContainer.requestLayout();

                BitmapFactory.Options opt = decodeFileOptions(R.drawable.log);
                float outWidth = opt.outWidth;
                float outHeight = opt.outHeight;
                float scaleW = params.width / outWidth;
                float scaleH = params.height / outHeight;

                float scale = Math.min(scaleW, scaleH);
                int width = (int) (outWidth * scale);
                int height = (int) (outHeight * scale);

                params = mImageView.getLayoutParams();
                params.width = width;
                params.height = height;
                mImageView.requestLayout();

                mImageView.setImageResource(R.drawable.log);
//                mTextSticker.setDrawRange(width, height);
                mTextSticker.setText(text);
            }
        });

//        mTextSticker.setOnOptionClickListener(new TextStickerView.OnOptionClickListener() {
//            @Override
//            public void onColorClick() {
//
//            }
//
//            @Override
//            public void onEditClick() {
//                showSoftInput(mTextSticker);
//                mEditText.setVisibility(View.VISIBLE);
//                mEditText.setFocusable(true);
//                mEditText.requestFocus();
//            }
//        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mTextSticker.setText(v.getText().toString());
                mEditText.setVisibility(View.GONE);
                hideSoftInput(mImageView);
                return false;
            }
        });
    }


    private void showSoftInput(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        if (imm.isActive()) {
            imm.showSoftInputFromInputMethod(v.getApplicationWindowToken(), 0);
        }
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void hideSoftInput(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    public BitmapFactory.Options decodeFileOptions(int path) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        opt.inSampleSize = 1;
        BitmapFactory.decodeResource(getResources(), path, opt);
        return opt;
    }
}
