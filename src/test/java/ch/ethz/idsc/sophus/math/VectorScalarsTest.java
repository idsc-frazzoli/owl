// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class VectorScalarsTest extends TestCase {
  public void testVector() {
    Scalar scalar = VectorScalar.of(19, 2.5, 3);
    Tensor tensor = VectorScalars.vector(scalar);
    assertEquals(tensor, Tensors.vector(19, 2.5, 3));
  }

  public void testAt() {
    Scalar scalar = VectorScalar.of(19, 2.5, 3);
    assertEquals(VectorScalars.at(scalar, 0), RealScalar.of(19));
    assertEquals(VectorScalars.at(scalar, 1), RealScalar.of(2.5));
    assertEquals(VectorScalars.at(scalar, 2), RealScalar.of(3));
    assertTrue(ExactScalarQ.of(VectorScalars.at(scalar, 0)));
    assertFalse(ExactScalarQ.of(VectorScalars.at(scalar, 1)));
    assertTrue(ExactScalarQ.of(VectorScalars.at(scalar, 2)));
  }
}
