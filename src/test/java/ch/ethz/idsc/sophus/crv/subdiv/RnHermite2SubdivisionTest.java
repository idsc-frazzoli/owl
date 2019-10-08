// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
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
  public void testStringReverse() {
    Tensor cp1 = RandomVariate.of(NormalDistribution.standard(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    HermiteSubdivision hs1 = RnHermite2Subdivision.string(cp1);
    HermiteSubdivision hs2 = RnHermite2Subdivision.string(Reverse.of(cp2));
    for (int count = 0; count < 3; ++count) {
      Tensor result1 = hs1.iterate();
      Tensor result2 = Reverse.of(hs2.iterate());
      result2.set(Tensor::negate, Tensor.ALL, 1);
      Chop._12.requireClose(result1, result2);
    }
  }

  public void testLinearReproduction() {
    Tensor coeffs = Tensors.vector(5, -3);
    ScalarUnaryOperator f0 = Series.of(coeffs);
    ScalarUnaryOperator f1 = Series.of(Multinomial.derivative(coeffs));
    Tensor domain = Range.of(0, 10);
    Tensor control = Transpose.of(Tensors.of(domain.map(f0), domain.map(f1)));
    HermiteSubdivision hermiteSubdivision = RnHermite2Subdivision.string(control);
    hermiteSubdivision.iterate();
    hermiteSubdivision.iterate();
    Tensor iterate = hermiteSubdivision.iterate();
    ExactTensorQ.require(iterate);
    assertEquals(iterate.length(), 33 * 2);
    Tensor id1 = Differences.of(iterate);
    Tensor id2 = Differences.of(id1);
    Chop.NONE.requireAllZero(id2);
  }
}
