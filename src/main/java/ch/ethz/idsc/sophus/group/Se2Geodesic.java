// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Mod;

/** References:
 * http://vixra.org/abs/1807.0463
 * https://www.youtube.com/watch?v=2vDciaUgL4E */
public enum Se2Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  private static final int INDEX_ANGLE = 2;
  private static final Mod MOD_DISTANCE = Mod.function(Math.PI * 2, -Math.PI);

  @Override // from GeodesicInterface
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    Tensor delta = new Se2GroupElement(p).inverse().combine(q);
    delta.set(MOD_DISTANCE, INDEX_ANGLE);
    Tensor x = Se2CoveringExponential.INSTANCE.log(delta);
    return scalar -> Se2CoveringIntegrator.INSTANCE.spin(p, x.multiply(scalar));
    // Se2GroupElement p_act = new Se2GroupElement(p);
    // Tensor delta = p_act.inverse().combine(q);
    // Tensor x = Se2CoveringExponential.INSTANCE.log(delta);
    // return scalar -> p_act.combine(Se2CoveringExponential.INSTANCE.exp(x.multiply(scalar)));
  }

  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return curve(p, q).apply(scalar);
  }
}
