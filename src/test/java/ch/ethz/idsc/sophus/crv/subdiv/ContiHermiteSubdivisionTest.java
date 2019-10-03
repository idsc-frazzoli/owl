// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ContiHermiteSubdivisionTest extends TestCase {
  public void testString() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    HermiteSubdivision hermiteSubdivision = ContiHermiteSubdivision.string(control);
    Tensor tensor = hermiteSubdivision.iterate();
    ExactTensorQ.require(tensor);
    assertEquals(tensor.length(), 7);
  }

  public void testCyclic() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    HermiteSubdivision hermiteSubdivision = ContiHermiteSubdivision.cyclic(control);
    Tensor tensor = hermiteSubdivision.iterate();
    ExactTensorQ.require(tensor);
    assertEquals(tensor.length(), 8);
  }
}
