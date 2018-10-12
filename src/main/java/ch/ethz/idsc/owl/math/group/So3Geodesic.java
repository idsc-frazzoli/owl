// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.mat.LinearSolve;

public enum So3Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  /** p and q are orthogonal matrices with dimension 3 x 3 */
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return p.dot(Rodrigues.exp(Rodrigues.log(LinearSolve.of(p, q)).multiply(scalar)));
  }
}
