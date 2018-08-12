// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Mod;

/** identifies (x,y,theta,v) === (x,y,theta + 2 pi n,v) for all n */
public enum Tse2Wrap implements CoordinateWrap {
  INSTANCE;
  // ---
  private static final int INDEX_ANGLE = 2;
  private static final Mod MOD = Mod.function(Math.PI * 2);
  private static final Mod MOD_DISTANCE = Mod.function(Math.PI * 2, -Math.PI);

  @Override // from CoordinateWrap
  public final Tensor represent(Tensor x) {
    Tensor r = x.copy();
    r.set(MOD, INDEX_ANGLE);
    return r;
  }

  @Override // from TensorDifference
  public Tensor difference(Tensor p, Tensor q) {
    Tensor tensor = q.subtract(p);
    tensor.set(MOD_DISTANCE, INDEX_ANGLE);
    return tensor;
  }
}
