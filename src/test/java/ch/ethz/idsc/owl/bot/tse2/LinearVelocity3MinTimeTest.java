// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LinearVelocity3MinTimeTest extends TestCase {
  public void testSimple() {
    Scalar v_max = Quantity.of(10, "m*s^-1");
    Scalar a_max = Quantity.of(1, "m*s^-2");
    Scalar v_tar = Quantity.of(3, "m*s^-1");
    LinearVelocity3MinTime linearVelocityMinTime = new LinearVelocity3MinTime(v_max, a_max, v_tar);
    Scalar timeToV_max = linearVelocityMinTime.timeToV_max(Quantity.of(2, "m*s^-1"));
    assertEquals(timeToV_max, Quantity.of(8, "s"));
    Scalar minDistToV_max = linearVelocityMinTime.minDistToV_max(Quantity.of(2, "m*s^-1"));
    assertEquals(minDistToV_max, Quantity.of(16, "m"));
  }
}
