// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

public class StateTimeCoordinateWrap implements Function<StateTime, Tensor>, Serializable {
  private final CoordinateWrap coordinateWrap;

  public StateTimeCoordinateWrap(CoordinateWrap coordinateWrap) {
    this.coordinateWrap = coordinateWrap;
  }

  @Override
  public Tensor apply(StateTime stateTime) {
    return coordinateWrap.represent(stateTime.state()).append(stateTime.time());
  }
}
