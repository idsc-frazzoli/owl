// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.owl.math.SymmetricVectorQ;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SmoothingKernelTest extends TestCase {
  private static Tensor constant(int i) {
    int width = 2 * i + 1;
    Scalar weight = RationalScalar.of(1, width);
    return Tensors.vector(k -> weight, width);
  }

  public void testConstant() {
    for (int width = 0; width < 5; ++width) {
      Tensor tensor = SmoothingKernel.DIRICHLET.apply(width);
      assertEquals(tensor, constant(width));
      assertTrue(ExactScalarQ.all(tensor));
      assertEquals(Total.of(tensor), RealScalar.ONE);
    }
  }

  public void testHann() {
    assertTrue(ExactScalarQ.all(SmoothingKernel.HANN.apply(1)));
    assertTrue(ExactScalarQ.all(SmoothingKernel.HANN.apply(2)));
  }

  public void testAll() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values())
      for (int size = 0; size < 5; ++size) {
        Tensor tensor = smoothingKernel.apply(size);
        SymmetricVectorQ.require(tensor);
        assertTrue(Chop._13.close(Total.of(tensor), RealScalar.ONE));
        assertFalse(Scalars.isZero(tensor.Get(0)));
        assertFalse(Scalars.isZero(tensor.Get(tensor.length() - 1)));
        assertEquals(tensor.length(), 2 * size + 1);
      }
  }

  public void testSymmetric() {
    for (int size = 0; size < 5; ++size) {
      Tensor tensor = RandomVariate.of(NormalDistribution.standard(), 2, 3, 4);
      for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
        Tensor v1 = tensor.map(smoothingKernel.windowFunction());
        Tensor v2 = tensor.negate().map(smoothingKernel.windowFunction());
        assertEquals(v1, v2);
      }
    }
  }

  public void testContinuity() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      Scalar scalar = smoothingKernel.windowFunction().apply(RationalScalar.HALF);
      String string = smoothingKernel.name().toLowerCase() + "Window[1/2]=" + scalar;
      string.length();
      assertEquals(smoothingKernel.apply(0), Tensors.of(RealScalar.ONE));
      Tensor vector = smoothingKernel.apply(1);
      assertTrue(Scalars.lessThan(RealScalar.of(1e-3), vector.Get(0).abs()));
    }
  }

  public void testAllFail() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values())
      try {
        smoothingKernel.apply(-1);
        fail();
      } catch (Exception exception) {
        // ---
      }
  }

  public void testAllFailQuantity() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values())
      try {
        smoothingKernel.windowFunction().apply(Quantity.of(1, "s"));
        fail();
      } catch (Exception exception) {
        // ---
      }
  }
}
