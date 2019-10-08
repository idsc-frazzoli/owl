// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Hermite1SubdivisionTest extends TestCase {
  public void testString() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {0, 0}}");
    TensorIteration hs1 = RnHermite1Subdivision.string(control);
    TensorIteration hs2 = new Hermite1Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).string(control);
    for (int count = 0; count < 6; ++count) {
      Tensor it1 = hs1.iterate();
      Tensor it2 = hs2.iterate();
      assertEquals(it1, it2);
      ExactTensorQ.require(it1);
      ExactTensorQ.require(it2);
    }
  }

  public void testStringReverseRn() {
    Tensor cp1 = RandomVariate.of(NormalDistribution.standard(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    TensorIteration hs1 = new Hermite1Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).string(cp1);
    TensorIteration hs2 = new Hermite1Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).string(Reverse.of(cp2));
    for (int count = 0; count < 3; ++count) {
      Tensor result1 = hs1.iterate();
      Tensor result2 = Reverse.of(hs2.iterate());
      result2.set(Tensor::negate, Tensor.ALL, 1);
      Chop._12.requireClose(result1, result2);
    }
  }

  public void testCyclic() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    TensorIteration hs1 = RnHermite1Subdivision.cyclic(control);
    TensorIteration hs2 = new Hermite1Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).cyclic(control);
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
    TensorIteration hermiteSubdivision = //
        new Hermite1Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).string(Quantity.of(1, "s"), control);
    hermiteSubdivision.iterate();
    hermiteSubdivision.iterate();
    hermiteSubdivision.iterate();
  }

  public void testCyclicQuantity() {
    Tensor control = Tensors.fromString("{{0[m], 0[m*s^-1]}, {1[m], 0[m*s^-1]}, {0[m], -1[m*s^-1]}, {0[m], 0[m*s^-1]}}");
    TensorIteration hermiteSubdivision = //
        new Hermite1Subdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).cyclic(Quantity.of(1, "s"), control);
    hermiteSubdivision.iterate();
    hermiteSubdivision.iterate();
    hermiteSubdivision.iterate();
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
