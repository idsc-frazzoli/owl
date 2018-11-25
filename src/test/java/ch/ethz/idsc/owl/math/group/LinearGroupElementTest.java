// code by jph
package ch.ethz.idsc.owl.math.group;

import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
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

  public void testSerializable() throws ClassNotFoundException, IOException {
    Tensor tensor = DiagonalMatrix.of(1, 2, 3);
    LinearGroupElement linearGroupElement = new LinearGroupElement(tensor);
    LinearGroupElement copy = Serialization.copy(linearGroupElement);
    Tensor result = copy.inverse().combine(tensor);
    assertEquals(result, IdentityMatrix.of(3));
  }

  public void testNonSquareFail() {
    try {
      new LinearGroupElement(HilbertMatrix.of(2, 3));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testCombineNonSquareFail() {
    LinearGroupElement linearGroupElement = new LinearGroupElement(DiagonalMatrix.of(1, 2));
    try {
      linearGroupElement.combine(HilbertMatrix.of(2, 3));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      linearGroupElement.combine(Tensors.vector(1, 2));
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
