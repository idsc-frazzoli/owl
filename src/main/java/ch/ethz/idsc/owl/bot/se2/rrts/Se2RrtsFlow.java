// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

public enum Se2RrtsFlow {
  ;
  /** @param orig
   * @param dest
   * @return */
  public static Tensor uBetween(StateTime orig, StateTime dest) {
    Tensor log = Se2Wrap.INSTANCE.difference(orig.state(), dest.state());
    Scalar delta = dest.time().subtract(orig.time());
    // TODO GJOEL/JPH test and possibly replace norm with hypot
    // Scalar vx = Hypot.of(log.Get(0), log.Get(1));
    Scalar vx = Norm._2.ofVector(Extract2D.FUNCTION.apply(log));
    return Tensors.of(vx, vx.zero(), log.Get(2)).divide(delta);
  }
}
