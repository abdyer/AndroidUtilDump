package couk.doridori.android.lib.ui;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockMapActivity;
import couk.doridori.android.lib.model.ISession;

/**
 * This activity provides alt lifecycle methods so if the session call fails your activity will not have its own
 * init  functionality called in the traditional lifecycle methods
 *
 * User: doriancussen
 * Date: 09/11/2012
 */
public abstract class InSessionMapActivity extends SherlockMapActivity {

    private boolean mIsSessionValid;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(null == getSession()){
            //session invalid - jump back to session start activity
            mIsSessionValid = false;
            Intent intent = new Intent(this, getSessionStartActivityClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            mIsSessionValid = true;
            onCreatePostSessionCheck(savedInstanceState);
        }
    }

    @Override
    protected final void onStart() {
        super.onStart();
        if(mIsSessionValid){
            onStartPostSessionCheck();
        }
    }

    @Override
    protected final void onResume() {
        super.onResume();
        if(mIsSessionValid){
            onResumePostSessionCheck();
        }
    }

    protected abstract void onCreatePostSessionCheck(Bundle savedInstanceState);
    protected void onStartPostSessionCheck(){};
    protected void onResumePostSessionCheck(){};

    /**
     * @return return the class of the activity that initiates the session here
     */
    protected abstract Class getSessionStartActivityClass();
    protected abstract ISession getSession();
}
