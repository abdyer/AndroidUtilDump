package couk.doridori.android.lib.app;

import de.greenrobot.event.EventBus;

/**
 * As there is no global callback for the state of the app i.e. is the app in the foreground or not you can have a base class call this in onStart and onStop and it will do a global event for anyone who is interested
 *
 * User: doriancussen
 * Date: 12/09/2013
 */
public class ActivityCounter
{
    //==================================================================================================
    // SINGLETON
    //==================================================================================================

    private static final ActivityCounter sInstance = new ActivityCounter();

    private ActivityCounter() { /*singleton*/ }

    public static ActivityCounter getsInstance()
    {
        return sInstance;
    }

    //==================================================================================================
    // CLASS
    //==================================================================================================

    public int mStartedCount = 0;

    public void activityStarted()
    {
        mStartedCount++;
    }

    public void activityStopped()
    {
        mStartedCount--;

        if(mStartedCount == 0)
        {
            EventBus.getDefault().post(new ApplicationMovedIntoBackgroundEvent());
        }
    }

    public class ApplicationMovedIntoBackgroundEvent{}
}
