// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class RnHermite1SubdivisionTest extends TestCase {
  public void testString() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {0, 0}}");
    TensorIteration hermiteSubdivision = RnHermite1Subdivision.string(control);
    Tensor iterate = hermiteSubdivision.iterate();
    Tensor expect = Tensors.fromString("{{0, 0}, {1/2, 3/2}, {1, 0}, {5/8, -5/4}, {0, -1}, {-1/8, 1/4}, {0, 0}}");
    assertEquals(iterate, expect);
    ExactTensorQ.require(iterate);
    iterate = hermiteSubdivision.iterate();
    // System.out.println(iterate);
    String string = "{{0, 0}, {5/32, 9/8}, {1/2, 3/2}, {27/32, 9/8}, {1, 0}, {57/64, -13/16}, {5/8, -5/4}, {19/64, -21/16}, {0, -1}, {-9/64, -3/16}, {-1/8, 1/4}, {-3/64, 5/16}, {0, 0}}";
    assertEquals(iterate, Tensors.fromString(string));
    ExactTensorQ.require(iterate);
  }

  public void testStringReverse() {
    Tensor cp1 = RandomVariate.of(NormalDistribution.standard(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    TensorIteration hs1 = RnHermite1Subdivision.string(cp1);
    TensorIteration hs2 = RnHermite1Subdivision.string(Reverse.of(cp2));
    for (int count = 0; count < 3; ++count) {
      Tensor result1 = hs1.iterate();
      Tensor result2 = Reverse.of(hs2.iterate());
      result2.set(Tensor::negate, Tensor.ALL, 1);
      Chop._12.requireClose(result1, result2);
    }
  }

  public void testCyclic() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    TensorIteration hermiteSubdivision = RnHermite1Subdivision.cyclic(control);
    Tensor iterate = hermiteSubdivision.iterate();
    Tensor expect = Tensors.fromString("{{0, 0}, {1/2, 3/2}, {1, 0}, {5/8, -5/4}, {0, -1}, {-1/2, -3/4}, {-1/2, 1}, {-1/8, 1/2}}");
    assertEquals(iterate, expect);
    ExactTensorQ.require(iterate);
  }

  public void testPolynomialReproduction() {
    Tensor coeffs = Tensors.vector(1, -3, 2, -1);
    ScalarUnaryOperator f0 = Series.of(coeffs);
    ScalarUnaryOperator f1 = Series.of(Multinomial.derivative(coeffs));
    Tensor domain = Range.of(0, 10);
    Tensor control = Transpose.of(Tensors.of(domain.map(f0), domain.map(f1)));
    TensorIteration hermiteSubdivision = RnHermite1Subdivision.string(control);
    Tensor iterate = hermiteSubdivision.iterate();
    ExactTensorQ.require(iterate);
    Tensor idm = Range.of(0, 19).multiply(RationalScalar.HALF);
    Tensor if0 = iterate.get(Tensor.ALL, 0);
    assertEquals(if0, idm.map(f0));
    Tensor if1 = iterate.get(Tensor.ALL, 1);
    assertEquals(if1, idm.map(f1));
  }
}
