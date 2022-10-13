package com.youer.androidui.coordinate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback;
import com.youer.androidui.R;
import com.youer.androidui.ScreenTools;
import com.youer.floatwindow.ScreenTool;

import static com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;

/**
 * @author youer
 * @date 2022/6/22
 */
public class CollapsingToolbarLayoutDialogActivity extends AppCompatActivity {

    private static final String TAG = "CoordinatorActivity";
    // 图片的初始高度
    int imageOriginHeight;
    MyBottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_collapsing_toolbar);
        DataBuilder.buildRecycleView(this, findViewById(R.id.recyclerView));
        initAppBarLayout();
        initBottomSheetBehavior();
    }

    private void initAppBarLayout() {
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ImageView imageView = findViewById(R.id.image);
        //// 把锚点放到左上角
        //imageView.setPivotX(0);
        // 把锚点放到居中
        imageView.setPivotX(ScreenTool.getScreenWidth(this) / 2f);
        imageView.setPivotY(0);
        //显示的调用invalidate
        imageView.invalidate();
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
            }
        });
        boolean sticky = false;     // 是否启用吸附效果，即上滑过程中松手会自动收缩为最小高度为止，下滑过程中松手会自动放大为最大高度, true开启，false不开启
        // 设置collapsingToolbarLayout的scrollFlags
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapseLayout);
        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams)collapsingToolbarLayout.getLayoutParams();
        if (sticky) {
            layoutParams.setScrollFlags(SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        } else {
            layoutParams.setScrollFlags(
                SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        }
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
                // verticalOffset == 0表明图片为最大状态，让bottomSheetBehavior来支持拉下
                bottomSheetBehavior.setIntercept(verticalOffset == 0);
            }

        });
    }

    public void initBottomSheetBehavior() {
        View container = findViewById(R.id.container);
        setHalfScreen(container);
        bottomSheetBehavior = (MyBottomSheetBehavior)BottomSheetBehavior.from(container);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    /**
     * 设置为半浮窗
     */
    private void setHalfScreen(View container) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)container
            .getLayoutParams();
        layoutParams.height = getDialogHeight();
        container.setLayoutParams(layoutParams);
    }

    private int getDialogHeight() {
        return (int)(ScreenTools.getScreenHeight(this) * 0.85);
    }
}