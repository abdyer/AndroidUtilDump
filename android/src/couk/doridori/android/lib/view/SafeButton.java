package couk.doridori.android.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

/**
 * Class to work around the annoying queuing of multiple taps for Buttons. Could have a time attribute
 *
 * @author dori
 */
public class SafeButton extends Button {
    public SafeButton(Context context) {
        super(context);
    }

    public SafeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SafeButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(new OnClickListenerWrapper(l));
    }

    private class OnClickListenerWrapper implements OnClickListener{

        private static final long DEFAULT_MIN_INTERVAL = 1500;
        private long mLastClickTime = 0;
        private final OnClickListener mListener;

        public OnClickListenerWrapper(OnClickListener listener){
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            if(currentTime - mLastClickTime > DEFAULT_MIN_INTERVAL){
                mListener.onClick(v);
                mLastClickTime = currentTime;
            }
        }
    }
}
