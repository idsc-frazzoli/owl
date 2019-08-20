// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.math.StochasticMatrixQ;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;
import junit.framework.TestCase;

public class BSplineLimitMatrixTest extends TestCase {
  public void testSimple() {
    for (int degree = 0; degree < 5; ++degree)
      for (int n = 1; n < 10; ++n) {
        Tensor tensor = BSplineLimitMatrix.string(n, degree);
        ExactTensorQ.require(tensor);
        StochasticMatrixQ.requireRows(tensor);
        StochasticMatrixQ.requireRows(Inverse.of(tensor));
        // System.out.println("n=" + n + " degree=" + degree);
        // System.out.println(Pretty.of(tensor));
      }
  }
}
