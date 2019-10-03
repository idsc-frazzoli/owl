// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LieMerrienHermiteSubdivisionTest extends TestCase {
  public void testString() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {0, 0}}");
    HermiteSubdivision hs1 = MerrienHermiteSubdivision.string(control);
    HermiteSubdivision hs2 = LieMerrienHermiteSubdivision.string(RnGroup.INSTANCE, RnExponential.INSTANCE, control);
    for (int count = 0; count < 6; ++count) {
      Tensor it1 = hs1.iterate();
      Tensor it2 = hs2.iterate();
      assertEquals(it1, it2);
      ExactTensorQ.require(it1);
      ExactTensorQ.require(it2);
    }
  }

  public void testCyclic() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    HermiteSubdivision hs1 = MerrienHermiteSubdivision.cyclic(control);
    HermiteSubdivision hs2 = LieMerrienHermiteSubdivision.cyclic(RnGroup.INSTANCE, RnExponential.INSTANCE, control);
    for (int count = 0; count < 6; ++count) {
      Tensor it1 = hs1.iterate();
      Tensor it2 = hs2.iterate();
      assertEquals(it1, it2);
      ExactTensorQ.require(it1);
      ExactTensorQ.require(it2);
    }
  }
}
