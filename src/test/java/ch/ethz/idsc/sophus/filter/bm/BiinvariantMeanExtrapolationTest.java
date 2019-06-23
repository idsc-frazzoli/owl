// code by jph
package ch.ethz.idsc.sophus.filter.bm;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.spline.MonomialExtrapolationMask;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class BiinvariantMeanExtrapolationTest extends TestCase {
  public void testSimple() {
    TensorUnaryOperator biinvariantMeanExtrapolation = //
        BiinvariantMeanExtrapolation.of(RnBiinvariantMean.INSTANCE, MonomialExtrapolationMask.INSTANCE);
    Tensor tensor = biinvariantMeanExtrapolation.apply(Tensors.vector(1, 2));
    assertEquals(tensor, RealScalar.of(3));
  }

  public void testSeries() throws ClassNotFoundException, IOException {
    TensorUnaryOperator biinvariantMeanExtrapolation = //
        Serialization.copy(BiinvariantMeanExtrapolation.of(RnBiinvariantMean.INSTANCE, MonomialExtrapolationMask.INSTANCE));
    Distribution distribution = DiscreteUniformDistribution.of(3, 12);
    for (int deg = 1; deg < 6; ++deg) {
      ScalarUnaryOperator scalarUnaryOperator = Series.of(RandomVariate.of(distribution, deg + 1));
      Tensor sequence = Range.of(0, deg + 1).map(scalarUnaryOperator);
      Tensor predict = biinvariantMeanExtrapolation.apply(sequence);
      Tensor actual = RealScalar.of(deg + 1).map(scalarUnaryOperator);
      assertEquals(predict, actual);
      ExactScalarQ.require(predict.Get());
    }
  }
}
