package com.youer.androidui.coordinate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener;
import com.youer.androidui.R;

/**
 * @author youer
 * @date 2022/6/22
 */
public class CollapsingToolbarLayoutActivity extends Activity {
    int id = 0;
    private static final String TAG = "CoordinatorActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter() {
            /**
             * @param parent
             * @param viewType
             * @return
             */
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView textView = new TextView(CollapsingToolbarLayoutActivity.this);
                return new MyViewHolder(textView);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                ((TextView)(holder.itemView)).setText("item" + id);
                id++;
            }

            @Override
            public int getItemCount() {
                return 100;
            }
        });
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
                if (imageOriginHeight == 0) {
                    imageOriginHeight = imageView.getMeasuredHeight();

                }
                float newHeight = imageOriginHeight + verticalOffset;
                float scale = newHeight / imageOriginHeight;
                Log.d(TAG, "onOffsetChanged: " + scale);
                ViewCompat.setScaleY(imageView, scale);
                ViewCompat.setScaleX(imageView, scale);
            }

        });
    }

    int imageOriginHeight;

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}