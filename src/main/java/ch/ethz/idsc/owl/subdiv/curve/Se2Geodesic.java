// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.owl.math.map.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Mod;

public enum Se2Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  private static final int INDEX_ANGLE = 2;
  private static final Mod MOD_DISTANCE = Mod.function(Math.PI * 2, -Math.PI);

  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Tensor p_inv = new Se2CoveringGroupAction(p).inverse();
    Tensor delta = new Se2CoveringGroupAction(p_inv).combine(q);
    delta.set(MOD_DISTANCE, INDEX_ANGLE);
    Tensor x = Se2CoveringExponential.INSTANCE.log(delta).multiply(scalar);
    return Se2CoveringIntegrator.INSTANCE.spin(p, x);
  }
}
