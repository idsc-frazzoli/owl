// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

// TODO is this beneficial: CoordinateWrap<StateTime> ?
public class StateTimeCoordinateWrap implements StateTimeTensorFunction {
  private final CoordinateWrap coordinateWrap;

  public StateTimeCoordinateWrap(CoordinateWrap coordinateWrap) {
    this.coordinateWrap = coordinateWrap;
  }

  @Override
  public Tensor apply(StateTime stateTime) {
    return coordinateWrap.represent(stateTime.state()).append(stateTime.time());
  }
}
