// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
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

public class Hermite1FilterTest extends TestCase {
  public void testR1PolynomialReproduction() {
    Tensor coeffs = Tensors.vector(1, 3, -2, 3);
    ScalarUnaryOperator f0 = Series.of(coeffs);
    ScalarUnaryOperator f1 = Series.of(Derive.of(coeffs));
    Tensor domain = Range.of(0, 10);
    Tensor control = Transpose.of(Tensors.of(domain.map(f0), domain.map(f1)));
    HermiteFilter hermiteFilter = new Hermite1Filter(RnGroup.INSTANCE, RnExponential.INSTANCE);
    TensorIteration tensorIteration = hermiteFilter.string(RealScalar.ONE, control);
    tensorIteration.iterate();
    Tensor iterate = tensorIteration.iterate();
    ExactTensorQ.require(iterate);
    assertEquals(control.extract(1, control.length() - 1), iterate);
  }

  public void testSe2ConstantReproduction() {
    Tensor control = ConstantArray.of(Tensors.fromString("{{2, 3, 1}, {0, 0, 0}}"), 10);
    HermiteFilter hermiteFilter = new Hermite1Filter(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
    TensorIteration tensorIteration = hermiteFilter.string(RealScalar.ONE, control);
    tensorIteration.iterate();
    Tensor iterate = tensorIteration.iterate();
    Chop._14.requireClose(control.extract(1, control.length() - 1), iterate);
  }

  public void testSe2LinearReproduction() {
    Tensor pg = Tensors.vector(1, 2, 3);
    Tensor pv = Tensors.vector(.3, -.2, -.1);
    Tensor control = Tensors.empty();
    for (int count = 0; count < 10; ++count) {
      control.append(Tensors.of(pg, pv));
      pg = Se2Group.INSTANCE.element(pg).combine(Se2CoveringExponential.INSTANCE.exp(pv));
    }
    HermiteFilter hermiteFilter = new Hermite1Filter(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
    TensorIteration tensorIteration = hermiteFilter.string(RealScalar.ONE, control);
    tensorIteration.iterate();
    Tensor iterate = tensorIteration.iterate();
    Chop._14.requireClose(control.extract(1, control.length() - 1), iterate);
  }
}
