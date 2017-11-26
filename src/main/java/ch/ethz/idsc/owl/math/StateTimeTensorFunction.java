// code by jph
package ch.ethz.idsc.owl.math;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** serializable interface for functions that map a {@link Tensor} to another {@link Tensor} */
public interface StateTimeTensorFunction extends Function<StateTime, Tensor>, Serializable {
  /** creates a StateTimeTensorFunction that does not depend on {@link StateTime#time()}
   * 
   * @param tensorUnaryOperator
   * @return function that applies {@link StateTime#state()} to given operator */
  static StateTimeTensorFunction state(TensorUnaryOperator tensorUnaryOperator) {
    return stateTime -> tensorUnaryOperator.apply(stateTime.state());
  }

  /** @return */
  static StateTimeTensorFunction withTime() {
    return StateTime::joined;
  }
}
