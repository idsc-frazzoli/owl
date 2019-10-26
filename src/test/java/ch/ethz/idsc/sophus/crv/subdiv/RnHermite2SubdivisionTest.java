// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Derive;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class RnHermite2SubdivisionTest extends TestCase {
  static final List<HermiteSubdivision> LIST = Arrays.asList( //
      RnHermite2Subdivisions.standard(), //
      RnHermite2Subdivisions.manifold());

  public void testStringReverse() {
    Tensor cp1 = RandomVariate.of(NormalDistribution.standard(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    for (HermiteSubdivision hermiteSubdivision : LIST) {
      TensorIteration hs1 = hermiteSubdivision.string(RealScalar.ONE, cp1);
      TensorIteration hs2 = hermiteSubdivision.string(RealScalar.ONE, Reverse.of(cp2));
      for (int count = 0; count < 3; ++count) {
        Tensor result1 = hs1.iterate();
        Tensor result2 = Reverse.of(hs2.iterate());
        result2.set(Tensor::negate, Tensor.ALL, 1);
        Chop._12.requireClose(result1, result2);
      }
    }
  }

  public void testLinearReproduction() {
    Tensor coeffs = Tensors.vector(5, -3);
    ScalarUnaryOperator f0 = Series.of(coeffs);
    ScalarUnaryOperator f1 = Series.of(Derive.of(coeffs));
    Tensor domain = Range.of(0, 10);
    Tensor control = Transpose.of(Tensors.of(domain.map(f0), domain.map(f1)));
    for (HermiteSubdivision hermiteSubdivision : LIST) {
      TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
      Tensor iterate = Do.of(tensorIteration::iterate, 3);
      ExactTensorQ.require(iterate);
      assertEquals(iterate.length(), 33 * 2);
      Tensor id1 = Differences.of(iterate);
      Tensor id2 = Differences.of(id1);
      Chop.NONE.requireAllZero(id2);
    }
  }
}
