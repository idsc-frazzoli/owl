// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Mod;

/** singleton instance */
/* package */ enum PsuWrap implements CoordinateWrap {
  INSTANCE;
  // ---
  private static final Mod MOD = Mod.function(Math.PI * 2);
  private static final Mod MOD_DISTANCE = Mod.function(Math.PI * 2, -Math.PI);

  @Override // from CoordinateWrap
  public Tensor represent(Tensor x) {
    return Tensors.of(MOD.apply(x.Get(0)), x.Get(1));
  }

  @Override // from CoordinateWrap
  public Tensor difference(Tensor p, Tensor q) {
    Tensor d = p.subtract(q);
    d.set(MOD_DISTANCE, 0);
    return d;
  }
}
