package com.asiawaters.fieldapprover.classes;

/**
 * Created by Андрей on 05.08.2016.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * A ListView that maintains a header pinned at the top of the list. The
 * pinned header can be pushed up and dissolved as needed.
 */
public class PinnedHeaderExpListView extends ExpandableListView{

    private static final int MAX_ALPHA = 255;

    private FAExpandableListAdapter mAdapter;
    private View mHeaderView;
    private boolean mHeaderViewVisible;

    private int mHeaderViewWidth;

    private int mHeaderViewHeight;

    public PinnedHeaderExpListView(Context context) {
        super(context);
    }

    public PinnedHeaderExpListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinnedHeaderExpListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPinnedHeaderView(View view) {
        mHeaderView = view;

        // Disable vertical fading when the pinned header is present
        // TODO change ListView to allow separate measures for top and bottom fading edge;
        // in this particular case we would like to disable the top, but not the bottom edge.
        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }
        requestLayout();
    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (FAExpandableListAdapter)adapter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }
    }

    /**
     * animating header pushing
     * @param position
     */
    public void configureHeaderView(int position) {
        final int group = getPackedPositionGroup(getExpandableListPosition(position));
        if (group<0) return;

        int groupView = getFlatListPosition(getPackedPositionForGroup(group));

        if (mHeaderView == null) {
            return;
        }

        mHeaderView.setOnClickListener(new OnClickListener() {

            public void onClick(View header) {
                if(!expandGroup(group)) collapseGroup(group);
            }
        });

        int state,nextSectionPosition = getFlatListPosition(getPackedPositionForGroup(group+1));

        if (mAdapter.getGroupCount()== 0) {
            state = PinnedHeaderAdapter.PINNED_HEADER_GONE;
        }else if (position < 0) {
            state = PinnedHeaderAdapter.PINNED_HEADER_GONE;
        }else if (nextSectionPosition != -1 && position == nextSectionPosition - 1) {
            state=PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP;
        }else  state=PinnedHeaderAdapter.PINNED_HEADER_VISIBLE;

        switch (state) {
            case PinnedHeaderAdapter.PINNED_HEADER_GONE: {
                mHeaderViewVisible = false;
                break;
            }

            case PinnedHeaderAdapter.PINNED_HEADER_VISIBLE: {
                mAdapter.configurePinnedHeader(mHeaderView, group, MAX_ALPHA);
                if (mHeaderView.getTop() != 0) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }
                mHeaderViewVisible = true;
                break;
            }

            case PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP: {
                View firstView = getChildAt(0);
                if(firstView==null){
                    if (mHeaderView.getTop() != 0) {
                        mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                    }
                    mHeaderViewVisible = true;
                    break;
                }
                int bottom = firstView.getBottom();
                int itemHeight = firstView.getHeight();
                int headerHeight = mHeaderView.getHeight();
                int y;
                int alpha;
                if (bottom < headerHeight) {
                    y = (bottom - headerHeight);
                    alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
                } else {
                    y = 0;
                    alpha = MAX_ALPHA;
                }
                mAdapter.configurePinnedHeader(mHeaderView, group, alpha);
                //выползание
                if (mHeaderView.getTop() != y) {
                    mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                }
                mHeaderViewVisible = true;
                break;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisible) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }

}