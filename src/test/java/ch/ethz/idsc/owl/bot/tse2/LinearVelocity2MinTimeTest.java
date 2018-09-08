// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LinearVelocity2MinTimeTest extends TestCase {
  public void testSimple() {
    Scalar v_max = Quantity.of(10, "m*s^-1");
    Scalar a_max = Quantity.of(1, "m*s^-2");
    LinearVelocity2MinTime linearVelocity2MinTime = new LinearVelocity2MinTime(v_max, a_max);
    Scalar timeToV_max = linearVelocity2MinTime.exactTimeToV_max(Quantity.of(2, "m*s^-1"));
    assertEquals(timeToV_max, Quantity.of(8, "s"));
    Scalar minDistToV_max = linearVelocity2MinTime.minDistToV_max(Quantity.of(2, "m*s^-1"));
    assertEquals(minDistToV_max, Quantity.of(16, "m"));
    Scalar minTime = linearVelocity2MinTime.minTime(Quantity.of(100, "m"), Quantity.of(2, "m*s^-1"));
    assertTrue(Scalars.lessEquals(minTime, Scalars.fromString("82/5[s]")));
    linearVelocity2MinTime.exactDistToV_max(Quantity.of(2, "m*s^-1"));
    // System.out.println(minTime);
  }

  public void testExactDist() {
    Scalar v_max = Quantity.of(10, "m*s^-1");
    Scalar a_max = Quantity.of(2, "m*s^-2");
    LinearVelocity2MinTime linearVelocity2MinTime = new LinearVelocity2MinTime(v_max, a_max);
    {
      Scalar v_cur = Quantity.of(0, "m*s^-1");
      Scalar dist = linearVelocity2MinTime.exactDistToV_max(v_cur);
      assertEquals(dist, Quantity.of(25, "m"));
      Scalar minD = linearVelocity2MinTime.minDistToV_max(v_cur);
      assertTrue(Scalars.lessEquals(minD, dist));
    }
    {
      Scalar v_cur = Quantity.of(5, "m*s^-1");
      Scalar dist = linearVelocity2MinTime.exactDistToV_max(v_cur);
      assertEquals(dist, Quantity.of(18.75, "m"));
      Scalar minD = linearVelocity2MinTime.minDistToV_max(v_cur);
      assertTrue(Scalars.lessEquals(minD, dist));
    }
    {
      Scalar v_cur = Quantity.of(10, "m*s^-1");
      Scalar dist = linearVelocity2MinTime.exactDistToV_max(v_cur);
      assertEquals(dist, Quantity.of(0, "m"));
      Scalar minD = linearVelocity2MinTime.minDistToV_max(v_cur);
      assertTrue(Scalars.lessEquals(minD, dist));
    }
  }
}
