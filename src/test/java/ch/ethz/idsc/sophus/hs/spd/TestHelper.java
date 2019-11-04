// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ enum TestHelper {
  ;
  static Tensor generateSpd(int n) {
    Distribution distribution = UniformDistribution.of(-2, 2);
    Tensor matrix = RandomVariate.of(distribution, n, n);
    Tensor x = Transpose.of(matrix).add(matrix);
    return SpdExponential.INSTANCE.exp(x);
  }
}
