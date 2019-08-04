// code by jph
package ch.ethz.idsc.sophus.lie.rn;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RnUniformResampleTest extends TestCase {
  public void testString() throws ClassNotFoundException, IOException {
    CurveSubdivision curveSubdivision = Serialization.copy(RnUniformResample.of(Quantity.of(2, "m")));
    Tensor tensor = curveSubdivision.string(Tensors.fromString("{{0[m]}, {1[m]}, {3[m]}, {6[m]}}"));
    assertEquals(ExactTensorQ.require(tensor), Tensors.fromString("{{0[m]}, {2[m]}, {4[m]}}"));
  }

  public void testCyclic() {
    CurveSubdivision curveSubdivision = RnUniformResample.of(Quantity.of(2, "m"));
    Tensor tensor = curveSubdivision.cyclic(Tensors.fromString("{{0[m]}, {1[m]}, {3[m]}, {6[m]}}"));
    assertEquals(ExactTensorQ.require(tensor), Tensors.fromString("{{0[m]}, {2[m]}, {4[m]}, {6[m]}, {4[m]}, {2[m]}}"));
  }

  public void testNegativeFail() {
    try {
      RnUniformResample.of(Quantity.of(0.0, "m"));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      RnUniformResample.of(Quantity.of(-1, "m"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNullFail() {
    try {
      RnUniformResample.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
