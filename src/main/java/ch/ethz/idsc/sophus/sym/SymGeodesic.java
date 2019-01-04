// code by jph
package ch.ethz.idsc.sophus.sym;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum SymGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return SymScalar.of(p.Get(), q.Get(), scalar);
  }
}
