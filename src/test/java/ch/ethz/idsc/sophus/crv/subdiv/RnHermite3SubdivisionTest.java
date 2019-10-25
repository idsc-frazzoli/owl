// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Derive;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class RnHermite3SubdivisionTest extends TestCase {
  public void testString() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    TensorIteration tensorIteration = RnHermite3Subdivisions.standard().string(RealScalar.ONE, control);
    Tensor tensor = tensorIteration.iterate();
    ExactTensorQ.require(tensor);
    assertEquals(tensor.length(), 7);
  }

  public void testStringReverse() {
    Tensor cp1 = RandomVariate.of(NormalDistribution.standard(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    TensorIteration ti1 = RnHermite3Subdivisions.standard().string(RealScalar.ONE, cp1);
    TensorIteration ti2 = RnHermite3Subdivisions.standard().string(RealScalar.ONE, Reverse.of(cp2));
    for (int count = 0; count < 3; ++count) {
      Tensor result1 = ti1.iterate();
      Tensor result2 = Reverse.of(ti2.iterate());
      result2.set(Tensor::negate, Tensor.ALL, 1);
      Chop._12.requireClose(result1, result2);
    }
  }

  public void testCyclic() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    TensorIteration tensorIteration = RnHermite3Subdivisions.standard().cyclic(RealScalar.ONE, control);
    Tensor tensor = tensorIteration.iterate();
    ExactTensorQ.require(tensor);
    assertEquals(tensor.length(), 8);
  }

  public void testPolynomialReproduction() {
    TestHelper.checkP(3, RnHermite3Subdivisions.standard());
    HermiteSubdivision hermiteSubdivision = RnHermite3Subdivisions.of( //
        RationalScalar.of(+2, 157), //
        RationalScalar.of(-3, 43));
    TestHelper.checkP(3, hermiteSubdivision);
  }

  public void testPolynomialReproductionMore() {
    HermiteSubdivision hermiteSubdivision = RnHermite3Subdivisions.of( //
        RationalScalar.of(+3, 157), //
        RationalScalar.of(-5, 43));
    Tensor coeffs = Tensors.vector(2, -7, 4, -3);
    ScalarUnaryOperator f0 = Series.of(coeffs);
    ScalarUnaryOperator f1 = Series.of(Derive.of(coeffs));
    Tensor domain = Range.of(0, 10);
    Tensor control = Transpose.of(Tensors.of(domain.map(f0), domain.map(f1)));
    TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
    Tensor iterate = tensorIteration.iterate();
    ExactTensorQ.require(iterate);
    Tensor idm = Range.of(0, 19).multiply(RationalScalar.HALF);
    Tensor if0 = iterate.get(Tensor.ALL, 0);
    assertEquals(if0, idm.map(f0));
    Tensor if1 = iterate.get(Tensor.ALL, 1);
    assertEquals(if1, idm.map(f1));
    Tensor id1 = Differences.of(iterate);
    Tensor id2 = Differences.of(id1);
    Tensor id3 = Differences.of(id2);
    Tensor id4 = Differences.of(id3);
    Chop.NONE.requireAllZero(id4);
  }

  public void testMatrices() {
    Tensor ARP = Tensors.fromString("{{1/128, +1/256}, {-3/32, -1/32}}");
    Tensor ARQ = Tensors.fromString("{{63/64, 0}, {0, 3/8}}");
    Tensor ARR = Tensors.fromString("{{1/128, -1/256}, {+3/32, -1/32}}");
    RnHermite3Subdivision rnHermite3Subdivision = RnHermite3Subdivisions.standard();
    assertEquals(ARP, ExactTensorQ.require(rnHermite3Subdivision.ARP));
    assertEquals(ARQ, ExactTensorQ.require(rnHermite3Subdivision.ARQ));
    assertEquals(ARR, ExactTensorQ.require(rnHermite3Subdivision.ARR));
  }

  public void testSpecialCase() {
    RnHermite3Subdivision rnHermite3Subdivision = //
        RnHermite3Subdivisions.of(RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(Array.zeros(2, 2), ExactTensorQ.require(rnHermite3Subdivision.ARP));
    assertEquals(DiagonalMatrix.of(1, 0.5), ExactTensorQ.require(rnHermite3Subdivision.ARQ));
    assertEquals(Array.zeros(2, 2), ExactTensorQ.require(rnHermite3Subdivision.ARR));
  }
}
