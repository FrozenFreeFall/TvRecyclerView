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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.owen.tvrecyclerview.widget.TvRecyclerView;

import static android.support.v7.widget.RecyclerView.HORIZONTAL;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class LayoutFragment extends Fragment {
    private static final String ARG_LAYOUT_ID = "layout_id";

    private TvRecyclerView mRecyclerView;
    private TextView mPositionText;
    private TextView mCountText;
    private TextView mStateText;
    private Toast mToast;

    private int mLayoutId;

    public static LayoutFragment newInstance(int layoutId) {
        LayoutFragment fragment = new LayoutFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_ID, layoutId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = getArguments().getInt(ARG_LAYOUT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(mLayoutId, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Activity activity = getActivity();

        mToast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);

        mRecyclerView = (TvRecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);

        mPositionText = (TextView) view.getRootView().findViewById(R.id.position);
        mCountText = (TextView) view.getRootView().findViewById(R.id.count);

        mStateText = (TextView) view.getRootView().findViewById(R.id.state);
        updateState(SCROLL_STATE_IDLE);

        /*final ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);

        itemClick.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                mToast.setText("Item clicked: " + position);
                mToast.show();
            }
        });

        itemClick.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View child, int position, long id) {
                mToast.setText("Item long pressed: " + position);
                mToast.show();
                return true;
            }
        });*/
        
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                updateState(scrollState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                mPositionText.setText("First: " + mRecyclerView.getFirstVisiblePosition());
                mCountText.setText("Count: " + mRecyclerView.getChildCount());
            }
        });
        
        if(mLayoutId == R.layout.layout_grid2) {
            IGridLayoutManager manager = new IGridLayoutManager(getContext(), 3);
            manager.setOrientation(HORIZONTAL);
            mRecyclerView.setLayoutManager(manager);
        } else {
//            final Drawable divider = getResources().getDrawable(R.drawable.divider);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));
//            mRecyclerView.addItemDecoration(new SpacingItemDecoration(20, 20));
            
//        mRecyclerView.setOrientation(TwoWayLayoutManager.Orientation.VERTICAL);
        }
        
        // 通过Margins来设置布局的横纵间距(与addItemDecoration()方法可二选一)
        mRecyclerView.setSpacingWithMargins(18, 18);
        
        // 设置选中的Item距离开始或结束的偏移量（与setSelectedItemAtCentered()方法二选一）
        mRecyclerView.setSelectedItemOffset(120, 120);
        // 设置选中的Item居中（与setSelectedItemOffset()方法二选一）
//        mRecyclerView.setSelectedItemAtCentered(true);

        mRecyclerView.setAdapter(new LayoutAdapter(activity, mRecyclerView, mLayoutId));
    }

    private void updateState(int scrollState) {
        String stateName = "Undefined";
        switch(scrollState) {
            case SCROLL_STATE_IDLE:
                stateName = "Idle";
                break;

            case SCROLL_STATE_DRAGGING:
                stateName = "Dragging";
                break;

            case SCROLL_STATE_SETTLING:
                stateName = "Flinging";
                break;
        }
//        Log.v("TwoWayView", "scrollState = "+stateName);
        
        mStateText.setText(stateName);
    }

    public int getLayoutId() {
        return getArguments().getInt(ARG_LAYOUT_ID);
    }
}
