// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.LinearSolve;

/** geodesic in special Euclidean group SE(3) of affine transformations
 * 
 * input p and q are 4 x 4 matrices that encode affine transformations */
public enum Se3Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return p.dot(Se3Exponential.INSTANCE.exp(Se3Exponential.INSTANCE.log(LinearSolve.of(p, q)).multiply(scalar)));
  }
}
