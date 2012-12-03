package couk.doridori.android.lib.util;

import android.content.Intent;

/**
 * @author Dorian Cussen
 *         Date: 29/11/2012
 */
public class Verifier
{
    public static void anyNull(Object... objects) throws NullPointerException {
        for(int i = 0; i < objects.length; i++){
            if(null == objects[i]){
                String errorMsg = "Arg "+i+" == null!!";
                XLog.e(errorMsg);
                throw new NullPointerException(errorMsg);
            }
        }
    }

    public static boolean anyIntentKeysMissing(Intent intent, String... keys){
        for(String key : keys){
            if(!intent.getExtras().containsKey(key))
                return true;
        }

        return false;
    }


}
