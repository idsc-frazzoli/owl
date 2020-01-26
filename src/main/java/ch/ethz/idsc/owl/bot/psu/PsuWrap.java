// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Mod;

/** singleton instance */
/* package */ enum PsuWrap implements CoordinateWrap {
  INSTANCE;

  private static final Mod MOD = Mod.function(Pi.TWO);

  @Override // from CoordinateWrap
  public Tensor represent(Tensor x) {
    return Tensors.of(MOD.apply(x.Get(0)), x.Get(1));
  }

  @Override // from TensorDifference
  public Tensor difference(Tensor p, Tensor q) {
    Tensor d = p.subtract(q);
    d.set(So2.MOD, 0);
    return d;
  }
}
