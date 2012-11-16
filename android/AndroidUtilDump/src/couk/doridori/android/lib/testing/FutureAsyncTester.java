package couk.doridori.android.lib.testing;

import android.os.Handler;
import android.os.HandlerThread;
import org.apache.http.MethodNotSupportedException;

import java.util.concurrent.*;

/**
 * <p>Class for testing async methods. We cant use CountDownLatch or Futures individually for the following reasons.</p>
 * <or>
 * <li>CountDownLatch - this will basically just cause the main thread to wait - this is not that useful for Android style async callbacks which generally seem to be initiated via Loopers / Handlers and get called on the main thread anyway. The thread would just wait and then the callback would come after the assert</li>
 * <li>FutureTask - this needs the Runnable / Callable thats passed in to return a result / exception. When the run or call method exits any threads waiting on the FutureRunnable will be released - not good for asynchronous calls with callbacks on the same thread!</li>
 * </or>
 * <p>What this class will do utilise Loopers (via HandlerThread) and Latches to provide a simple-to-use class for testing those async calls Android style! The use of HandlerThread means that the thread wont die strait after calling the .run() method and wont just close after a passed in Runnables.run() method is called (which would be useless for async). Also it means any handlers that are created by the called async methods can communicate back to this worker thread. If we did this using a thread without a looper this aspect would not work.</p>
 * <p>While its common to mock out a lot of these types of calls I want to test a remote api with multiple user types and a class that does this is very much needed for me!</p>
 *
 * <b>From API level 9 onwards - shouldnt matter as TEST proj only</b>
 *
 * <p>Check out the test methods for this class in the Test project to see how you would use it</p>
 */
public abstract class FutureAsyncTester<T> implements RunnableFuture<FutureAsyncTestResult<T>> {

    private HandlerThread mHandlerThread;
    private FutureAsyncTestResult<T> mResult;
    /**
     * The countDownLatch here is just used for blocking behaviour (with an optional timeout setting) to support the RunnableFuture inherited get() methods
     */
    private CountDownLatch mCountDownLatch;
    /**
     * To communicate with the worker looper
     */
    private Handler mHandler;
    private boolean mHasStarted = false;

    public FutureAsyncTester() {
        mCountDownLatch = new CountDownLatch(1);
        mHandlerThread = new HandlerThread(FutureAsyncTester.class.getName());

    }

    public void start(){
        if(mHasStarted)
            throw new RuntimeException("only call start() once");

        mHasStarted = true;

        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandler.post(this);
    }

    /**
     * Override this and call setResult() methods in your async callbacks
     */
    @Override
    public abstract void run();

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

    /**
     * Will call start() and therefore internally call {@link #run()}
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public FutureAsyncTestResult<T> get() throws InterruptedException, ExecutionException {
        start();
        mCountDownLatch.await();
        return mResult;
    }

    /**
     * Will call start() and therefore internally call {@link #run()}
     *
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public FutureAsyncTestResult<T> get(long timeout, TimeUnit unit) throws InterruptedException {
        start();
        mCountDownLatch.await(timeout, unit);
        return mResult;
    }

    /**
     * This should be called from inside your overridden {@link #run()} implementation. These args will be wrapped
     * and passed back from your get() method.
     *
     * @param result if successful set a result
     * @param e if an exception is caught set this instead (marking failure)
     */
    public synchronized void setResult(T result, Exception e){
        mResult = new FutureAsyncTestResult<T>(result, e);
        mCountDownLatch.countDown();
        mHandlerThread.quit();
    }
}