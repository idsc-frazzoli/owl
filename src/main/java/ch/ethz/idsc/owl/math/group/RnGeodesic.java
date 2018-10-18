// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** geodesics in the Euclidean space R^n are straight lines */
public enum RnGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return p.add(q.subtract(p).multiply(scalar));
  }
}
