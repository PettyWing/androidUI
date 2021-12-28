package com.youer.androidui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.youer.ui.FloatWindow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openFloatWindow();
    }

    public void openFloatWindow() {
        LinearLayout floatView = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.float_view, null);

        FloatWindow floatWindow = new FloatWindow.Builder(this, floatView)
            .build();
        floatWindow.show();

    }
}