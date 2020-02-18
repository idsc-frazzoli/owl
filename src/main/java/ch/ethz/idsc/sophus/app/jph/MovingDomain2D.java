// code by jph
package ch.ethz.idsc.sophus.app.jph;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;

/** Reference:
 * "Weighted Averages on Surfaces"
 * by Daniele Panozzo, Ilya Baran, Olga Diamanti, Olga Sorkine-Hornung */
class MovingDomain2D {
  private final Tensor origin;
  private final Tensor domain;
  private final Tensor[][] weights;

  public MovingDomain2D(Tensor origin, BarycentricCoordinate barycentricCoordinate, Tensor domain) {
    this.origin = origin;
    this.domain = domain;
    int rows = domain.length();
    int cols = Unprotect.dimension1(domain);
    weights = new Tensor[rows][cols];
    for (int cx = 0; cx < rows; ++cx) {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = domain.get(cx, cy);
        weights[cx][cy] = barycentricCoordinate.weights(origin, point);
      }
    }
  }

  public Tensor origin() {
    return origin;
  }

  public Tensor[][] forward(Tensor target, BiinvariantMean biinvariantMean) {
    int rows = domain.length();
    int cols = Unprotect.dimension1(domain);
    Tensor[][] array = new Tensor[rows][cols];
    for (int cx = 0; cx < rows; ++cx)
      for (int cy = 0; cy < cols; ++cy)
        array[cx][cy] = biinvariantMean.mean(target, weights[cx][cy]);
    return array;
  }
}
