// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SignedCurvature2DTest extends TestCase {
  public void testCounterClockwise() {
    Tensor a = Tensors.vector(1, 0);
    Tensor b = Tensors.vector(0, 1);
    Tensor c = Tensors.vector(-1, 0);
    assertEquals(Chop._10.of(SignedCurvature2D.of(a, b, c).get().add(RealScalar.ONE)), RealScalar.ZERO);
    assertEquals(Chop._10.of(SignedCurvature2D.of(c, b, a).get().subtract(RealScalar.ONE)), RealScalar.ZERO);
  }

  public void testStraight() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(2, 2);
    Tensor c = Tensors.vector(5, 5);
    assertEquals(SignedCurvature2D.of(a, b, c).get(), RealScalar.ZERO);
  }

  public void testSingular1() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(1, 1);
    Tensor c = Tensors.vector(2, 2);
    assertFalse(SignedCurvature2D.of(a, b, c).isPresent());
  }

  public void testSingular2() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(2, 2);
    Tensor c = Tensors.vector(1, 1);
    assertFalse(SignedCurvature2D.of(a, b, c).isPresent());
  }

  public void testSingular3() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(1, 1);
    Tensor c = Tensors.vector(1, 1);
    assertFalse(SignedCurvature2D.of(a, b, c).isPresent());
  }

  public void testQuantity() {
    Tensor a = Tensors.fromString("{1[m],0[m]}");
    Tensor b = Tensors.fromString("{0[m],1[m]}");
    Tensor c = Tensors.fromString("{-1[m],0[m]}");
    assertTrue(Chop._10.close(SignedCurvature2D.of(a, b, c).get(), Quantity.of(-1, "m^-1")));
    assertTrue(Chop._10.close(SignedCurvature2D.of(c, b, a).get(), Quantity.of(+1, "m^-1")));
  }

  public void testFail() {
    Tensor a = Tensors.vector(1, 1, 0);
    Tensor b = Tensors.vector(1, 2, 1);
    Tensor c = Tensors.vector(1, 3, 2);
    try {
      SignedCurvature2D.of(a, b, c);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testString() {
    Tensor points = Tensors.fromString("{{0,0},{1,1},{2,0}}");
    Tensor vector = SignedCurvature2D.string(points);
    Chop._12.requireClose(vector, Tensors.vector(1, 1, 1));
  }

  public void testStringEmpty() {
    Tensor points = Tensors.empty();
    Tensor vector = SignedCurvature2D.string(points);
    assertEquals(points, vector);
  }
}
