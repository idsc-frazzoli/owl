// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.ethz.idsc.sophus.math.StochasticMatrixQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BiinvariantMeanTest extends TestCase {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._1);
  private static final Distribution DISTRIBUTION = UniformDistribution.unit();

  /** @param n
   * @return */
  private static Tensor affine(int n) {
    Tensor matrix = RandomVariate.of(DISTRIBUTION, n, n);
    matrix = StochasticMatrixQ.requireRows(Tensor.of(matrix.stream().map(NORMALIZE)));
    StochasticMatrixQ.requireRows(Inverse.of(matrix));
    return matrix;
  }

  public void testRnSimple() {
    Distribution distribution = UniformDistribution.unit();
    for (int n = 1; n < 7; ++n) {
      Tensor origin = RandomVariate.of(distribution, n, 3);
      Tensor matrix = affine(n);
      Tensor invers = Inverse.of(matrix);
      Tensor mapped = Tensor.of(matrix.stream() //
          .map(weights -> RnBiinvariantMean.INSTANCE.mean(origin, weights)));
      Tensor result = Tensor.of(invers.stream() //
          .map(weights -> RnBiinvariantMean.INSTANCE.mean(mapped, weights)));
      Chop._10.requireClose(origin, result);
    }
  }

  public void testSe2CSimple() {
    Distribution distribution = UniformDistribution.unit();
    int fails = 0;
    for (int n = 1; n < 7; ++n) {
      Tensor origin = RandomVariate.of(distribution, n, 3);
      Tensor matrix = affine(n);
      Tensor invers = Inverse.of(matrix);
      Tensor mapped = Tensor.of(matrix.stream() //
          .map(weights -> Se2CoveringBiinvariantMean.INSTANCE.mean(origin, weights)));
      Tensor result = Tensor.of(invers.stream() //
          .map(weights -> Se2CoveringBiinvariantMean.INSTANCE.mean(mapped, weights)));
      Chop._10.requireClose(origin.get(Tensor.ALL, 2), result.get(Tensor.ALL, 2));
      if (!Chop._01.close(origin, result))
        ++fails;
    }
    assertTrue(fails <= 2);
  }
}
