// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.owl.math.map.Se2Integrator;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2CoveringGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Tensor p_inv = new Se2CoveringGroupAction(p).inverse();
    Tensor delta = new Se2CoveringGroupAction(p_inv).combine(q);
    Tensor x = Se2Utils.log(delta).multiply(scalar);
    return Se2Integrator.INSTANCE.spin(p, x);
  }
}
