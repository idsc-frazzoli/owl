// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Hermite1SubdivisionTest extends TestCase {
  public void testString() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {0, 0}}");
    TensorIteration hs1 = RnHermite1Subdivision.string(control);
    TensorIteration hs2 = new Hermite1Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).string(RealScalar.ONE, control);
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
    TensorIteration hs1 = RnHermite1Subdivision.cyclic(control);
    TensorIteration hs2 = new Hermite1Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).cyclic(RealScalar.ONE, control);
    for (int count = 0; count < 6; ++count) {
      Tensor it1 = hs1.iterate();
      Tensor it2 = hs2.iterate();
      assertEquals(it1, it2);
      ExactTensorQ.require(it1);
      ExactTensorQ.require(it2);
    }
  }

  public void testStringQuantity() {
    Tensor control = Tensors.fromString("{{0[m], 0[m*s^-1]}, {1[m], 0[m*s^-1]}, {0[m], -1[m*s^-1]}, {0[m], 0[m*s^-1]}}");
    TensorIteration tensorIteration = //
        new Hermite1Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).string(Quantity.of(1, "s"), control);
    tensorIteration.iterate();
    tensorIteration.iterate();
    tensorIteration.iterate();
  }

  public void testCyclicQuantity() {
    Tensor control = Tensors.fromString("{{0[m], 0[m*s^-1]}, {1[m], 0[m*s^-1]}, {0[m], -1[m*s^-1]}, {0[m], 0[m*s^-1]}}");
    TensorIteration tensorIteration = //
        new Hermite1Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).cyclic(Quantity.of(1, "s"), control);
    tensorIteration.iterate();
    tensorIteration.iterate();
    tensorIteration.iterate();
  }

  public void testNullFail() {
    try {
      new Hermite1Subdivision(Se2CoveringGroup.INSTANCE, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      new Hermite1Subdivision(null, Se2CoveringExponential.INSTANCE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
