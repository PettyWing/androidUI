package com.youer.androidui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.youer.androidui.coordinate.CollapsingToolbarLayoutActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onFloatWindow(View view) {
        Uri.Builder uriBuilder = Uri
            .parse("youer://io.github.pettywing/floatwindow")
            .buildUpon();
        uriBuilder.appendQueryParameter("test", "xxx");
        uriBuilder.appendQueryParameter("test2", "yyy");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uriBuilder.build());
        startActivity(intent);
    }

    public void onCoordinatorClick(View view) {
        startActivity(new Intent(this, CollapsingToolbarLayoutActivity.class));
    }
}