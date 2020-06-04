// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** Reference:
 * "Weighted Averages on Surfaces"
 * by Daniele Panozzo, Ilya Baran, Olga Diamanti, Olga Sorkine-Hornung */
/* package */ class AveragedMovingDomain2D extends MovingDomain2D {
  public AveragedMovingDomain2D(Tensor origin, TensorUnaryOperator tensorUnaryOperator, Tensor domain) {
    super(origin, tensorUnaryOperator, domain);
  }

  public Tensor[][] forward(Tensor target, BiinvariantMean biinvariantMean) {
    int rows = domain.length();
    int cols = Unprotect.dimension1(domain);
    Tensor[][] array = new Tensor[rows][cols];
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy)
        array[cx][cy] = biinvariantMean.mean(target, weights[cx][cy]);
    });
    return array;
  }
}
