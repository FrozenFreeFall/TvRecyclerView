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

package com.owen.tvrecyclerview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.owen.tvrecyclerview.BaseLayoutManager;
import com.owen.tvrecyclerview.R;
import com.owen.tvrecyclerview.TwoWayLayoutManager;

import java.lang.reflect.Constructor;

public class TvRecyclerView extends RecyclerView {
    private static final String LOGTAG = TvRecyclerView.class.getSimpleName();
    private static final int DEFAULT_SELECTED_ITEM_OFFSET = 40;

    private int mVerticalSpacingWithMargins = 0;
    private int mHorizontalSpacingWithMargins = 0;
    
    private int mSelectedItemOffsetStart;
    private int mSelectedItemOffsetEnd;
    
    private boolean mSelectedItemCentered;
    private boolean mIsBaseLayoutManager;

    private int mScrollState = SCROLL_STATE_IDLE;

    private static final Class<?>[] sConstructorSignature = new Class[] {
            Context.class, AttributeSet.class};

    private final Object[] sConstructorArgs = new Object[2];
    

    public TvRecyclerView(Context context) {
        this(context, null);
    }

    public TvRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        init(context);
        
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TvRecyclerView, defStyle, 0);

        final String name = a.getString(R.styleable.TvRecyclerView_tv_layoutManager);
        if (!TextUtils.isEmpty(name)) {
            loadLayoutManagerFromName(context, attrs, name);
        }
        mSelectedItemCentered = a.getBoolean(R.styleable.TvRecyclerView_selectedItemisCentered, false);
        mSelectedItemOffsetStart = a.getInt(R.styleable.TvRecyclerView_selectedItemOffsetStart, DEFAULT_SELECTED_ITEM_OFFSET);
        mSelectedItemOffsetEnd = a.getInt(R.styleable.TvRecyclerView_selectedItemOffsetEnd, DEFAULT_SELECTED_ITEM_OFFSET);
        
        a.recycle();
    }

    private void init(Context context){
//        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        setChildrenDrawingOrderEnabled(true);
        setWillNotDraw(true); // 自身不作onDraw处理
        setHasFixedSize(true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);

        setClipChildren(false);
        setClipToPadding(false);

        setClickable(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }
    
    private void loadLayoutManagerFromName(Context context, AttributeSet attrs, String name) {
        try {
            final int dotIndex = name.indexOf('.');
            if (dotIndex == -1) {
                name = "com.owen.tvrecyclerview.widget." + name;
            } else if (dotIndex == 0) {
                final String packageName = context.getPackageName();
                name = packageName + "." + name;
            }

            Class<? extends TwoWayLayoutManager> clazz =
                    context.getClassLoader().loadClass(name).asSubclass(TwoWayLayoutManager.class);

            Constructor<? extends TwoWayLayoutManager> constructor =
                    clazz.getConstructor(sConstructorSignature);

            sConstructorArgs[0] = context;
            sConstructorArgs[1] = attrs;

            setLayoutManager(constructor.newInstance(sConstructorArgs));
        } catch (Exception e) {
            throw new IllegalStateException("Could not load TwoWayLayoutManager from " +
                                             "class: " + name, e);
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        mIsBaseLayoutManager = layout instanceof BaseLayoutManager;
//        if (!(layout instanceof TwoWayLayoutManager)) {
//            throw new IllegalArgumentException("TwoWayView can only use TwoWayLayoutManager " +
//                                                "subclasses as its layout manager");
//        }
        super.setLayoutManager(layout);
    }

    /**
     * 设置选中的Item距离开始或结束的偏移量；
     * 与滚动方向有关；
     * 与setSelectedItemAtCentered()方法二选一
     * @param offsetStart
     * @param offsetEnd
     */
    public void setSelectedItemOffset(int offsetStart, int offsetEnd) {
        this.mSelectedItemOffsetStart = offsetStart;
        this.mSelectedItemOffsetEnd = offsetEnd;
    }

    /**
     * 设置选中的Item居中；
     * 与setSelectedItemOffset()方法二选一
     * @param isCentered
     */
    public void setSelectedItemAtCentered(boolean isCentered) {
        this.mSelectedItemCentered = isCentered;
    }

    private boolean isVertical() {
        if(mIsBaseLayoutManager) {
            BaseLayoutManager layout = (BaseLayoutManager) getLayoutManager();
            return layout.isVertical();
        }
        return true;
    }
    
    private int getFreeSize() {
        if(!isVertical()) {
            return getFreeHeight();
        } else {
            return getFreeWidth();
        }
    }
    
    private int getFreeHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }
    
    private int getFreeWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if(mSelectedItemCentered) {
            mSelectedItemOffsetStart = !isVertical() ? (getFreeWidth() - child.getWidth()) : (getFreeHeight() - child.getHeight());
            mSelectedItemOffsetStart /= 2;
            mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
        }
        super.requestChildFocus(child, focused);
    }
    
    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentRight = getWidth() - getPaddingRight();
        final int parentBottom = getHeight() - getPaddingBottom();
        final int childLeft = child.getLeft() + rect.left - child.getScrollX();
        final int childTop = child.getTop() + rect.top - child.getScrollY();
        final int childRight = childLeft + rect.width();
        final int childBottom = childTop + rect.height();

        final int offScreenLeft = Math.min(0, childLeft - parentLeft - mSelectedItemOffsetStart);
        final int offScreenTop = Math.min(0, childTop - parentTop - mSelectedItemOffsetStart);
        final int offScreenRight = Math.max(0, childRight - parentRight + mSelectedItemOffsetEnd);
        final int offScreenBottom = Math.max(0, childBottom - parentBottom + mSelectedItemOffsetEnd);

        // Favor the "start" layout direction over the end when bringing one side or the other
        // of a large rect into view. If we decide to bring in end because start is already
        // visible, limit the scroll such that start won't go out of bounds.
        final int dx;
        if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            dx = offScreenRight != 0 ? offScreenRight
                    : Math.max(offScreenLeft, childRight - parentRight);
        } else {
            dx = offScreenLeft != 0 ? offScreenLeft
                    : Math.min(childLeft - parentLeft, offScreenRight);
        }

        // Favor bringing the top into view over the bottom. If top is already visible and
        // we should scroll to make bottom visible, make sure top does not go out of bounds.
        final int dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom);
        
        if (dx != 0 || dy != 0) {
            if (immediate) {
                scrollBy(dx, dy);
            } else {
                smoothScrollBy(dx, dy);
            }
            if(!isVertical()) {
                if(dx == 0) {
                    postInvalidate();
                }
            } else if (dy == 0) {
                postInvalidate();
            }
            return true;
        }

        // 重绘是为了选中item置顶，具体请参考getChildDrawingOrder方法
        postInvalidate();
        return false;
    }
    
    /**
     * 通过Margins来设置布局的横纵间距；
     * (与addItemDecoration()方法可二选一)
     * @param verticalSpacing
     * @param horizontalSpacing
     */
    public void setSpacingWithMargins(int verticalSpacing, int horizontalSpacing) {
        this.mVerticalSpacingWithMargins = verticalSpacing;
        this.mHorizontalSpacingWithMargins = horizontalSpacing;
        if(mIsBaseLayoutManager) {
            BaseLayoutManager layout = (BaseLayoutManager) getLayoutManager();
            layout.setSpacingWithMargins(verticalSpacing, horizontalSpacing);
        }
        adjustPadding();
    }
    
