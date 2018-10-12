// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.map.Se2CoveringGroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2CoveringGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Se2CoveringGroupElement p_act = new Se2CoveringGroupElement(p);
    Tensor delta = p_act.inverse().combine(q);
    Tensor x = Se2CoveringExponential.INSTANCE.log(delta).multiply(scalar);
    Tensor m = Se2CoveringExponential.INSTANCE.exp(x);
    return p_act.combine(m);
  }
}
