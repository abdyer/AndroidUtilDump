package couk.doridori.android.lib.testing;

import android.os.Handler;
import android.os.HandlerThread;
import org.apache.http.MethodNotSupportedException;

import java.util.concurrent.*;

/**
 * <p>Class for testing async methods. We cant use CountDownLatch of Futures as they are for the following reasons.</p>
 * <or>
 * <li>CountDownLatch - this will basically just cause the main thread to wait - this is not that useful for Android style async callbacks which generally seem to be initiated via Loopers / Handlers and get called on the main thread anyway. The thread would just wait and then the callback would come after the assert</li>
 * <li>FutureTask - this needs the Runnable / Callable thats passed in to return a result / exception. When the run or call method exits any threads waiting on the FutureRunnable will be released - not good for asyncrounous calls with callbacks on the same thread!</li>
 * </or>
 * <p>What this class will do utilise Loopers and Latches to provide a simple to use class for testing those async calls Android style!</p>
 * <p>While its common to mock out a lot of these types of calls I want to test a remote api with multiple user types and a class that does this is very much needed for me!</p>
 *
 * <b>From API level 9 onwards - shouldnt matter as TEST proj only</b>
 */
public abstract class FutureAsyncTest<T> extends HandlerThread implements RunnableFuture<FutureAsyncTestResult<T>> {

    private FutureAsyncTestResult mResult;
    private CountDownLatch mCountDownLatch;
    /**
     * To communicate with the worker looper
     */
    private Handler mHandler;
    private boolean mHasStarted = false;

    public FutureAsyncTest() {
        super(FutureAsyncTest.class.getName());
        mCountDownLatch = new CountDownLatch(1);
    }

    @Override
    public synchronized void start() {
        throw new RuntimeException(new MethodNotSupportedException("not supported - super.start() is called in the get() methods"));
    }

    @Override
    public final void run() {
        super.run(); //start looper

        if(mHasStarted)
            throw new RuntimeException("can only call run() and start ONCE!");

        mHandler = new Handler(getLooper());
        mHandler.post(this);
    }

    /**
     * Override this and call setResult() methods in your async callbacks
     */
    public abstract void runTest();

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new RuntimeException(new MethodNotSupportedException("not supported - dont need for testing!"));
    }

    @Override
    public boolean isCancelled() {
        throw new RuntimeException(new MethodNotSupportedException("not supported - dont need for testing!"));
    }

    @Override
    public boolean isDone() {
        return null != mResult;
    }

    @Override
    public FutureAsyncTestResult<T> get() throws InterruptedException, ExecutionException {
        super.start();
        mCountDownLatch.await();
        return mResult;
    }

    @Override
    public FutureAsyncTestResult<T> get(long timeout, TimeUnit unit) throws InterruptedException {
        super.start();
        mCountDownLatch.await(timeout, unit);
        return mResult;
    }

    public synchronized void setResult(FutureAsyncTestResult result){
        mResult = result;
        mCountDownLatch.countDown();
    }
}