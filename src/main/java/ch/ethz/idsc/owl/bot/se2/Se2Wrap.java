// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Mod;

/** identifies (x,y,theta) === (x,y,theta + 2 pi n) for all n */
public class Se2Wrap implements CoordinateWrap {
  private static final int INDEX_ANGLE = 2;
  private static final Mod MOD = Mod.function(Math.PI * 2);
  private static final Mod MOD_DISTANCE = Mod.function(Math.PI * 2, -Math.PI);
  // ---
  // angular error may need a different "weight" from error in x, y
  // figure out default scaling
  private final Tensor scale;

  /** one can choose scale == {1, 1, 1}, or scale == {1, 1, 1, a, b, c, ...}
   * 
   * if more angular accuracy is required, one can choose, for instance scale == {1, 1, 2}
   * 
   * the first three values are used to weight {x, y, angle} coordinate differences.
   * the angle difference is taken modulo 2*pi mapped to the interval [-pi, pi).
   * 
   * @param scale weighs the differences in (x, y, theta).
   * the parameter scale is only used to compute distance, but not representation */
  public Se2Wrap(Tensor scale) {
    VectorQ.elseThrow(scale);
    GlobalAssert.that(INDEX_ANGLE < scale.length());
    this.scale = scale;
  }

  @Override // from CoordinateWrap
  public Tensor represent(Tensor x) {
    Tensor r = x.copy();
    r.set(MOD, INDEX_ANGLE);
    return r;
  }

  @Override // from CoordinateWrap
  public Scalar distance(Tensor p, Tensor q) {
    Tensor d = p.subtract(q);
    d.set(MOD_DISTANCE, INDEX_ANGLE);
    return Norm._2.ofVector(d.pmul(scale));
  }
}
