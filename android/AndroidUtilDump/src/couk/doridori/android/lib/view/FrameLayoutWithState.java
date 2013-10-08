/**
 Copyright [2011] [Dorian Cussen]

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package couk.doridori.android.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import couk.doridori.android.lib.R;

/**
 * Copyright (C) 2011 Dorian Cussen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *
 * Like a normal frameLayout apart from this will swap out its views
 * depending on some data state.
 *
 * You should pass in layout file ids to the empty and loading resId xml
 * attrbutes and an id into the content view attr
 *
 * If you supply your own error layout it must have a textView with the id
 * 'state_error_text'
 *
 * If your going to use the same loading / error / empty views (with differnt text) just set them in the source below and make sure the contents ids for the textViews match
 *
 * Will auto hide all children on start
 *
 * <b>WARNING - Samsung s3 running 4.0.4 (possibly a 4.0.4 bug) cannot handle a view changing from GONE to VISIBLE with <code>animateLayoutChanges=true</code>. As this is a Framelayout you can either change to INVISIBLE instead of GONE (less efficent as will still be measured when not vis) OR implement custom show hide anims for this class. Prob best to just not use animateLayoutChanges. Custom animations solution is untested however :)<b/> Think this has something to do with view invlidation as a PTR etc will then show the view
 *
 * Animations can be setup but using layoutTransitions = true in the manifest (unless they have been globally disabled in the user settings)
 *
 * TODO: should add the child views the same way as done for AOSP views - and not via a post in onLayout. Causing issues like not being abel to grab child views after setContentView is called
 */
public class FrameLayoutWithState extends FrameLayout {

    private ViewState mCurrentViewState = ViewState.NOT_INIT;
    private int mLoadingResId, mEmptyResId, mContentResId, mErrorResId;
    private View mLoadingView, mEmptyView, mContentView, mErrorView;
    private boolean mHasBeenLayedOut;
    private String mErrorText = null;
    private String mEmptyText;

    private OnClickListener mErrorClickListener;

    public FrameLayoutWithState(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        getCustomAttrs(context, attrs);
    }

    public FrameLayoutWithState(Context context, AttributeSet attrs) {

        super(context, attrs);
        getCustomAttrs(context, attrs);
    }

    public FrameLayoutWithState(Context context) {

        super(context);
        throw new RuntimeException("Use a constructor with attrs");
    }

