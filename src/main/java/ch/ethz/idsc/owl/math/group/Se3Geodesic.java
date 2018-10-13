// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.LinearSolve;

public enum Se3Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return p.dot(Se3Exponential.INSTANCE.exp(Se3Exponential.INSTANCE.log(LinearSolve.of(p, q)).multiply(scalar)));
  }
}
