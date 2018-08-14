package youga.viewapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;

import youga.viewapplication.widget.ThumbView;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/07/17 16:18
 * description:
 */
public class ThumbWindow extends PopupWindow {

    private ThumbView mThumbView;

    public ThumbWindow(Context context) {
        super(context);


        View view = LayoutInflater.from(context).inflate(R.layout.window_thumb, null);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mThumbView = (ThumbView) view.findViewById(R.id.thumbView);
        setContentView(view);

    }

    public void show(View anchor) {
        Point image = ThumbView.measureImageSize(anchor.getContext());
        Point layout = ThumbView.measureLayoutSize(image);

        int width = anchor.getWidth();
        int height = anchor.getHeight();

        showAsDropDown(anchor, -(layout.x / 2 - width / 2), -(layout.y + (height - image.y) / 2));
        mThumbView.startThumbAnimator(true);
    }
}
