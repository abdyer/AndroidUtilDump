package couk.doridori.android.lib.testing;

public class FutureAsyncTestResult<T>{

    public final T result;
    public final Exception exception;

    public FutureAsyncTestResult(T result, Exception exception) {
        this.result = result;
        this.exception = exception;
    }
}