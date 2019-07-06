// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import junit.framework.TestCase;

public class DecibelTest extends TestCase {
  public void testSimple() {
    Scalar scalar = Decibel.FUNCTION.apply(RealScalar.of(100));
    assertEquals(scalar, RealScalar.of(40));
  }

  public void testVector() {
    Tensor tensor = Decibel.of(Range.of(1, 100));
    assertEquals(tensor.length(), 99);
  }

  public void testNegativeInfinity() {
    Scalar scalar = Decibel.FUNCTION.apply(RealScalar.ZERO);
    assertEquals(scalar, DoubleScalar.NEGATIVE_INFINITY);
  }
}
