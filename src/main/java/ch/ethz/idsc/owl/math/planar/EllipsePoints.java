// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;

/** @see CirclePoints */
public enum EllipsePoints {
  ;
  public static Tensor of(int n, Tensor scale) {
    return Tensor.of(CirclePoints.of(n).stream().map(row -> row.pmul(scale)));
  }

  public static Tensor of(int n, Scalar width, Scalar height) {
    return of(n, Tensors.of(width, height));
  }
}
