/*
 * Copyright (C) 2014 Lucas Rocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.owen.tvrecyclerview.example;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.owen.tvrecyclerview.TwoWayLayoutManager;
import com.owen.tvrecyclerview.widget.SpannableGridLayoutManager;
import com.owen.tvrecyclerview.widget.StaggeredGridLayoutManager;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.SimpleViewHolder> {
    private static final int COUNT = 50;

    private final Context mContext;
    private final TvRecyclerView mRecyclerView;
    private final List<Integer> mItems;
    private final int mLayoutId;
    private int mCurrentItemId = 0;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;

        public SimpleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    public LayoutAdapter(Context context, TvRecyclerView recyclerView, int layoutId) {
        mContext = context;
        mItems = new ArrayList<Integer>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            addItem(i);
        }

        mRecyclerView = recyclerView;
        mLayoutId = layoutId;
    }

    public void addItem(int position) {
        final int id = mCurrentItemId++;
        mItems.add(position, id);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        holder.title.setText(mItems.get(position).toString());

        boolean isVertical = (mRecyclerView.getOrientation() == TwoWayLayoutManager.Orientation.VERTICAL);
        final View itemView = holder.itemView;
        final int itemId = mItems.get(position);

        if (mLayoutId == R.layout.layout_staggered_grid) {
            final int dimenId;
            if (itemId % 3 == 0) {
                dimenId = R.dimen.staggered_child_medium;
            } else if (itemId % 5 == 0) {
                dimenId = R.dimen.staggered_child_large;
            } else if (itemId % 7 == 0) {
                dimenId = R.dimen.staggered_child_xlarge;
            } else {
                dimenId = R.dimen.staggered_child_small;
            }

            final int span;
            if (itemId == 2) {
                span = 2;
            } else {
                span = 1;
            }

            final int size = mContext.getResources().getDimensionPixelSize(dimenId);

            final StaggeredGridLayoutManager.LayoutParams lp =
                    (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();

            if (!isVertical) {
                lp.span = span;
                lp.width = size;
                itemView.setLayoutParams(lp);
            } else {
                lp.span = span;
                lp.height = size;
                itemView.setLayoutParams(lp);
            }
        } else if (mLayoutId == R.layout.layout_spannable_grid) {
            final SpannableGridLayoutManager.LayoutParams lp =
                    (SpannableGridLayoutManager.LayoutParams) itemView.getLayoutParams();

            final int span1 = (itemId == 0 || itemId == 6 || itemId == 13 || itemId == 5 ? 2 : 1);
            final int span2 = (itemId == 0 || itemId == 6 || itemId == 13 ? 2 : itemId == 5 ? 4 : 1);

//            int span1 = 1;
//            int span2 = 1;
            
            final int colSpan = (isVertical ? span2 : span1);
            final int rowSpan = (isVertical ? span1 : span2);
//            lp.setMargins(15, 15, 15, 15);
            if (lp.rowSpan != rowSpan || lp.colSpan != colSpan) {
                lp.rowSpan = rowSpan;
                lp.colSpan = colSpan;
                
                itemView.setLayoutParams(lp);
            }
        } else if(mLayoutId == R.layout.layout_grid2) {
            GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();
            lp.width = 200;
//            lp.setMargins(4, 4, 4, 4);
//            if(position % 2 == 0) {
//                lp.getViewLayoutPosition()
//            }
            itemView.setLayoutParams(lp);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    
    public interface OnItemSelectedListenner {
        void onSelected(View view, int positin);
    }
}
