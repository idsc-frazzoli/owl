// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface StateIntegrator {
  /** collects {@link StateTime}s along trajectory from starting point along flow
   * until a stop criterion is met
   * 
   * @param stateTime starting point
   * @param u control
   * @return */
  List<StateTime> trajectory(StateTime stateTime, Tensor u);
}
