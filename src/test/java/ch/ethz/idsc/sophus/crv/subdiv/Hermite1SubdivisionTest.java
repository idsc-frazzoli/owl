// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Hermite1SubdivisionTest extends TestCase {
  public void testStringQuantity() {
    Tensor control = Tensors.fromString("{{0[m], 0[m*s^-1]}, {1[m], 0[m*s^-1]}, {0[m], -1[m*s^-1]}, {0[m], 0[m*s^-1]}}");
    TensorIteration tensorIteration = //
        Hermite1Subdivisions.of(RnGroup.INSTANCE, RnExponential.INSTANCE).string(Quantity.of(1, "s"), control);
    Tensor tensor = Do.of(tensorIteration::iterate, 3);
    ExactTensorQ.require(tensor);
  }

  public void testCyclicQuantity() {
    Tensor control = Tensors.fromString("{{0[m], 0[m*s^-1]}, {1[m], 0[m*s^-1]}, {0[m], -1[m*s^-1]}, {0[m], 0[m*s^-1]}}");
    TensorIteration tensorIteration = //
        Hermite1Subdivisions.of(RnGroup.INSTANCE, RnExponential.INSTANCE).cyclic(Quantity.of(1, "s"), control);
    Tensor tensor = Do.of(tensorIteration::iterate, 3);
    ExactTensorQ.require(tensor);
  }

  public void testNullFail() {
    try {
      Hermite1Subdivisions.of(Se2CoveringGroup.INSTANCE, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      Hermite1Subdivisions.of(null, Se2CoveringExponential.INSTANCE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
