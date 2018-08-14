package youga.viewapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private AppDialog mAppDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        ListView listView = (ListView) findViewById(R.id.listView);

        String[] arrays = new String[]{
                "Canvas",
                "ThumbView",
        };

        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrays));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(MainActivity.this, CanvasActivity.class);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, ThumbActivity.class);
                        break;
                }
                if (intent != null) startActivity(intent);
            }
        });

        mAppDialog = new AppDialog(this);
        mAppDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 333 && resultCode == Activity.RESULT_OK) {
            mAppDialog.setEditText(data.getStringExtra("ooo"));
        }
    }
}
