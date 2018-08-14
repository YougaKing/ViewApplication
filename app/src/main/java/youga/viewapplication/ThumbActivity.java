package youga.viewapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

import youga.viewapplication.widget.ThumbAnimatorUtil;
import youga.viewapplication.widget.ThumbView;

public class ThumbActivity extends AppCompatActivity {

    boolean mBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumb);

        final ImageView thumbView = (ImageView) findViewById(R.id.thumbView);
        final ThumbWindow thumbWindow = new ThumbWindow(getBaseContext());
        thumbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mBoolean) {
                    thumbView.setImageResource(R.drawable.thumb_light);
                    thumbWindow.show(view);
                } else {
                    thumbView.setImageResource(R.drawable.thumb_normal);
                }
                mBoolean = !mBoolean;
//                ThumbAnimatorUtil.startAnimator(view);
//                thumbView.startThumbAnimator(true);
            }
        });
    }
}
