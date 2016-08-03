package com.owen.tvrecyclerview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.util.AttributeSet;
import android.util.Log;

import com.owen.tvrecyclerview.R;

/**
 * {@link android.support.v7.widget.RecyclerView.ItemDecoration} that applies a
 * vertical and horizontal spacing between items of the target
 * {@link android.support.v7.widget.RecyclerView}.
 */
public class SpacingItemDecoration extends ItemDecoration {
    protected int mVerticalSpacing;
    protected int mHorizontalSpacing;
    protected final ItemSpacingOffsets mItemSpacing;

    public SpacingItemDecoration(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpacingItemDecoration(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.TvRecyclerView_SpacingItemDecoration, defStyle, 0);

        mVerticalSpacing =
                Math.max(0, a.getInt(R.styleable.TvRecyclerView_SpacingItemDecoration_android_verticalSpacing, 0));
        mHorizontalSpacing =
                Math.max(0, a.getInt(R.styleable.TvRecyclerView_SpacingItemDecoration_android_horizontalSpacing, 0));

        a.recycle();

        mItemSpacing = new ItemSpacingOffsets(mHorizontalSpacing, mHorizontalSpacing);
    }

    public SpacingItemDecoration(int verticalSpacing, int horizontalSpacing) {
        mItemSpacing = new ItemSpacingOffsets(verticalSpacing, horizontalSpacing);
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        mItemSpacing.getItemOffsets(outRect, itemPosition, parent);
        Log.e("SpacingItemDecoration", "itemPosition=" + itemPosition + " , outRect.right"+ outRect.right 
                + " , outRect.left="+outRect.left
                + " , outRect.top="+outRect.top
                + " , outRect.bottom="+outRect.bottom
        );
    }
}
