package youga.viewapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Random;

import youga.viewapplication.canvas.LeafView;
import youga.viewapplication.canvas.LoadingView;

public class CanvasActivity extends AppCompatActivity {

    private static final int REFRESH_PROGRESS = 222;
    CharSequence mCharSequence;
    String mString;
    StringBuffer mBuffer;
    StringBuilder mBuilder;
    LoadingView mLoadingView;
    LeafView mLeafView;
    int mProgress;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_PROGRESS:
                    if (mProgress < 40) {
                        mProgress += 10;
                        // 随机800ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                new Random().nextInt(800));
                        mLoadingView.setProgress(mProgress);
                        mLeafView.setProgress(mProgress);
                    } else {
                        mProgress += 10;
                        // 随机1200ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                new Random().nextInt(1200));
                        mLoadingView.setProgress(mProgress);
                        mLeafView.setProgress(mProgress);
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mLeafView = (LeafView) findViewById(R.id.leafView);
        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 3000);

    }
}