//    private boolean isAdjustedPadding = false;
    private void adjustPadding() {
        if((mVerticalSpacingWithMargins > 0 || mHorizontalSpacingWithMargins > 0)) {
            final int verticalSpacingHalf = mVerticalSpacingWithMargins / 2;
            final int horizontalSpacingHalf = mHorizontalSpacingWithMargins / 2;
            final int l = getPaddingLeft() - verticalSpacingHalf;
            final int t = getPaddingTop() - horizontalSpacingHalf;
            final int r = getPaddingRight() - verticalSpacingHalf;
            final int b = getPaddingBottom() - horizontalSpacingHalf;
            setPadding(l, t, r, b);
        }
    }
    
    public TwoWayLayoutManager.Orientation getOrientation() {
        if(mIsBaseLayoutManager) {
            BaseLayoutManager layout = (BaseLayoutManager) getLayoutManager();
            return layout.getOrientation();
        } else {
            return TwoWayLayoutManager.Orientation.HORIZONTAL;
        }
    }

    public void setOrientation(TwoWayLayoutManager.Orientation orientation) {
        if(mIsBaseLayoutManager) {
            BaseLayoutManager layout = (BaseLayoutManager) getLayoutManager();
            layout.setOrientation(orientation);
        }
    }

    public int getFirstVisiblePosition() {
        if(getChildCount() == 0)
            return 0;
        else
            return getChildLayoutPosition(getChildAt(0));
    }

    public int getLastVisiblePosition() {
        final int childCount = getChildCount();
        if(childCount == 0)
            return 0;
        else
            return getChildLayoutPosition(getChildAt(childCount - 1));
    }
    
    int position = 0;
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View view = getFocusedChild();
        if(null != view) {
            position = getChildAdapterPosition(view) - getFirstVisiblePosition();
            if (position < 0) {
                return i;
            } else {
                if (i == childCount - 1) {//这是最后一个需要刷新的item
                    if (position > i) {
                        position = i;
                    }
                    return position;
                }
                if (i == position) {//这是原本要在最后一个刷新的item
                    return childCount - 1;
                }
            }
        }
        return i;
    }

    @Override
    public void onScrollStateChanged(int state) {
        mScrollState = state;
    }
    
    public boolean isScrolling() {
        return mScrollState == SCROLL_STATE_SETTLING;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(isScrolling()) return true;
        
//        switch (event.getAction()) {
//            case KeyEvent.ACTION_DOWN:
//                if(onKeyDown(event.getKeyCode(), event))
//                    return true;
//                break;
//            case KeyEvent.ACTION_UP:
//                if(onKeyUp(event.getKeyCode(), event))
//                    return true;
//                break;
//        }
        
        return super.dispatchKeyEvent(event);
    }
    
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v(LOGTAG, "onKeyDown...KeyCode = " + keyCode);
        int direction = -1;
        int padding = 0;
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_DOWN:
                direction = FOCUS_DOWN;
                padding = getPaddingBottom();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                direction = FOCUS_RIGHT;
                padding = getPaddingRight();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                direction = FOCUS_LEFT;
                padding = getPaddingLeft();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                direction = FOCUS_UP;
                padding = getPaddingTop();
                break;
        }
        
        if(direction == -1 || hasInBorder(direction)) {
            return false;
        }
        
        View view = focusSearch(getFocusedChild(), direction);
        if(null != view) {
            view.requestFocus();
        }
        return true;
    }
    
    private boolean hasInBorder(int direction) {
        final View view = getFocusedChild();
        if(null == view)
            return false;
        
        switch (direction) {
            case FOCUS_DOWN:
                Log.e(LOGTAG, "onKeyDown...view.getBottom()="+view.getBottom() + " , getHeight="+getHeight());
                return (getHeight() - view.getBottom()) <= getPaddingBottom();
            case FOCUS_UP:
                return view.getTop() <= getPaddingTop();
            case FOCUS_LEFT:
                return view.getLeft() <= getPaddingLeft() && getFirstVisiblePosition() == 0;
            case FOCUS_RIGHT:
                Log.e(LOGTAG, "onKeyDown...getWidth()="+getWidth());
                Log.e(LOGTAG, "onKeyDown...view.getRight()="+view.getRight() + " , getPaddingRight()="+getPaddingRight());
                Log.e(LOGTAG, "onKeyDown...getLastVisiblePosition()="+getLastVisiblePosition() + " , getItemCount()="+getAdapter().getItemCount());
                return (getWidth() - view.getRight()) <= getPaddingRight() && getLastVisiblePosition() == (getAdapter().getItemCount() - 1);
            default:
                return false;
        }
    }
    
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        Log.e(LOGTAG, "onFocusChanged..." + gainFocus + " ,direction="+direction);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean hasFocus() {
        Log.e(LOGTAG, "hasFocus...");
        return super.hasFocus();
    }

    @Override
    public boolean isInTouchMode() {
        boolean result = super.isInTouchMode();
//        Log.e(LOGTAG, "isInTouchMode...result="+result);
        // 解决4.4版本抢焦点的问题
        if (Build.VERSION.SDK_INT == 19) {
            return !(hasFocus() && !result);
        } else {
            return result;
        }
    }
}
