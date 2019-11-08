// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ enum TestHelper {
  ;
  static Tensor generateSim(int n) {
    Distribution distribution = UniformDistribution.of(-2, 2);
    Tensor matrix = RandomVariate.of(distribution, n, n);
    return Transpose.of(matrix).add(matrix);
  }

  static Tensor generateSpd(int n) {
    return SpdExponential.INSTANCE.exp(generateSim(n));
  }
}
