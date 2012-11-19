package couk.doridori.android.lib.testing;

import junit.framework.TestCase;

import java.util.concurrent.ExecutionException;
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
                    setResult(true);
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
                    setResult(true, new Exception("test"), true);
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
                    setResult(true);
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
                    setResult(new Exception("test"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //use a longer timeout
        FutureAsyncTestResult<Boolean> result = futureAsyncTester.get(100, TimeUnit.MILLISECONDS);
        assertNotNull(result.exception);
    }

    public void testGetWaitForever_asyncOpQuickReturn_returnsFalseResult() throws ExecutionException, InterruptedException {
        final FutureAsyncTester<Boolean> futureAsyncTester = new FutureAsyncTester<Boolean>() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    setResult(false, new Exception("test"), true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //use a longer timeout
        FutureAsyncTestResult<Boolean> result = futureAsyncTester.get();
        assertNotNull(result);
        assertFalse(result.result);
    }

    public void testGet_setShouldReturnFalse_returnAtMaxTime() throws InterruptedException {
        final FutureAsyncTester<Boolean> futureAsyncTester = new FutureAsyncTester<Boolean>() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1);
                    setResult(true, new Exception("test"), false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //use a longer timeout
        long startTime = System.currentTimeMillis();
        FutureAsyncTestResult<Boolean> result = futureAsyncTester.get(300, TimeUnit.MILLISECONDS);
        long finishTime = System.currentTimeMillis();
        assertTrue(finishTime-startTime >250 && finishTime-startTime<350);
        assertNotNull(result);
        assertTrue(result.result);
    }

}
