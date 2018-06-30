// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Rodriguez;
import ch.ethz.idsc.tensor.mat.LinearSolve;

public enum So3Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  /** p and q are orthogonal matrices with dimension 3 x 3 */
  @Override
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return p.dot(Rodriguez.exp(Rodriguez.log(LinearSolve.of(p, q)).multiply(scalar)));
  }
}
