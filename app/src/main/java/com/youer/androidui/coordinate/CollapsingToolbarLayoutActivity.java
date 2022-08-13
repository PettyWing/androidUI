package com.youer.androidui.coordinate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener;
import com.youer.androidui.R;

/**
 * @author youer
 * @date 2022/6/22
 */
public class CollapsingToolbarLayoutActivity extends AppCompatActivity {

    private static final String TAG = "CoordinatorActivity";
    // 图片的初始高度
    int imageOriginHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collapsing_toolbar);
        DataBuilder.buildRecycleView(this, findViewById(R.id.recyclerView));
        initAppBarLayout();
    }

    private void initAppBarLayout() {
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ImageView imageView = findViewById(R.id.image);
        // 把锚点放到左上角
        imageView.setPivotX(0);
        imageView.setPivotY(0);
        //显示的调用invalidate
        imageView.invalidate();
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
            }
        });
        appBarLayout.addOnOffsetChangedListener(new OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // 第一次滑动的时候记录图片的原始高度
                if (imageOriginHeight == 0) {
                    imageOriginHeight = imageView.getMeasuredHeight();

                }
                // 根据滑动的距离缩放图片
                float newHeight = imageOriginHeight + verticalOffset;
                float scale = newHeight / imageOriginHeight;
                ViewCompat.setScaleY(imageView, scale);
                ViewCompat.setScaleX(imageView, scale);
            }

        });
    }

}