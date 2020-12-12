// code by jph
package ch.ethz.idsc.sophus.ply;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class SutherlandHodgmanAlgorithmTest extends TestCase {
  public void testFail() {
    AssertFail.of(() -> SutherlandHodgmanAlgorithm.of(HilbertMatrix.of(2, 3)));
  }

  public void testLine() {
    Tensor tensor = SutherlandHodgmanAlgorithm.intersection( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(3, 3), //
        Tensors.vector(3, 2));
    assertEquals(tensor, Tensors.vector(3, 0));
    ExactTensorQ.require(tensor);
  }

  public void testSingular() {
    AssertFail.of(() -> SutherlandHodgmanAlgorithm.intersection( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(4, 0), //
        Tensors.vector(9, 0)));
  }
}
