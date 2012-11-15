package couk.doridori.android.lib.testing;

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
            public void runTest() {
                try {
                    Thread.sleep(10000);
                    setResult(null, null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //use a shorter timeout
        FutureAsyncTestResult<Boolean> result = futureAsyncTest.get(100, TimeUnit.MILLISECONDS);
        assertNull(result);
    }

    public void testGet_asyncOperationQuickReturn_returnTrue() throws InterruptedException {
        final FutureAsyncTest<Boolean> futureAsyncTest = new FutureAsyncTest<Boolean>() {
            @Override
            public void runTest() {
                try {
                    Thread.sleep(200);
                    setResult(true, null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //use a longer timeout
        FutureAsyncTestResult<Boolean> result = futureAsyncTest.get(1500, TimeUnit.MILLISECONDS);
        assertTrue(result.result);
    }

}
