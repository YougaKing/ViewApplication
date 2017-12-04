package youga.imagemarge;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        mTextSticker.setText("轻笑一伪装成国家的矿山可吧，整个国家的GDP对比内蒙古……差距是整整一百倍（笑）党的政策亚克西啊~至于讨不讨厌中国");

        mImageView.getLayoutParams().height = getResources().getDisplayMetrics().heightPixels;
        mImageView.requestLayout();

        mTextSticker.setOnOptionClickListener(new TextSticker.OnOptionClickListener() {
            @Override
            public void onDeleteClick() {

            }

            @Override
            public void onColorClick() {

            }

            @Override
            public void onEditClick() {
                showSoftInput(mTextSticker);
                mEditText.setVisibility(View.VISIBLE);
                mEditText.setFocusable(true);
                mEditText.requestFocus();
            }
        });

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
}
