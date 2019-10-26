// code by jph
package ch.ethz.idsc.sophus.crv;

import java.util.Iterator;

import ch.ethz.idsc.sophus.math.SignedCurvature2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** @see CurvatureComb */
public enum Curvature2D {
  ;
  /** @param points of the form {{p1x, p1y}, {p2x, p2y}, ..., {pNx, pNy}}
   * @return vector */
  public static Tensor string(Tensor points) {
    int length = points.length();
    Tensor vector = Array.zeros(length);
    if (2 < length) {
      Iterator<Tensor> iterator = points.iterator();
      Tensor p = iterator.next();
      Tensor q = iterator.next();
      int index = 0;
      while (iterator.hasNext())
        vector.set( //
            SignedCurvature2D.of(p, p = q, q = iterator.next()).orElse(RealScalar.ZERO), //
            ++index);
      vector.set(vector.get(1), 0);
      vector.set(vector.get(length - 2), length - 1);
    }
    return vector;
  }
}
