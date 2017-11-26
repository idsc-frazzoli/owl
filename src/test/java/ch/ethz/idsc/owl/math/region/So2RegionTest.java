// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class So2RegionTest extends TestCase {
  public void testSimple() {
    ImplicitFunctionRegion ifr = new So2Region(RealScalar.ONE, RealScalar.ONE);
    assertEquals(ifr.apply(RealScalar.of(2)), RealScalar.ZERO);
    assertEquals(ifr.apply(RealScalar.of(1)), RealScalar.of(-1));
    assertEquals(ifr.apply(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(ifr.apply(RealScalar.of(-1)), RealScalar.of(1));
    assertEquals(ifr.apply(RealScalar.of(-2)), RealScalar.of(2));
  }

  public void testSimple2PI() {
    ImplicitFunctionRegion ifr = new So2Region(RealScalar.ONE, RealScalar.ONE);
    double pi2 = Math.PI * 2;
    assertEquals(ifr.apply(RealScalar.of(2 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.apply(RealScalar.of(1 + pi2)), RealScalar.of(-1));
    assertEquals(ifr.apply(RealScalar.of(0 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.apply(RealScalar.of(-1 + pi2)), RealScalar.of(1));
    assertEquals(ifr.apply(RealScalar.of(-2 + pi2)), RealScalar.of(2));
  }

  public void testSimpleN2PI() {
    ImplicitFunctionRegion ifr = new So2Region(RealScalar.ONE, RealScalar.ONE);
    double pi2 = -Math.PI * 2;
    assertEquals(ifr.apply(RealScalar.of(2 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.apply(RealScalar.of(1 + pi2)), RealScalar.of(-1));
    assertEquals(ifr.apply(RealScalar.of(0 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.apply(RealScalar.of(-1 + pi2)), RealScalar.of(1));
    assertEquals(ifr.apply(RealScalar.of(-2 + pi2)), RealScalar.of(2));
  }
}
