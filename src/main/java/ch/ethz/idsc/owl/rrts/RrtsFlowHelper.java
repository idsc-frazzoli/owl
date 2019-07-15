// code by gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.function.BiFunction;

import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

public enum RrtsFlowHelper {
  ;
  public static final BiFunction<StateTime, StateTime, Tensor> U_R2 = (StateTime orig, StateTime dest) -> {
    Tensor direction = dest.state().subtract(orig.state());
    Scalar delta = dest.time().subtract(orig.time());
    return direction.divide(delta);
  };
  public static final BiFunction<StateTime, StateTime, Tensor> U_SE2 = (StateTime orig, StateTime dest) -> {
    // TODO JPH confirm correct variant
    // Tensor direction = dest.state().subtract(orig.state());
    // direction.set(So2.MOD, 2);
    Tensor direction = Se2Wrap.INSTANCE.difference(orig.state(), dest.state());
    Scalar delta = dest.time().subtract(orig.time());
    return Tensors.of(Norm._2.ofVector(Extract2D.FUNCTION.apply(direction)), //
        RealScalar.ZERO, //
        direction.Get(2)).divide(delta);
  };
}
