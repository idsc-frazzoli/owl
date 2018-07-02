// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.Se2GroupAction;
import ch.ethz.idsc.owl.math.map.Se2Integrator;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Tensor p_inv = new Se2GroupAction(p).inverse();
    Tensor delta = new Se2GroupAction(p_inv).circ(q);
    Tensor x = Se2Utils.log(delta).multiply(scalar);
    return Se2Integrator.INSTANCE.spin(p, x);
  }
}
