// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** References: */
public enum St1Geodesic implements GeodesicInterface {
  INSTANCE;
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Tensor delta = new St1GroupElement(p).inverse().combine(q);
    Tensor x = St1CoveringExponential.INSTANCE.log(delta).multiply(scalar);
    return St1CoveringIntegrator.INSTANCE.spin(p, x);
  }
}
