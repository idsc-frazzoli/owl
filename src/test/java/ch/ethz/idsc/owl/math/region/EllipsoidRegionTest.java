// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class EllipsoidRegionTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(1, 1));
    assertTrue(region.isMember(Tensors.vector(10, 5)));
    assertTrue(region.isMember(Tensors.vector(10, 5.5)));
    assertTrue(region.isMember(Tensors.vector(10, 6)));
    assertFalse(region.isMember(Tensors.vector(10, 6.5)));
  }

  public void testSimple2() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(2, 2));
    assertTrue(region.isMember(Tensors.vector(10, 5)));
    assertTrue(region.isMember(Tensors.vector(10, 5.5)));
    assertTrue(region.isMember(Tensors.vector(10, 7)));
    assertTrue(region.isMember(Tensors.vector(12, 5)));
    assertTrue(region.isMember(Tensors.vector(11.2, 6.2)));
    assertFalse(region.isMember(Tensors.vector(10, 7.1)));
    assertFalse(region.isMember(Tensors.vector(10, 7.5)));
  }

  public void testEllipsoid() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(2, 1));
    assertTrue(region.isMember(Tensors.vector(10, 5)));
    assertTrue(region.isMember(Tensors.vector(10, 5.5)));
    assertFalse(region.isMember(Tensors.vector(10, 7)));
    assertTrue(region.isMember(Tensors.vector(12, 5)));
    assertFalse(region.isMember(Tensors.vector(12.1, 5)));
    assertFalse(region.isMember(Tensors.vector(11.2, 6.2)));
    assertFalse(region.isMember(Tensors.vector(10, 6.1)));
    assertFalse(region.isMember(Tensors.vector(10, 7.5)));
  }

  public void testInfty() {
    ImplicitFunctionRegion ifr = new EllipsoidRegion(Tensors.vector(5, 10), Tensors.vector(1 / 0.0, 2));
    assertEquals(ifr.apply(Tensors.vector(1000, 8)), RealScalar.ZERO);
  }

  public void test1D() {
    ImplicitFunctionRegion ifr = new EllipsoidRegion(Tensors.vector(10), Tensors.vector(2));
    assertEquals(ifr.apply(Tensors.vector(8)), RealScalar.ZERO);
  }

  public void testLengthFail() {
    try {
      new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, 0, 3));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNegativeFail() {
    try {
      new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, -2));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testZeroFail() {
    try {
      new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, 0));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      new EllipsoidRegion(Tensors.vector(10, 2, 3), Tensors.vector(1, 0.0, 3));
    } catch (Exception exception) {
      // ---
    }
  }
}
