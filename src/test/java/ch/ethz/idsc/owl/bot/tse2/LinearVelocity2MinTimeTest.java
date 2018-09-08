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
    LinearVelocity2MinTime linearVelocityMinTime = new LinearVelocity2MinTime(v_max, a_max);
    Scalar timeToV_max = linearVelocityMinTime.timeToV_max(Quantity.of(2, "m*s^-1"));
    assertEquals(timeToV_max, Quantity.of(8, "s"));
    Scalar minDistToV_max = linearVelocityMinTime.minDistToV_max(Quantity.of(2, "m*s^-1"));
    assertEquals(minDistToV_max, Quantity.of(16, "m"));
    Scalar minTime = linearVelocityMinTime.minTime(Quantity.of(100, "m"), Quantity.of(2, "m*s^-1"));
    assertTrue(Scalars.lessEquals(minTime, Scalars.fromString("82/5[s]")));
    // System.out.println(minTime);
  }
}
