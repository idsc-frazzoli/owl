// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public enum So2Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    Tensor log = So2Exponential.INSTANCE.log(q.subtract(p));
    return scalar -> p.add(So2Exponential.INSTANCE.exp(log.multiply(scalar)));
  }

  /** p and q are orthogonal matrices with dimension 2 x 2 */
  @Override // from GeodesicInterface
  public Scalar split(Tensor p, Tensor q, Scalar scalar) {
    return curve(p, q).apply(scalar).Get();
  }
}
