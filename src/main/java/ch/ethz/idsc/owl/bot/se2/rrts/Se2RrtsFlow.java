// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

public class Se2RrtsFlow {
  public static Tensor uBetween(StateTime orig, StateTime dest) {
    // TODO JPH confirm correct variant
    // Tensor direction = dest.state().subtract(orig.state());
    // direction.set(So2.MOD, 2);
    Tensor direction = Se2Wrap.INSTANCE.difference(orig.state(), dest.state());
    Scalar delta = dest.time().subtract(orig.time());
    return Tensors.of( //
        Norm._2.ofVector(Extract2D.FUNCTION.apply(direction)), // vx
        RealScalar.ZERO, //
        direction.Get(2)).divide(delta);
  }
}
