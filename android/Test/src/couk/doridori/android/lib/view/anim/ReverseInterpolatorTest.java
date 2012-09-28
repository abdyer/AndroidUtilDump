package couk.doridori.android.lib.view.anim;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * User: doriancussen
 * Date: 28/09/2012
 */
public class ReverseInterpolatorTest extends TestCase {
    public void testValues(){
        Interpolator interpolator = new ReverseInterpolator(new LinearInterpolator());

        Assert.assertEquals("",interpolator.getInterpolation(0), 0.0f);
        Assert.assertEquals("",interpolator.getInterpolation(0.5f), 1.0f);
        Assert.assertEquals("",interpolator.getInterpolation(1), 0.0f);
        Assert.assertEquals("", interpolator.getInterpolation(0.25f), 0.5f);
        Assert.assertEquals("", interpolator.getInterpolation(0.75f), 0.5f);
    }
}
