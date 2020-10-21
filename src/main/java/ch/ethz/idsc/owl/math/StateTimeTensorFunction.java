// code by jph
package ch.ethz.idsc.owl.math;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

/** serializable interface for functions that map a {@link StateTime} to a {@link Tensor} */
public enum StateTimeTensorFunction {
  ;
  /** creates a StateTimeTensorFunction that does not depend on {@link StateTime#time()}
   * 
   * @param tensorUnaryOperator
   * @return function that applies {@link StateTime#state()} to given operator */
  @SuppressWarnings("unchecked")
  public static Function<StateTime, Tensor> state(TensorUnaryOperator tensorUnaryOperator) {
    return (Function<StateTime, Tensor> & Serializable) //
    stateTime -> tensorUnaryOperator.apply(stateTime.state());
  }
}
