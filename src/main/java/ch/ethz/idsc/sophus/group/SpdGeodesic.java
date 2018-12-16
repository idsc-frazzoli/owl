// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** see for instance
 * Riemannian Variance Filtering: An Independent Filtering Scheme for Statistical
 * Tests on Manifold-valued Data */
public enum SpdGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    throw new UnsupportedOperationException(); // TODO not implemented yet
  }
}
