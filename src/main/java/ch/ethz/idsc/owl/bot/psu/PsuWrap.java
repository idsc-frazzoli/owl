// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Mod;

public enum PsuWrap implements CoordinateWrap {
  INSTANCE;
  // ---
  private static Mod MOD = Mod.function(Math.PI * 2);
  private static Mod MOD_DISTANCE = Mod.function(Math.PI * 2, -Math.PI);

  @Override
  public Tensor represent(Tensor x) {
    return Tensors.of(MOD.apply(x.Get(0)), x.Get(1));
  }

  @Override
  public Scalar distance(Tensor p, Tensor q) {
    Tensor d = p.subtract(q);
    d.set(MOD_DISTANCE, 0);
    return Norm._2.ofVector(d); // mix of units [rad] and [rad/sec] (!)
  }
}
