// code by jph
package ch.ethz.idsc.owl.bot.sat;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.sophus.lie.r2.Extract2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum SatelliteStateSpaceModel implements StateSpaceModel {
  INSTANCE;

  /** @param x of the form {px, py, vx, vy}
   * @param u of the form {ux, uy} */
  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    Tensor pos = Extract2D.FUNCTION.apply(x);
    Tensor vel = x.extract(2, 4);
    Tensor acc = pos.multiply(Norm._2.ofVector(pos).negate());
    return Join.of(vel, acc.add(u));
  }
}
