package couk.doridori.android.lib.model;

/**
 * User: doriancussen
 * Date: 09/11/2012
 */
public class Session {

    private static Session sInstance;

    private boolean mSessionValid = false;

    /**
     * This should be called on successful login or at anytime a new session starts
     */
    private static void newSession(){
        sInstance = new Session();
    }

    private static Session getsInstance(){
        return sInstance;
    }

    private Session(){
        //singleton
    }

    /**
     * Call this is all activitys onResume() that are inside a session i.e. require the session to be valid. If false
     * you should retrun to the point where the session is created (via CLEAR_TOP on an activity still in the stack)
     *
     * @return
     */
    public boolean isSessionValid() {
        return null != sInstance;
    }
}