// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.opt.RigidMotionFit;

/** Reference:
 * "Weighted Averages on Surfaces"
 * by Daniele Panozzo, Ilya Baran, Olga Diamanti, Olga Sorkine-Hornung */
class LSMovingDomain2D extends MovingDomain2D {
  public LSMovingDomain2D(Tensor origin, WeightingInterface weightingInterface, Tensor domain) {
    super(origin, weightingInterface, domain);
  }

  @Override
  public Tensor[][] forward(Tensor target, BiinvariantMean biinvariantMean) {
    System.out.println("here");
    int rows = domain.length();
    int cols = Unprotect.dimension1(domain);
    Tensor[][] array = new Tensor[rows][cols];
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy)
        array[cx][cy] = RigidMotionFit.of(origin, target, weights[cx][cy]).apply(domain.get(cx, cy));
    });
    return array;
  }
}
