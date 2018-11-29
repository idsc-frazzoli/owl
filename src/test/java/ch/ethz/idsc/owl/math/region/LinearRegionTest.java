// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class LinearRegionTest extends TestCase {
  public void testSimple() {
    LinearRegion linearRegion = new LinearRegion(RealScalar.of(10), RealScalar.of(2));
    assertEquals(linearRegion.center(), RealScalar.of(10));
    assertEquals(linearRegion.radius(), RealScalar.of(2));
    assertEquals(linearRegion.signedDistance(RealScalar.of(7)), RealScalar.of(1));
    assertEquals(linearRegion.signedDistance(RealScalar.of(9)), RealScalar.of(-1));
    assertEquals(linearRegion.signedDistance(RealScalar.of(10)), RealScalar.of(-2));
    assertEquals(linearRegion.signedDistance(RealScalar.of(11)), RealScalar.of(-1));
    assertTrue(ExactScalarQ.of(linearRegion.signedDistance(RealScalar.of(11))));
    assertEquals(linearRegion.distance(RealScalar.of(7)), RealScalar.of(1));
    assertEquals(linearRegion.distance(RealScalar.of(9)), RealScalar.of(0));
    assertEquals(linearRegion.distance(RealScalar.of(10)), RealScalar.of(0));
    assertEquals(linearRegion.distance(RealScalar.of(11)), RealScalar.of(0));
    assertTrue(ExactScalarQ.of(linearRegion.distance(RealScalar.of(11))));
  }

  public void testQuantity() {
    LinearRegion linearRegion = new LinearRegion(Quantity.of(10, "m"), Quantity.of(2, "m"));
    assertEquals(linearRegion.signedDistance(Quantity.of(7, "m")), Quantity.of(1, "m"));
    assertEquals(linearRegion.signedDistance(Quantity.of(9, "m")), Quantity.of(-1, "m"));
    assertTrue(ExactScalarQ.of(linearRegion.distance(Quantity.of(11, "m"))));
  }

  public void testClip() {
    LinearRegion linearRegion = new LinearRegion(Quantity.of(10, "m"), Quantity.of(2, "m"));
    Clip clip = linearRegion.clip();
    assertEquals(clip.min(), Quantity.of(8, "m"));
    assertEquals(clip.max(), Quantity.of(12, "m"));
  }

  public void testFail() {
    try {
      new LinearRegion(null, RealScalar.of(2));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      new LinearRegion(RealScalar.of(2), RealScalar.of(-3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
