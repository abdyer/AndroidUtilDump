package couk.doridori.android.lib.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

/**
 * In your project you can extends this class and add your project specific KEYS
 *
 * User: doriancussen
 * Date: 01/11/2012
 */
public abstract class AbsPrefsHelper {

    private static SharedPreferences sSharedPreferences;

    public static synchronized SharedPreferences getDefaultSharedPreferences(Context context){
        if(sSharedPreferences == null)
            sSharedPreferences = context.getSharedPreferences("defaultPrefs", Context.MODE_PRIVATE);

        return sSharedPreferences;
    }

    public static synchronized String getString(Context context, String key, String defValue){
        return getDefaultSharedPreferences(context).getString(key, defValue);
    }

    public static synchronized SharedPreferences.Editor getEditor(Context ctx){
        return getDefaultSharedPreferences(ctx).edit();
    }

    public static synchronized void asyncCommit(final SharedPreferences.Editor editor){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            editor.apply();
        }else{
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    editor.commit();
                    return null;
                }
            }.execute();
        }
    }

    public synchronized void removePref(Context ctx, String key){
        SharedPreferences.Editor editor = getEditor(ctx);
        editor.remove(key);
        asyncCommit(editor);
    }


}
