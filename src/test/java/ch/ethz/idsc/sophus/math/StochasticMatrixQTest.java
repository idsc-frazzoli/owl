// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class StochasticMatrixQTest extends TestCase {
  public void testSimple() {
    StochasticMatrixQ.requireRows(IdentityMatrix.of(3));
  }

  public void testScalarFail() {
    try {
      StochasticMatrixQ.requireRows(RealScalar.ONE);
      fail();
    } catch (Exception e) {
      // ---
    }
  }

  public void testVectorFail() {
    try {
      StochasticMatrixQ.requireRows(Tensors.vector(1, 0, 0));
      fail();
    } catch (Exception e) {
      // ---
    }
  }
}
