// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Mod;

/** identifies (x,z,v,gamma) === (x,z, v,gamma + 2 pi n) for all n */
public enum ApWrap implements CoordinateWrap {
  INSTANCE;
  // ---
  private static final int INDEX_ANGLE = 3;
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
