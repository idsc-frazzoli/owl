// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LinearGroupElementTest extends TestCase {
  public void testSimple() {
    int n = 5;
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    LinearGroupElement linearGroupElement = new LinearGroupElement(matrix);
    Tensor result = linearGroupElement.inverse().combine(matrix);
    assertTrue(Chop._10.close(result, IdentityMatrix.of(n)));
  }

  public void testNonSquareFail() {
    try {
      new LinearGroupElement(HilbertMatrix.of(2, 3));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNonInvertibleFail() {
    LinearGroupElement linearGroupElement = new LinearGroupElement(DiagonalMatrix.of(1, 0, 2));
    try {
      linearGroupElement.inverse();
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
