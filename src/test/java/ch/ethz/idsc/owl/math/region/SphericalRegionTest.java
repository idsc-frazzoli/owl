// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SphericalRegionTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = new SphericalRegion(Tensors.vector(1, 1), RealScalar.ONE);
    assertTrue(region.isMember(Tensors.vector(1, 0)));
    assertTrue(region.isMember(Tensors.vector(0, 1)));
    assertFalse(region.isMember(Tensors.vector(2, 0)));
    assertFalse(region.isMember(Tensors.vector(0, 2)));
  }

  public void testPoint() {
    Region<Tensor> region = new SphericalRegion(Tensors.vector(1, 1), RealScalar.ZERO);
    assertTrue(region.isMember(Tensors.vector(1, 1)));
  }

  public void testDistance() {
    SphericalRegion region = new SphericalRegion(Tensors.vector(1, 1), RealScalar.ZERO);
    assertEquals(region.signedDistance(Tensors.vector(11, 1)), RealScalar.of(10));
  }

  public void testCenter() {
    SphericalRegion sr = new SphericalRegion(Tensors.vector(1, 2), RealScalar.of(5));
    assertEquals(sr.center(), Tensors.vector(1, 2));
    assertEquals(sr.radius(), RealScalar.of(5));
  }

  public void testQuantity() {
    RegionWithDistance<Tensor> regionWithDistance = new SphericalRegion(Tensors.fromString("{10[m],20[m]}"), Quantity.of(5, "m"));
    assertTrue(regionWithDistance.isMember(Tensors.fromString("{11[m],19[m]}")));
    Scalar scalar = regionWithDistance.distance(Tensors.fromString("{10[m],0[m]}"));
    assertEquals(scalar, Quantity.of(15, "m"));
    assertTrue(ExactScalarQ.of(scalar));
  }

  public void testSignedDistance() {
    ImplicitFunctionRegion<Tensor> implicitFunctionRegion = new SphericalRegion(Tensors.fromString("{10[m],20[m]}"), Quantity.of(5, "m"));
    assertTrue(implicitFunctionRegion.isMember(Tensors.fromString("{11[m],19[m]}")));
    Scalar scalar = implicitFunctionRegion.signedDistance(Tensors.fromString("{11[m],20[m]}"));
    assertEquals(scalar, Quantity.of(-4, "m"));
    assertTrue(ExactScalarQ.of(scalar));
  }

  public void testFail() {
    try {
      new SphericalRegion(RealScalar.ZERO, RealScalar.ONE);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      new SphericalRegion(Tensors.vector(1, 2), RealScalar.ONE.negate());
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
