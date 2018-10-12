// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.owl.subdiv.curve.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum RnGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return p.add(q.subtract(p).multiply(scalar));
  }
}
