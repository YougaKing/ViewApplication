package youga.viewapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by YougaKing on 2016/11/28.
 */

public class AppDialog extends Dialog {


    EditText mEditText;
    Button mButton;

    public AppDialog(final Activity activity) {
        super(activity);

    }


    public void setEditText(String text) {
    }
}
