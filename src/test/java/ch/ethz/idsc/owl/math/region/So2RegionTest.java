// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.qty.Unit;
import junit.framework.TestCase;

public class So2RegionTest extends TestCase {
  public void testSimple() {
    So2Region ifr = So2Region.periodic(RealScalar.ONE, RealScalar.ONE);
    assertEquals(ifr.signedDistance(RealScalar.of(2)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(1)), RealScalar.of(-1));
    assertEquals(ifr.distance(RealScalar.of(1)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(-1)), RealScalar.of(1));
    assertEquals(ifr.signedDistance(RealScalar.of(-2)), RealScalar.of(2));
  }

  public void testSimple2PI() {
    ImplicitFunctionRegion ifr = So2Region.periodic(RealScalar.ONE, RealScalar.ONE);
    double pi2 = Math.PI * 2;
    assertEquals(ifr.signedDistance(RealScalar.of(2 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(1 + pi2)), RealScalar.of(-1));
    assertEquals(ifr.signedDistance(RealScalar.of(0 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(-1 + pi2)), RealScalar.of(1));
    assertEquals(ifr.signedDistance(RealScalar.of(-2 + pi2)), RealScalar.of(2));
  }

  public void testSimpleN2PI() {
    ImplicitFunctionRegion ifr = So2Region.periodic(RealScalar.ONE, RealScalar.ONE);
    double pi2 = -Math.PI * 2;
    assertEquals(ifr.signedDistance(RealScalar.of(2 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(1 + pi2)), RealScalar.of(-1));
    assertEquals(ifr.signedDistance(RealScalar.of(0 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(-1 + pi2)), RealScalar.of(1));
    assertEquals(ifr.signedDistance(RealScalar.of(-2 + pi2)), RealScalar.of(2));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    So2Region so2Region = So2Region.periodic(RealScalar.ONE, RealScalar.of(0.2));
    assertEquals(so2Region.center(), RealScalar.ONE);
    assertEquals(so2Region.radius(), RealScalar.of(0.2));
    So2Region copy = Serialization.copy(so2Region);
    assertEquals(copy.center(), RealScalar.ONE);
    assertEquals(copy.radius(), RealScalar.of(0.2));
  }

  public void testUnits() {
    Scalar maxTurning = Quantity.of(6, "rad*s^-1");
    So2Region so2Region = new So2Region(Quantity.of(2, "rad"), Quantity.of(1, "rad"), Quantity.of(Math.PI, "rad"));
    Scalar duration = so2Region.signedDistance(Quantity.of(4, "rad")).divide(maxTurning);
    assertEquals(QuantityUnit.of(duration), Unit.of("s"));
  }

  public void testFail() {
    try {
      So2Region.periodic(RealScalar.of(2), RealScalar.of(-1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
