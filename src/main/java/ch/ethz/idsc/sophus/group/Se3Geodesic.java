// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** geodesic in special Euclidean group SE(3) of affine transformations
 * 
 * input p and q are 4 x 4 matrices that encode affine transformations
 * 
 * @see LinearGroup
 * @see LieGroupElement */
public enum Se3Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from TensorGeodesic
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    Tensor log = Se3Exponential.INSTANCE.log(LinearSolve.of(p, q));
    return scalar -> p.dot(Se3Exponential.INSTANCE.exp(log.multiply(scalar)));
  }

  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return curve(p, q).apply(scalar);
  }
}
