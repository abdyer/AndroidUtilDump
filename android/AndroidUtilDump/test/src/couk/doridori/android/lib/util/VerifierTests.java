package couk.doridori.android.lib.util;

import junit.framework.TestCase;

/**
 * @author Dorian Cussen
 *         Date: 14/12/2012
 */
public class VerifierTests extends TestCase
{
    public void testIsAllNotNull_oneNull_returnFalse()
    {
        Object[] array = {"one", "two", null, "four"};
        assertFalse(Verifier.isAllNotNull(array));
    }

    public void testIsAllNotNull_nonNull_returnTrue()
    {
        Object[] array = {"one", "two", "three", "four"};
        assertTrue(Verifier.isAllNotNull(array));
    }

    public void testThrowIfAllNull_allNull_shouldThrowNPE()
    {
        Object[] nullArray = {null, null, null, null};
        try
        {
            Verifier.throwIfAllNull(nullArray);
            fail();
        }
        catch(NullPointerException e)
        {
             assertTrue(true);
        }
    }

    public void testThrowIfAllNull_emptyInput_shouldThrowNPE()
    {
        Object[] nullArray = {};
        try
        {
            Verifier.throwIfAllNull(nullArray);
            fail();
        }
        catch(NullPointerException e)
        {
            assertTrue(true);
        }
    }

    public void testThrowIfAllNull_notAllNull_shouldReturn()
    {
        Object[] nullArray = {null, null, null, new Object()};
        try
        {
            Verifier.throwIfAllNull(nullArray);
            assertTrue(true);
        }
        catch(NullPointerException e)
        {
            fail();
        }
    }


}
