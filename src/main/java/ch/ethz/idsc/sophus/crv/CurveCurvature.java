// code by jph
package ch.ethz.idsc.sophus.crv;

import ch.ethz.idsc.sophus.math.SignedCurvature2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

public enum CurveCurvature {
  ;
  /** @param points of the form {{p1x, p1y}, {p2x, p2y}, ..., {pNx, pNy}}
   * @return vector */
  public static Tensor string(Tensor points) {
    int length = points.length();
    Tensor vector = Array.zeros(length);
    int last = length - 1;
    for (int index = 1; index < last; ++index)
      vector.set(SignedCurvature2D.of( //
          points.get(index - 1), //
          points.get(index + 0), //
          points.get(index + 1) //
      ).orElse(RealScalar.ZERO), index);
    if (2 < length) {
      vector.set(vector.get(1), 0);
      vector.set(vector.get(length - 2), last);
    }
    return vector;
  }
}
