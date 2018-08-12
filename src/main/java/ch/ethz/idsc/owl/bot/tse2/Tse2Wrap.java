// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.io.Serializable;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.Mod;

/** identifies (x,y,theta,v) === (x,y,theta + 2 pi n,v) for all n */
public class Tse2Wrap implements CoordinateWrap, Serializable {
  protected static final int INDEX_ANGLE = 2;
  protected static final Mod MOD = Mod.function(Math.PI * 2);
  protected static final Mod MOD_DISTANCE = Mod.function(Math.PI * 2, -Math.PI);
  // ---
  protected final Tensor scale;

  /** if more angular accuracy is required, one can choose, for instance scale == {1, 1, 2, 1}
   * 
   * the first four values are used to weight {x, y, angle, vel} coordinate differences.
   * the angle difference is taken modulo 2*pi mapped to the interval [-pi, pi).
   * 
   * @param scale weighs the differences in (x, y, theta, vel).
   * the parameter scale is only used to compute distance, but not representation */
  public Tse2Wrap(Tensor scale) {
    GlobalAssert.that(INDEX_ANGLE < scale.length());
    this.scale = VectorQ.require(scale);
  }

  @Override // from CoordinateWrap
  public final Tensor represent(Tensor x) {
    Tensor r = x.copy();
    r.set(MOD, INDEX_ANGLE);
    return r;
  }

  // @Override // from TensorMetric
  // public final Scalar distance(Tensor p, Tensor q) {
  // Tensor d = difference(p, q);
  // d.set(MOD_DISTANCE, INDEX_ANGLE);
  // return Norm._2.ofVector(d.pmul(scale));
  // }
  /** default difference is simply the vector difference
   * 
   * @param p
   * @param q
   * @return */
  @Override
  public Tensor difference(Tensor p, Tensor q) {
    return p.subtract(q);
  }
}
