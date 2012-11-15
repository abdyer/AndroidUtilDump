package couk.doridori.android.lib.testing;

import couk.doridori.android.lib.util.XLog;
import junit.framework.TestCase;

import java.util.concurrent.TimeUnit;

/**
 * User: doriancussen
 * Date: 15/11/2012
 */
public class FutureAsyncTestTester extends TestCase {

    public void testGet_asyncOperationTooLong_returnNull() throws InterruptedException {
        final FutureAsyncTest<Boolean> futureAsyncTest = new FutureAsyncTest<Boolean>() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    setResult(true, null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //use a shorter timeout
        FutureAsyncTestResult<Boolean> resultWrapper = futureAsyncTest.get(500, TimeUnit.MILLISECONDS);
        assertNull(resultWrapper);
    }

    public void testGet_asyncOperationQuickReturn_returnTrue() throws InterruptedException {
        final FutureAsyncTest<Boolean> futureAsyncTest = new FutureAsyncTest<Boolean>() {
            @Override
            public void run() {
                try {
                    XLog.d();
                     Thread.sleep(1);
                    XLog.d();
                    setResult(true, null);
                    XLog.d();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //use a longer timeout
        XLog.d(Thread.currentThread().getName());
        FutureAsyncTestResult<Boolean> result = futureAsyncTest.get(100, TimeUnit.MILLISECONDS);
        XLog.d();
        assertTrue(result.result);
    }

}
