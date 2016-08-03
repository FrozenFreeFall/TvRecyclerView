package com.owen.tvrecyclerview.example;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by owen on 16/7/22.
 */
public class IGridLayoutManager extends GridLayoutManager{
    private static final String LOGTAG = "IGridLayoutManager";
    
    public IGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public IGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public IGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean requestChildRectangleOnScreen(final RecyclerView parent, View child, Rect rect, boolean immediate) {

        int topPadding = 0;
        int bottomPadding = 0;
        int leftPadding = 230;
        int rightPadding = 130;
        //
        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentRight = getWidth() - getPaddingRight();
        final int parentBottom = getHeight() - getPaddingBottom();
        final int childLeft = child.getLeft() + rect.left;
        final int childTop = child.getTop() + rect.top;
        final int childRight = childLeft + rect.width();
        final int childBottom = childTop + rect.height();

        final int offScreenLeft = Math.min(0, childLeft - parentLeft - leftPadding);
        final int offScreenTop = Math.min(0, childTop - parentTop - topPadding);
        final int offScreenRight = Math.max(0, childRight - parentRight + rightPadding);
        final int offScreenBottom = Math.max(0, childBottom - parentBottom + bottomPadding);

        Rect childRect = new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        // Favor the "start" layout direction over the end when bringing one
        // side or the other
        // of a large rect into view. If we decide to bring in end because start
        // is already
        // visible, limit the scroll such that start won't go out of bounds.
        int dx;
        if (getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL) {
            dx = offScreenRight != 0 ? offScreenRight : Math.max(offScreenLeft, childRight - parentRight);
        } else {
            dx = offScreenLeft != 0 ? offScreenLeft : Math.min(childLeft - parentLeft, offScreenRight);
        }

        // Favor bringing the top into view over the bottom. If top is already
        // visible and
        // we should scroll to make bottom visible, make sure top does not go
        // out of bounds.
        int dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom);
        //
        //
        if (dx != 0 || dy != 0) {
            if (immediate) {
                parent.scrollBy(dx, dy);
            } else {
                parent.smoothScrollBy(dx, dy);
            }
            if(getOrientation() == HORIZONTAL) {
                if(dx == 0) {
                    parent.postInvalidate();
                }
            } else {
                if(dy == 0) {
                    parent.postInvalidate();
                }
            }
            Log.e(LOGTAG, "requestChildRectangleOnScreen , dx = "+dx + " , dy = "+dy + " , immediate = " + immediate);
            Log.e(LOGTAG, "requestChildRectangleOnScreen"
                    + " , parentLeft = "+parentLeft
                    + " , parentTop = "+parentTop
                    + " , parentRight = " + parentRight
                    + " , parentBottom = " + parentBottom
                    + " , childLeft = " + childLeft
                    + " , childTop = " + childTop
                    + " , childRight = " + childRight
                    + " , childBottom = " + childBottom
                    + " , offScreenLeft = " + offScreenLeft
                    + " , offScreenTop = " + offScreenTop
                    + " , offScreenRight = " + offScreenRight
                    + " , offScreenBottom = " + offScreenBottom
            );
            return true;
        }
        // 重绘是为了选中item置顶，具体请参考getChildDrawingOrder方法
        parent.postInvalidate();
        Log.e(LOGTAG, "requestChildRectangleOnScreen");
        return false;
    }
}
