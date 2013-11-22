package couk.doridori.android.lib.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.*;
import couk.doridori.android.lib.R;

/**
 * This class enables you to use multiple <b>ListView headers</b> and set one of them as <b>sticky</b> (not having sticky group headings OR sticky views inside a scrollView, there are OS projects that do that already)
 *
 * This viewgroup contains the sticky header view and the listview and uses a similar approach to the one described
 * here
 *
 * http://www.pushing-pixels.org/2011/07/18/android-tips-and-tricks-synchronized-scrolling.html but for listviews instead
 * of scrollviews
 *
 * with some code taken from
 *
 * http://applidium.github.io/HeaderListView/
 *
 * Also see https://plus.google.com/u/0/+RomanNurik/posts/1Sb549FvpJt
 *
 * @Author dori
 */
public class StickyHeaderListViewHolder extends RelativeLayout
{
    private InternalListView mListView;
    private RelativeLayout mStickyHeaderWrapper;
    private FrameLayout mScrollBarView;
    private boolean mStickyHeaderSet;
    private int mStickyHeaderIndex;

    public StickyHeaderListViewHolder(Context context)
    {
        super(context);
        init(context);
    }

    public StickyHeaderListViewHolder(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public StickyHeaderListViewHolder(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        //LIST VIEW
        mListView = new InternalListView(getContext());
        LayoutParams listParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        listParams.addRule(ALIGN_PARENT_TOP);
        mListView.setLayoutParams(listParams);
        mListView.setOnScrollListener(new OnScrollListener());
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setOverScrollMode(OVER_SCROLL_NEVER);
        addView(mListView);

        //STICKY HEADER VIEW
        mStickyHeaderWrapper = new RelativeLayout(getContext());
        LayoutParams headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        headerParams.addRule(ALIGN_PARENT_TOP);
        mStickyHeaderWrapper.setLayoutParams(headerParams);
        mStickyHeaderWrapper.setGravity(Gravity.BOTTOM);
        addView(mStickyHeaderWrapper);

        //CUSTOM SCROLL BAR
        // The list view's scroll bar can be hidden by the header, so we display our own scroll bar instead
        Drawable scrollBarDrawable = getResources().getDrawable(R.drawable.scrollbar_handle_holo_light);
        mScrollBarView = new FrameLayout(getContext());
        LayoutParams scrollParams = new LayoutParams(scrollBarDrawable.getIntrinsicWidth(), LayoutParams.MATCH_PARENT);
        scrollParams.addRule(ALIGN_PARENT_RIGHT);
        scrollParams.rightMargin = (int) dpToPx(2);
        mScrollBarView.setLayoutParams(scrollParams);

        ImageView scrollIndicator = new ImageView(context);
        scrollIndicator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        scrollIndicator.setImageDrawable(scrollBarDrawable);
        scrollIndicator.setScaleType(ImageView.ScaleType.FIT_XY);
        mScrollBarView.addView(scrollIndicator);

        addView(mScrollBarView);
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getContext().getResources().getDisplayMetrics());
    }

    public ListView getListView()
    {
        return mListView;
    }

    /**
     * Use this instead of adding headers to the listview directly
     *
     * @param headerView
     * @param isSticky
     */
    public void addHeaderView(View headerView, boolean isSticky, int height)
    {
        if(mStickyHeaderSet)
            throw new RuntimeException("Only one of the header views can be sticky!");

        if(isSticky)
        {
            mStickyHeaderSet = true;
            mStickyHeaderIndex = mListView.getHeaderViewsCount(); //do this before adding as the index is count-1 anyhow
            mStickyHeaderWrapper.addView(headerView);

            //need to add a header view of the same size that will be position tracked and we will manually move the floating 'sticky' header view in line with this
            View fakeHeader = new View(headerView.getContext());
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height);
            fakeHeader.setLayoutParams(layoutParams);
            fakeHeader.setBackgroundColor(Color.WHITE);
            mListView.addHeaderView(fakeHeader);
        }
        else
            mListView.addHeaderView(headerView);
    }


    private class OnScrollListener implements AbsListView.OnScrollListener
    {
        private static final int FADE_DELAY    = 1000;
        private static final int FADE_DURATION = 2000;

        private AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            switch(scrollState)
            {
                case SCROLL_STATE_IDLE:
                    mStickyHeaderWrapper.requestLayout();
                    break;
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            updateScrollBar();

            //grab the row view we are pinning too
            if(!mStickyHeaderSet)
                return;

            int yDisplacement;

            //if dummy header row is on screen we should pin the floating header view to it
            if(mStickyHeaderIndex >= firstVisibleItem && mStickyHeaderIndex < (firstVisibleItem+visibleItemCount))
            {
                //TODO this will miss the very first update
                int fakeHeaderChildIndex = mStickyHeaderIndex - firstVisibleItem;
                View fakeHeaderRow = mListView.getChildAt(fakeHeaderChildIndex);
                int top = fakeHeaderRow.getTop();
                Log.d("onScroll", "top:"+top);

                yDisplacement = Math.max(0,top);
            }
            else
            {
                yDisplacement = 0;
            }


            LayoutParams params = (LayoutParams) mStickyHeaderWrapper.getChildAt(0).getLayoutParams();

            boolean scheduleLayout = false;
            if(params.topMargin != yDisplacement)
                scheduleLayout = true;

            params.topMargin = yDisplacement;

            if(scheduleLayout)
                mStickyHeaderWrapper.requestLayout();
        }

        private void updateScrollBar() {
            if (mStickyHeaderWrapper != null && mListView != null && mScrollBarView != null) {
                int offset = mListView.computeVerticalScrollOffset();
                int range = mListView.computeVerticalScrollRange();
                int extent = mListView.computeVerticalScrollExtent();
                mScrollBarView.setVisibility(extent >= range ? View.INVISIBLE : View.VISIBLE);
                if (extent >= range) {
                    return;
                }
                int top = range == 0 ? mListView.getHeight() : mListView.getHeight() * offset / range;
                int bottom = range == 0 ? 0 : mListView.getHeight() - mListView.getHeight() * (offset + extent) / range;
                mScrollBarView.setPadding(0, top, 0, bottom);
                fadeOut.reset();
                fadeOut.setFillBefore(true);
                fadeOut.setFillAfter(true);
                fadeOut.setStartOffset(FADE_DELAY);
                fadeOut.setDuration(FADE_DURATION);
                mScrollBarView.clearAnimation();
                mScrollBarView.startAnimation(fadeOut);
            }
        }
    }

    /**
     * Used so the scroll compute methods can access the below protected methods
     */
    protected class InternalListView extends ListView {

        public InternalListView(Context context) {
            super(context);
        }

        @Override
        protected int computeVerticalScrollExtent() {
            return super.computeVerticalScrollExtent();
        }

        @Override
        protected int computeVerticalScrollOffset() {
            return super.computeVerticalScrollOffset();
        }

        @Override
        protected int computeVerticalScrollRange() {
            return super.computeVerticalScrollRange();
        }
    }
}
