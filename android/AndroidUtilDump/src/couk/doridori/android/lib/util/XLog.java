package couk.doridori.android.lib.util;

import android.util.Log;

public class XLog
{

    private static final int NONE = 0;
    private static final int ERRORS_ONLY = 1;
    private static final int ERRORS_WARNINGS = 2;
    private static final int ERRORS_WARNINGS_INFO = 3;
    private static final int ERRORS_WARNINGS_INFO_DEBUG = 4;
    private static final int ALL = 5;

    private static final int LOGGING_LEVEL_FILTER = ALL;

    static
    {
        if (LOGGING_LEVEL_FILTER >= ALL)
            Log.v(XLog.class.getName(), "ILog class reloaded");
    }

    public static void e(String text, Exception e)
    {
        e(text + " : " + e.getClass().getName() + ": " + e.getMessage());
    }

    public static void e(String text)
    {
        if (LOGGING_LEVEL_FILTER >= ERRORS_ONLY)
        {
            Trace trace = getTrace();
            Log.e(trace.clazz, trace.trace + text);
        }
    }

    public static void w(String text)
    {
        if (LOGGING_LEVEL_FILTER >= ERRORS_WARNINGS)
        {
            Trace trace = getTrace();
            Log.w(trace.clazz, getTrace().trace + text);
        }
    }

    public static void i(String text)
    {
        if (LOGGING_LEVEL_FILTER >= ERRORS_WARNINGS_INFO)
        {
            Trace trace = getTrace();
            Log.i(trace.clazz, getTrace().trace + text);
        }
    }

    public static void d()
    {
        if (LOGGING_LEVEL_FILTER >= ERRORS_WARNINGS_INFO_DEBUG)
        {
            Trace trace = getTrace();
            Log.d(trace.clazz, getTrace().trace + "<method>");
        }
    }

    public static void d(Object obj)
    {
        if (LOGGING_LEVEL_FILTER >= ERRORS_WARNINGS_INFO_DEBUG)
        {
            Trace trace = getTrace();
            Log.d(obj.toString(), getTrace().trace);
        }
    }

    public static void d(String text)
    {
        if (LOGGING_LEVEL_FILTER >= ERRORS_WARNINGS_INFO_DEBUG)
        {
            Trace trace = getTrace();
            Log.d(trace.clazz, getTrace().trace + text);
        }
    }

    public static void dn(String text)
    {
        if (LOGGING_LEVEL_FILTER >= ERRORS_WARNINGS_INFO_DEBUG)
        {
            Trace trace = getTrace();
            Log.d(trace.clazz, "");//blank line
            Log.d(trace.clazz, getTrace().trace + text);
        }
    }

    public static void d(String tag, String text)
    {
        if (LOGGING_LEVEL_FILTER >= ERRORS_WARNINGS_INFO_DEBUG)
        {
            Trace trace = getTrace();
            Log.d(tag, getTrace().trace + text);
        }
    }

    public static void d(Object obj, String text)
    {
        if (LOGGING_LEVEL_FILTER >= ERRORS_WARNINGS_INFO_DEBUG)
        {
            Trace trace = getTrace();
            Log.d(obj.getClass().getName(), getTrace().trace + text);
        }
    }

    public static void v()
    {
        if (LOGGING_LEVEL_FILTER >= ALL)
        {
            Trace trace = getTrace();
            Log.v(trace.clazz, getTrace().trace + "<method>");
        }
    }

    public static void v(String text)
    {
        if (LOGGING_LEVEL_FILTER >= ALL)
        {
            Trace trace = getTrace();
            Log.v(trace.clazz, getTrace().trace + text);
        }
    }

    /**
     * print the stack of execution path to log output - sometimes .printStackTrace can be out of order with log statements
     */
    public static void dTraceWhole()
    {

        if (LOGGING_LEVEL_FILTER >= ERRORS_WARNINGS_INFO_DEBUG)
        {
            Throwable t = new Throwable();
            StackTraceElement[] elements = t.getStackTrace();
            StringBuilder builder = new StringBuilder();
            for (StackTraceElement e : elements)
            {
                builder.append("\n\t");
                builder.append(e);
            }
            Log.d(XLog.class.getName(), getTrace().trace + builder.toString());
        }
    }

    private static Trace getTrace()
    {

        int depth = 2;
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();
        String callerMethodName = elements[depth].getMethodName();
        String callerClassPath = elements[depth].getClassName();
        int lineNum = elements[depth].getLineNumber();
        int i = callerClassPath.lastIndexOf('.');
        String callerClassName = callerClassPath.substring(i + 1);
        String trace = callerClassName + ": " + callerMethodName + "() [" + lineNum + "] - ";

        return new Trace(trace, callerClassName);
    }

    public static class Trace
    {
        public String trace;
        public String clazz;

        public Trace(String trace, String clazz)
        {
            this.trace = trace;
            this.clazz = clazz;
        }
    }
}