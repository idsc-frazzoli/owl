// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LinearVelocity2MinTimeTest extends TestCase {
  public void testSimple() {
    Scalar v_max = Quantity.of(10, "m*s^-1");
    Scalar a_max = Quantity.of(1, "m*s^-2");
    LinearVelocity2MinTime linearVelocity2MinTime = new LinearVelocity2MinTime(v_max, a_max);
    Scalar v_cur = Quantity.of(2, "m*s^-1");
    TimeDistPair timeDistPair = linearVelocity2MinTime.timeDistToV_max(v_cur);
    assertEquals(timeDistPair.time, Quantity.of(8, "s"));
    assertEquals(timeDistPair.dist, Quantity.of(48, "m"));
    Scalar minTime = linearVelocity2MinTime.minTime(Quantity.of(12, "m"), v_cur);
    Chop._10.requireClose(minTime, Quantity.of(3.2915026221291814, "s"));
    // ---
    minTime = linearVelocity2MinTime.minTime(Quantity.of(100, "m"), Quantity.of(2, "m*s^-1"));
    assertTrue(Scalars.lessEquals(minTime, Scalars.fromString("82/5[s]")));
    assertTrue(Scalars.lessEquals(minTime, Scalars.fromString("66/5[s]")));
  }

  public void testMore() {
    Scalar v_max = Quantity.of(10, "m*s^-1");
    Scalar a_max = Quantity.of(3, "m*s^-2");
    LinearVelocity2MinTime linearVelocity2MinTime = new LinearVelocity2MinTime(v_max, a_max);
    TimeDistPair timeToV_max = linearVelocity2MinTime.timeDistToV_max(Quantity.of(2, "m*s^-1"));
    assertEquals(timeToV_max.time, Scalars.fromString("8/3[s]"));
    assertEquals(timeToV_max.dist, Scalars.fromString("16[m]"));
    Scalar minTime = linearVelocity2MinTime.minTime(Quantity.of(100, "m"), Quantity.of(2, "m*s^-1"));
    assertEquals(minTime, Scalars.fromString("166/15[s]"));
  }

  public void testExactDist() {
    Scalar v_max = Quantity.of(10, "m*s^-1");
    Scalar a_max = Quantity.of(2, "m*s^-2");
    LinearVelocity2MinTime linearVelocity2MinTime = new LinearVelocity2MinTime(v_max, a_max);
    {
      Scalar v_cur = Quantity.of(0, "m*s^-1");
      Scalar dist = linearVelocity2MinTime.timeDistToV_max(v_cur).dist;
      assertEquals(dist, Quantity.of(25, "m"));
      assertTrue(ExactScalarQ.of(dist));
      Scalar time = linearVelocity2MinTime.minTime(Quantity.of(50, "m"), v_cur);
      assertEquals(time, Quantity.of(7.5, "s"));
      assertTrue(ExactScalarQ.of(time));
    }
    {
      Scalar v_cur = Quantity.of(5, "m*s^-1");
      Scalar dist = linearVelocity2MinTime.timeDistToV_max(v_cur).dist;
      assertEquals(dist, Quantity.of(18.75, "m"));
      Scalar time = linearVelocity2MinTime.minTime(Quantity.of(10, "m"), v_cur);
      Chop._12.requireClose(time, Quantity.of(1.5311288741492746, "s"));
    }
    {
      Scalar v_cur = Quantity.of(10, "m*s^-1");
      Scalar dist = linearVelocity2MinTime.timeDistToV_max(v_cur).dist;
      assertEquals(dist, Quantity.of(0, "m"));
      Scalar time = linearVelocity2MinTime.minTime(Quantity.of(0, "m"), v_cur);
      assertEquals(time, Quantity.of(0, "s"));
      assertTrue(ExactScalarQ.of(time));
    }
  }

  public void testFail() {
    Scalar v_max = Quantity.of(10, "m*s^-1");
    Scalar a_max = Quantity.of(2, "m*s^-2");
    LinearVelocity2MinTime linearVelocity2MinTime = new LinearVelocity2MinTime(v_max, a_max);
    try {
      linearVelocity2MinTime.timeDistToV_max(Quantity.of(11, "m*s^-1"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