    private void getCustomAttrs(Context context, AttributeSet attrs) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FrameLayoutWithState);

        // get state layout res id's if present, else use default
        mEmptyResId = array.getResourceId(R.styleable.FrameLayoutWithState_emptyView,
                R.layout.element_data_state_empty);
        mLoadingResId = array.getResourceId(R.styleable.FrameLayoutWithState_loadingView,
                R.layout.element_data_state_loading);
        mErrorResId = array.getResourceId(R.styleable.FrameLayoutWithState_errorView,
                R.layout.element_data_state_error);

        if (array.hasValue(R.styleable.FrameLayoutWithState_contentView) == false)
            throw new RuntimeException("need to set contentView attr");
        mContentResId = array.getResourceId(R.styleable.FrameLayoutWithState_contentView, -1);

        array.recycle();
    }

    private void inflateStateViews() {

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // dont inflate - just grab from contents
        mContentView = findViewById(mContentResId);

        if (mContentView == null) {
            throw new NullPointerException("contentView cannot be null, have you set the contentView attribute");
        }

        if(mContentView.getVisibility() != View.GONE)
            throw new RuntimeException("need to set gone in xml or will flicker");

        mContentView.setVisibility(View.GONE);

        mLoadingView = layoutInflater.inflate(mLoadingResId, this, false);
        mLoadingView.setVisibility(View.GONE);
        addView(mLoadingView);

        mEmptyView = layoutInflater.inflate(mEmptyResId, this, false);
        mEmptyView.setVisibility(View.GONE);
        addView(mEmptyView);

        mErrorView = layoutInflater.inflate(mErrorResId, this, false);
        mErrorView.setVisibility(View.GONE);
        addView(mErrorView);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);

        if (mContentView == null) // have not got a handle to the content view
        // yet
        {
            // need to post these operations in a handler otherwise will not display when > 3.0.
            // not sure why this is but presumably some method thats called after onLayout() and
            // before any posted runnables are run is stopping it working as expected - Dori
            new Handler().post(new Runnable() {

                @Override
                public void run() {

                    mHasBeenLayedOut = true;
                    inflateStateViews();
                    showViewBasedOnState(true);
                    if(mErrorText != null){
                        setErrorText(mErrorText);
                    }
                    if(mEmptyText != null){
                        setEmptyText(mEmptyText);
                    }
                    if(null != mErrorClickListener)
                        mErrorView.setOnClickListener(mErrorClickListener);
                }
            });
        }
    }

    public void setViewState(ViewState newViewState) {
        setViewState(newViewState, true);
    }

    /**
     * @param newViewState
     * @param animate true if should animate when showing content
     */
    public void setViewState(ViewState newViewState, boolean animate) {

        mCurrentViewState = newViewState;

        if (mHasBeenLayedOut) {
            showViewBasedOnState(animate);
        }
    }

    /**
     * @param msg can not be null
     */
    public void setViewStateError(String msg) {

        mCurrentViewState = ViewState.ERROR;
        mErrorText = msg;

        if (mHasBeenLayedOut) {
            showViewBasedOnState(true);
            setErrorText(mErrorText);
        }
    }

    /**
     * @param msg can not be null
     */
    public void setViewStateEmpty(String msg) {

        mCurrentViewState = ViewState.EMPTY;
        mEmptyText = msg;

        if (mHasBeenLayedOut) {
            showViewBasedOnState(true);
            setEmptyText(mEmptyText);
        }
    }

    public void setOnClickForError(OnClickListener onClickListener)
    {
        if(mHasBeenLayedOut)
            mErrorView.setOnClickListener(onClickListener);
        else
            mErrorClickListener = onClickListener;
    }

    /**
     * If a custom error view has been used it will have to include a textView with the ID R.id.state_error_text for
     * this method to not throw an exception!
     *
     * @param errorTxt can not be null
     */
    private void setErrorText(String errorTxt) {
        TextView errorTxtView = (TextView) findViewById(R.id.state_error_text);
        errorTxtView.setText(mErrorText);
    }

    /**
     * If a custom empty view has been used it will have to include a textView with the ID R.id.state_empty_text for
     * this method to not throw an exception!
     *
     * @param emptyText can not be null
     */
    private void setEmptyText(String emptyText){
        TextView emptyTxtView = (TextView) findViewById(R.id.state_empty_text);
        emptyTxtView.setText(emptyText);
    }

    public ViewState getViewState() {

        return mCurrentViewState;
    }

    /**
     * @param animate true if should animate when showing content
     */
    private void showViewBasedOnState(boolean animate) {

        switch (mCurrentViewState) {
            case NOT_INIT:
                // hide all
                mLoadingView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                mContentView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.GONE);
                break;

            case CONTENT:

                // show content view
                mLoadingView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                mContentView.setVisibility(View.VISIBLE);
                mErrorView.setVisibility(View.GONE);
                break;

            case EMPTY:
                // show empty view
                mLoadingView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mContentView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.GONE);
                break;

            case LOADING:
                // show loading view
                mLoadingView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                mContentView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.GONE);
                break;

            case ERROR:
                // show error view
                mLoadingView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                mContentView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.VISIBLE);
                break;

        }

        invalidate();
    }

    public static enum ViewState{
        /**
         * Loading has not started yet
         */
        NOT_INIT,
        /**
         * Loading started
         */
        LOADING,
        /**
         * Loading finished and empty data
         */
        EMPTY,
        /**
         * Loading finished with success
         */
        CONTENT,
        /**
         * Loading finished with error
         */
        ERROR;
    }

}

