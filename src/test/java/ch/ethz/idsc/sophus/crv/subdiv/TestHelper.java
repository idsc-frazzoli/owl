// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
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
}
