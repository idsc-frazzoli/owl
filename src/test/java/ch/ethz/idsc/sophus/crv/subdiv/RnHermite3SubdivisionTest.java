// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RnHermite3SubdivisionTest extends TestCase {
  public void testString() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    HermiteSubdivision hermiteSubdivision = RnHermite3Subdivision.string(control);
    Tensor tensor = hermiteSubdivision.iterate();
    ExactTensorQ.require(tensor);
    assertEquals(tensor.length(), 7);
  }

  public void testCyclic() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    HermiteSubdivision hermiteSubdivision = RnHermite3Subdivision.cyclic(control);
    Tensor tensor = hermiteSubdivision.iterate();
    ExactTensorQ.require(tensor);
    assertEquals(tensor.length(), 8);
  }
}
