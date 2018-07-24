// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2CoveringGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Se2CoveringGroupAction p_act = new Se2CoveringGroupAction(p);
    Tensor p_inv = p_act.inverse();
    Tensor delta = new Se2CoveringGroupAction(p_inv).combine(q);
    Tensor x = Se2CoveringExponential.INSTANCE.log(delta).multiply(scalar);
    Tensor m = Se2CoveringExponential.INSTANCE.exp(x);
    return p_act.combine(m);
  }
}
