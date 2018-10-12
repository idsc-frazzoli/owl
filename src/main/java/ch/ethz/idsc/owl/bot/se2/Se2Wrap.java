// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.owl.math.map.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.map.Se2CoveringGroupElement;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Mod;

/** identifies (x,y,theta) === (x,y,theta + 2 pi n) for all n
 * 
 * representation of angles is in the interval [0, 2pi)
 * 
 * differences are mapped to [-pi, pi)
 * 
 * @see Se2CoveringWrap */
public enum Se2Wrap implements CoordinateWrap {
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
    Tensor tensor = new Se2CoveringGroupElement(p).inverse().combine(q);
    tensor.set(MOD_DISTANCE, INDEX_ANGLE);
    return Se2CoveringExponential.INSTANCE.log(tensor);
  }
}
