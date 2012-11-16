package couk.doridori.android.lib.testing;

import junit.framework.TestCase;

import java.util.concurrent.TimeUnit;

/**
 * User: doriancussen
 * Date: 15/11/2012
 */
public class FutureAsyncTesterTest extends TestCase {

    public void testGet_asyncOperationTooLong_returnNullResult() throws InterruptedException {
        final FutureAsyncTester<Boolean> futureAsyncTester = new FutureAsyncTester<Boolean>() {
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
        FutureAsyncTestResult<Boolean> resultWrapper = futureAsyncTester.get(500, TimeUnit.MILLISECONDS);
        assertNull(resultWrapper);
    }

    public void testGet_asyncOperationTooLong_returnNullException() throws InterruptedException {
        final FutureAsyncTester<Boolean> futureAsyncTester = new FutureAsyncTester<Boolean>() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    setResult(true, new Exception("test"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //use a shorter timeout
        FutureAsyncTestResult<Boolean> resultWrapper = futureAsyncTester.get(500, TimeUnit.MILLISECONDS);
        assertNull(resultWrapper);
    }

    public void testGet_asyncOperationQuickReturn_returnTrue() throws InterruptedException {
        final FutureAsyncTester<Boolean> futureAsyncTester = new FutureAsyncTester<Boolean>() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1);
                    setResult(true, null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //use a longer timeout
        FutureAsyncTestResult<Boolean> result = futureAsyncTester.get(100, TimeUnit.MILLISECONDS);
        assertTrue(result.result);
    }


    public void testGet_asyncOperationQuickReturn_returnException() throws InterruptedException {
        final FutureAsyncTester<Boolean> futureAsyncTester = new FutureAsyncTester<Boolean>() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1);
                    setResult(null, new Exception("test"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //use a longer timeout
        FutureAsyncTestResult<Boolean> result = futureAsyncTester.get(100, TimeUnit.MILLISECONDS);
        assertNotNull(result.exception);
    }

}
