// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class So2RegionTest extends TestCase {
  public void testSimple() {
    So2Region ifr = new So2Region(RealScalar.ONE, RealScalar.ONE);
    assertEquals(ifr.signedDistance(RealScalar.of(2)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(1)), RealScalar.of(-1));
    assertEquals(ifr.distance(RealScalar.of(1)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(-1)), RealScalar.of(1));
    assertEquals(ifr.signedDistance(RealScalar.of(-2)), RealScalar.of(2));
  }

  public void testSimple2PI() {
    ImplicitFunctionRegion<Tensor> ifr = new So2Region(RealScalar.ONE, RealScalar.ONE);
    double pi2 = Math.PI * 2;
    assertEquals(ifr.signedDistance(RealScalar.of(2 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(1 + pi2)), RealScalar.of(-1));
    assertEquals(ifr.signedDistance(RealScalar.of(0 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(-1 + pi2)), RealScalar.of(1));
    assertEquals(ifr.signedDistance(RealScalar.of(-2 + pi2)), RealScalar.of(2));
  }

  public void testSimpleN2PI() {
    ImplicitFunctionRegion<Tensor> ifr = new So2Region(RealScalar.ONE, RealScalar.ONE);
    double pi2 = -Math.PI * 2;
    assertEquals(ifr.signedDistance(RealScalar.of(2 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(1 + pi2)), RealScalar.of(-1));
    assertEquals(ifr.signedDistance(RealScalar.of(0 + pi2)), RealScalar.ZERO);
    assertEquals(ifr.signedDistance(RealScalar.of(-1 + pi2)), RealScalar.of(1));
    assertEquals(ifr.signedDistance(RealScalar.of(-2 + pi2)), RealScalar.of(2));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    ImplicitFunctionRegion<Tensor> ifr = new So2Region(RealScalar.ONE, RealScalar.ONE);
    Serialization.copy(ifr);
  }
}
