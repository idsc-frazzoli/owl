// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Hermite2SubdivisionTest extends TestCase {
  public void testString() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {0, 0}}");
    HermiteSubdivision hs1 = RnHermite2Subdivision.string(control);
    HermiteSubdivision hs2 = new Hermite2Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).string(control);
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
    HermiteSubdivision hs1 = RnHermite2Subdivision.cyclic(control);
    HermiteSubdivision hs2 = new Hermite2Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).cyclic(control);
    for (int count = 0; count < 6; ++count) {
      Tensor it1 = hs1.iterate();
      Tensor it2 = hs2.iterate();
      assertEquals(it1, it2);
      ExactTensorQ.require(it1);
      ExactTensorQ.require(it2);
    }
  }

  public void testNullFail() {
    try {
      new Hermite2Subdivision(Se2CoveringGroup.INSTANCE, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      new Hermite2Subdivision(null, Se2CoveringExponential.INSTANCE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
