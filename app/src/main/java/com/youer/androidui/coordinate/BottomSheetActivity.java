package com.youer.androidui.coordinate;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback;
import com.youer.androidui.R;
import com.youer.androidui.ScreenTools;

/**
 * @author youer
 * @date 2022/8/12
 */
public class BottomSheetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet);
        DataBuilder.buildRecycleView(this, findViewById(R.id.recyclerView));
        initBottomSheet();
    }

    private void initBottomSheet() {
        ImageView imageView = findViewById(R.id.image);
        // 把锚点放到左上角
        imageView.setPivotX(0);
        imageView.setPivotY(0);
        //显示的调用invalidate
        imageView.invalidate();

        float imageMinHeight = getResources().getDimension(R.dimen.image_mini_height);
        float imageMaxHeight = getResources().getDimension(R.dimen.image_max_height);
        int screenHeight = ScreenTools.getScreenHeight(this);

        BottomSheetBehavior behavior = BottomSheetBehavior.from(findViewById(R.id.recyclerView));
        // 设置折叠状态下的高度
        behavior.setPeekHeight((int)(screenHeight - imageMaxHeight));
        // 设置展开状态下的高度
        behavior.setExpandedOffset((int)imageMinHeight + ScreenTools.getStatusBarHeight(this));
        behavior.setFitToContents(false);
        // 监听bottomSheet的滑动
        behavior.addBottomSheetCallback(new BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                ViewCompat.setScaleY(imageView, 1 - slideOffset * 0.5f);
                ViewCompat.setScaleX(imageView, 1 - slideOffset * 0.5f);
            }
        });
    }
}