// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Derive;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class Hermite3FilterTest extends TestCase {
  public void testR1PolynomialReproduction() {
    Tensor coeffs = Tensors.vector(1, 3, -2, 3);
    ScalarUnaryOperator f0 = Series.of(coeffs);
    ScalarUnaryOperator f1 = Series.of(Derive.of(coeffs));
    Tensor domain = Range.of(0, 10);
    Tensor control = Transpose.of(Tensors.of(domain.map(f0), domain.map(f1)));
    HermiteFilter hermiteFilter = //
        new Hermite3Filter(RnGroup.INSTANCE, RnExponential.INSTANCE, RnBiinvariantMean.INSTANCE);
    TensorIteration tensorIteration = hermiteFilter.string(RealScalar.ONE, control);
    Tensor iterate = Do.of(tensorIteration::iterate, 2);
    ExactTensorQ.require(iterate);
    assertEquals(control, iterate);
  }

  public void testSe2ConstantReproduction() {
    Tensor control = ConstantArray.of(Tensors.fromString("{{2, 3, 1}, {0, 0, 0}}"), 10);
    HermiteFilter hermiteFilter = //
        new Hermite3Filter(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, Se2BiinvariantMean.FILTER);
    TensorIteration tensorIteration = hermiteFilter.string(RealScalar.ONE, control);
    Tensor iterate = Do.of(tensorIteration::iterate, 2);
    Chop._14.requireClose(control, iterate);
  }

  public void testSe2LinearReproduction() {
    Tensor pg = Tensors.vector(1, 2, 3);
    Tensor pv = Tensors.vector(.3, -.2, -.1);
    Tensor control = Tensors.empty();
    for (int count = 0; count < 10; ++count) {
      control.append(Tensors.of(pg, pv));
      pg = Se2Group.INSTANCE.element(pg).combine(Se2CoveringExponential.INSTANCE.exp(pv));
    }
    HermiteFilter hermiteFilter = //
        new Hermite3Filter(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, Se2BiinvariantMean.FILTER);
    TensorIteration tensorIteration = hermiteFilter.string(RealScalar.ONE, control);
    Tensor iterate = Do.of(tensorIteration::iterate, 2);
    Chop._14.requireClose(control, iterate);
  }
}
