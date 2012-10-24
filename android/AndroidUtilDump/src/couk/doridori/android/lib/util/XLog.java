package couk.doridori.android.lib.util;

import android.util.Log;

public class XLog {

	private static final int NONE = 0;
	private static final int ERRORS_ONLY = 1;
	private static final int ERRORS_WARNINGS = 2;
	private static final int ERRORS_WARNINGS_INFO = 3;
	private static final int ERRORS_WARNINGS_INFO_DEBUG = 4;
	private static final int ALL = 5;

	private static final int LOGGING_LEVEL_FILTER = ALL;

	private static final String TAG_NO_FILTER = "XLog";

	static {
		Log.i(TAG_NO_FILTER, "ILog class reloaded");
	}

	public static void e(String text, Exception e) {

		e(text + " : " + e.getClass().getName() + ": " + e.getMessage());
	}

	public static void e(String text) {

		if (LOGGING_LEVEL_FILTER > NONE) {
			Log.e(TAG_NO_FILTER, getTrace() + text);
		}
	}

	public static void w(String text) {

		if (LOGGING_LEVEL_FILTER > ERRORS_ONLY) {
			Log.w(TAG_NO_FILTER, getTrace() + text);
		}
	}

	public static void i(String text) {

		if (LOGGING_LEVEL_FILTER > ERRORS_WARNINGS) {
			Log.i(TAG_NO_FILTER, getTrace() + text);
		}
	}

	public static void d() {

		if (LOGGING_LEVEL_FILTER > ERRORS_WARNINGS_INFO) {
			Log.d(TAG_NO_FILTER, getTrace() + "<method>");
		}
	}
	
	public static void d(Object obj){
		if (LOGGING_LEVEL_FILTER > ERRORS_WARNINGS_INFO) {
			Log.d(obj.toString(), getTrace());
		}
	}

	public static void d(String text) {

		if (LOGGING_LEVEL_FILTER > ERRORS_WARNINGS_INFO) {
			Log.d(TAG_NO_FILTER, getTrace() + text);
		}
	}

	public static void d(String tag, String text) {

		if (LOGGING_LEVEL_FILTER > ERRORS_WARNINGS_INFO) {
			Log.d(tag, getTrace() + text);
		}
	}
	
	public static void d(Object obj, String text) {

		if (LOGGING_LEVEL_FILTER > ERRORS_WARNINGS_INFO) {
			Log.d(obj.getClass().getName(), getTrace() + text);
		}
	}


	public static void v(String text) {

		if (LOGGING_LEVEL_FILTER > ERRORS_WARNINGS_INFO_DEBUG) {
			Log.v(TAG_NO_FILTER, getTrace() + text);
		}
	}

	private static String getTrace() {

		int depth = 2;
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		String callerMethodName = elements[depth].getMethodName();
		String callerClassPath = elements[depth].getClassName();
		int lineNum = elements[depth].getLineNumber();
		int i = callerClassPath.lastIndexOf('.');
		String callerClassName = callerClassPath.substring(i + 1);
		String trace = callerClassName + ": " + callerMethodName + "() [" + lineNum + "] - ";
		return trace;
	}
}