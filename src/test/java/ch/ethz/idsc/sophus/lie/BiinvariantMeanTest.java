// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BiinvariantMeanTest extends TestCase {
  public void testRnSimple() {
    Distribution distribution = UniformDistribution.unit();
    for (int n = 1; n < 8; ++n) {
      Tensor origin = RandomVariate.of(distribution, n, 3);
      Tensor matrix = RandomVariate.of(distribution, n, n);
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
    for (int n = 1; n < 8; ++n) {
      Tensor origin = RandomVariate.of(distribution, n, 3);
      Tensor matrix = RandomVariate.of(distribution, n, n);
      Tensor invers = Inverse.of(matrix);
      Tensor mapped = Tensor.of(matrix.stream() //
          .map(weights -> Se2CoveringBiinvariantMean.INSTANCE.mean(origin, weights)));
      Tensor result = Tensor.of(invers.stream() //
          .map(weights -> Se2CoveringBiinvariantMean.INSTANCE.mean(mapped, weights)));
      Chop._10.requireClose(origin.get(Tensor.ALL, 2), result.get(Tensor.ALL, 2));
      // System.out.println(n);
      // System.out.println(Pretty.of(origin.subtract(result).map(Round._3)));
    }
  }
}
