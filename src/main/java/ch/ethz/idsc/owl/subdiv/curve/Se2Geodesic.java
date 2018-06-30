// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.Se2Integrator;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.LinearSolve;

public enum Se2Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    // TODO provide dedicated functions
    Tensor tensor = LinearSolve.of(Se2Utils.toSE2Matrix(p), Se2Utils.toSE2Matrix(q));
    Tensor g = Se2Utils.fromSE2Matrix(tensor);
    Tensor x = Se2Utils.log(g).multiply(scalar);
    return Se2Integrator.INSTANCE.spin(p, x);
  }
}
