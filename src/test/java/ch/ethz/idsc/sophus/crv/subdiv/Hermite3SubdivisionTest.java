// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.IOException;

import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Hermite3SubdivisionTest extends TestCase {
  public void testStringLength2() {
    Tensor control = Tensors.fromString("{{3, 4}, {1, -3}}");
    TensorIteration tensorIteration1 = RnHermite3Subdivisions.standard().string(RealScalar.ONE, control);
    TensorIteration tensorIteration2 = //
        Hermite3Subdivisions.of(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE) //
            .string(RealScalar.ONE, control);
    for (int count = 0; count < 6; ++count) {
      Tensor it1 = tensorIteration1.iterate();
      Tensor it2 = tensorIteration2.iterate();
      assertEquals(it1, it2);
      ExactTensorQ.require(it1);
      ExactTensorQ.require(it2);
    }
  }

  public void testStringReverseRn() {
    Tensor cp1 = RandomVariate.of(NormalDistribution.standard(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    TensorIteration tensorIteration1 = //
        Hermite3Subdivisions.of(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE) //
            .string(RealScalar.ONE, cp1);
    TensorIteration tensorIteration2 = //
        Hermite3Subdivisions.of(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE) //
            .string(RealScalar.ONE, Reverse.of(cp2));
    for (int count = 0; count < 3; ++count) {
      Tensor result1 = tensorIteration1.iterate();
      Tensor result2 = Reverse.of(tensorIteration2.iterate());
      result2.set(Tensor::negate, Tensor.ALL, 1);
      Chop._12.requireClose(result1, result2);
    }
  }

  public void testCyclic() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    TensorIteration tensorIteration1 = RnHermite3Subdivisions.standard().cyclic(RealScalar.ONE, control);
    TensorIteration tensorIteration2 = //
        Hermite3Subdivisions.of(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE) //
            .cyclic(RealScalar.ONE, control);
    for (int count = 0; count < 6; ++count) {
      Tensor it1 = tensorIteration1.iterate();
      Tensor it2 = tensorIteration2.iterate();
      assertEquals(it1, it2);
      ExactTensorQ.require(it1);
      ExactTensorQ.require(it2);
    }
  }

  public void testQuantity() throws ClassNotFoundException, IOException {
    TestHelper.checkQuantity(Hermite3Subdivisions.of(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE));
  }

  public void testNullFail() {
    try {
      Hermite3Subdivisions.of(Se2CoveringGroup.INSTANCE, null, RnBiinvariantMean.INSTANCE);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      Hermite3Subdivisions.of(null, Se2CoveringExponential.INSTANCE, RnBiinvariantMean.INSTANCE);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      Hermite3Subdivisions.of(Se2CoveringGroup.INSTANCE, Se2CoveringExponential.INSTANCE, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
