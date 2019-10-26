// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.IOException;

import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Derive;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.Assert;

/* package */ enum TestHelper {
  ;
  private static void checkString(HermiteSubdivision hs1, HermiteSubdivision hs2) {
    Distribution distribution = DiscreteUniformDistribution.of(-5, 6);
    Tensor control = RandomVariate.of(distribution, 4, 2);
    TensorIteration tensorIteration1 = hs1.string(RealScalar.ONE, control);
    TensorIteration tensorIteration2 = hs2.string(RealScalar.ONE, control);
    for (int count = 0; count < 6; ++count) {
      Tensor it1 = tensorIteration1.iterate();
      Tensor it2 = tensorIteration2.iterate();
      Assert.assertEquals(it1, it2);
      ExactTensorQ.require(it1);
      ExactTensorQ.require(it2);
    }
  }

  private static void checkCyclic(HermiteSubdivision hs1, HermiteSubdivision hs2) {
    Distribution distribution = DiscreteUniformDistribution.of(-5, 6);
    Tensor control = RandomVariate.of(distribution, 4, 2);
    TensorIteration tensorIteration1 = hs1.cyclic(RealScalar.ONE, control);
    TensorIteration tensorIteration2 = hs2.cyclic(RealScalar.ONE, control);
    for (int count = 0; count < 6; ++count) {
      Tensor it1 = tensorIteration1.iterate();
      Tensor it2 = tensorIteration2.iterate();
      Assert.assertEquals(it1, it2);
      ExactTensorQ.require(it1);
      ExactTensorQ.require(it2);
    }
  }

  public static void check(HermiteSubdivision hs1, HermiteSubdivision hs2) {
    checkString(hs1, hs2);
    checkCyclic(hs1, hs2);
  }

  /***************************************************/
  public static void checkP(int n, HermiteSubdivision hermiteSubdivision) {
    Distribution distribution = DiscreteUniformDistribution.of(-5, 6);
    Tensor coeffs = RandomVariate.of(distribution, n + 1);
    ScalarUnaryOperator f0 = Series.of(coeffs);
    ScalarUnaryOperator f1 = Series.of(Derive.of(coeffs));
    Tensor domain = Range.of(0, 10);
    Tensor control = Transpose.of(Tensors.of(domain.map(f0), domain.map(f1)));
    TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
    Tensor iterate = tensorIteration.iterate();
    ExactTensorQ.require(iterate);
    Tensor idm = Range.of(0, 19).multiply(RationalScalar.HALF);
    Tensor if0 = iterate.get(Tensor.ALL, 0);
    Assert.assertEquals(if0, idm.map(f0));
    Tensor if1 = iterate.get(Tensor.ALL, 1);
    Assert.assertEquals(if1, idm.map(f1));
  }

  public static void checkQuantity(HermiteSubdivision hermiteSubdivision) throws ClassNotFoundException, IOException {
    Tensor control = Tensors.fromString("{{0[m], 0[m*s^-1]}, {1[m], 0[m*s^-1]}, {0[m], -1[m*s^-1]}, {0[m], 0[m*s^-1]}}");
    HermiteSubdivision copy = Serialization.copy(hermiteSubdivision);
    {
      TensorIteration tensorIteration = copy.string(Quantity.of(3, "s"), control);
      ExactTensorQ.require(Do.of(tensorIteration::iterate, 2));
    }
    {
      TensorIteration tensorIteration = copy.cyclic(Quantity.of(3, "s"), control);
      ExactTensorQ.require(Do.of(tensorIteration::iterate, 2));
    }
  }
}
