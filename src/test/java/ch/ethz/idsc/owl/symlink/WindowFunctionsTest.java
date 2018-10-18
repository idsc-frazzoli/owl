// code by jph
package ch.ethz.idsc.owl.symlink;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sig.SymmetricVectorQ;
import junit.framework.TestCase;

public class WindowFunctionsTest extends TestCase {
  private static Tensor constant(int i) {
    int width = 2 * i + 1;
    Scalar weight = RationalScalar.of(1, width);
    return Tensors.vector(k -> weight, width);
  }

  public void testConstant() {
    for (int width = 0; width < 5; ++width) {
      Tensor tensor = WindowFunctions.DIRICHLET.apply(width);
      assertEquals(tensor, constant(width));
      assertTrue(ExactScalarQ.all(tensor));
      assertEquals(Total.of(tensor), RealScalar.ONE);
    }
  }

  public void testBinomial() {
    for (int size = 0; size < 5; ++size) {
      Tensor mask = WindowFunctions.BINOMIAL.apply(size);
      assertEquals(Total.of(mask), RealScalar.ONE);
      assertTrue(ExactScalarQ.all(mask));
    }
  }

  public void testSpecific() {
    Tensor result = WindowFunctions.BINOMIAL.apply(2);
    Tensor expect = Tensors.fromString("{1/16, 1/4, 3/8, 1/4, 1/16}");
    assertEquals(result, expect);
  }

  public void testAll() {
    for (WindowFunctions windowFunctions : WindowFunctions.values())
      for (int size = 0; size < 5; ++size) {
        Tensor tensor = windowFunctions.apply(size);
        SymmetricVectorQ.require(tensor);
        assertTrue(Chop._13.close(Total.of(tensor), RealScalar.ONE));
        assertFalse(Scalars.isZero(tensor.Get(0)));
        assertFalse(Scalars.isZero(tensor.Get(tensor.length() - 1)));
        assertEquals(tensor.length(), 2 * size + 1);
      }
  }

  public void testIsZeroBlackman() {
    assertTrue(WindowFunctions.BLACKMAN.isZero());
  }

  public void testIsZeroHann() {
    assertTrue(WindowFunctions.HANN.isZero());
  }

  public void testIsZeroNutall() {
    assertTrue(WindowFunctions.NUTTALL.isZero());
  }

  public void testIsZeroParzen() {
    assertTrue(WindowFunctions.PARZEN.isZero());
  }

  public void testIsZeroTukey() {
    assertTrue(WindowFunctions.TUKEY.isZero());
  }

  public void testAllFail() {
    for (WindowFunctions windowFunctions : WindowFunctions.values())
      try {
        windowFunctions.apply(-1);
        assertTrue(false);
      } catch (Exception exception) {
        // ---
      }
  }
}
