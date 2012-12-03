package couk.doridori.android.lib.util;

import android.content.Intent;

/**
 * @author Dorian Cussen
 *         Date: 29/11/2012
 */
public class Verifier
{
    public static boolean anyNull(Object... objects){
        for(Object o : objects){
            if(null == o)
                return true;
        }

        return false;
    }

    public static boolean anyIntentKeysMissing(Intent intent, String... keys){
        for(String key : keys){
            if(!intent.getExtras().containsKey(key))
                return true;
        }

        return false;
    }


}
