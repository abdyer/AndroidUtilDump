package couk.doridori.android.lib.util;

import android.content.Intent;

/**
 * @author Dorian Cussen
 *         Date: 29/11/2012
 */
public class Verifier
{
    public static boolean isAllNotNull(Object... objects)
    {
        for(Object object : objects)
        {
            if(null == object)
                return false;
        }

        return true;
    }

    public static void throwIfAnyNull(Object... objects)
    {
        for (int i = 0; i < objects.length; i++)
        {
            if (null == objects[i])
            {
                String errorMsg = "Arg with index " + i + " == null!!";
                XLog.e(errorMsg);
                throw new NullPointerException(errorMsg);
            }
        }
    }

    public static void throwIfAllNull(Object... objects)
    {
        if(objects.length == 0)
        {
            throw new NullPointerException("no objects passed in");
        }

        boolean allNull = true;

        for (int i = 0; i < objects.length; i++)
        {
            if (null != objects[i])
            {
                allNull = false;
            }
        }

        if(allNull)
            throw new NullPointerException("All NULL!");
    }

    public static void throwIfIntentKeysMissing(Intent intent, String... keys)
    {
        if(null == intent.getExtras())
            throw new RuntimeException("No intent extras at all!");

        StringBuilder builder = new StringBuilder();
        boolean anyMissing = false;

        for (String key : keys)
        {
            if (!intent.getExtras().containsKey(key))
            {
                anyMissing = true;
                XLog.w("Intent extra missing with key : "+key);
                builder.append(key+" ,");
            }
        }

        if(anyMissing)
            throw new RuntimeException("Intent key(s) missing : "+builder.toString());
    }
}
