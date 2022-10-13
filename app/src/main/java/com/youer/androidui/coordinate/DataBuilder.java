package com.youer.androidui.coordinate;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

/**
 * @author youer
 * @date 2022/8/12
 */
public class DataBuilder {
    public static void buildRecycleView(Context context, RecyclerView recyclerView) {
        final int[] id = {0};
        LinearLayoutManager layoutManager =new LinearLayoutManager(context, 1, false);
        layoutManager.setSmoothScrollbarEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new Adapter() {
            /**
             * @param parent
             * @param viewType
             * @return
             */
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView textView = new TextView(context);
                textView.setTextColor(Color.BLACK);
                return new MyViewHolder(textView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((TextView)(holder.itemView)).setText("item" + id[0]);
                id[0]++;
            }

            @Override
            public int getItemCount() {
                return 100;
            }
        });
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
} 