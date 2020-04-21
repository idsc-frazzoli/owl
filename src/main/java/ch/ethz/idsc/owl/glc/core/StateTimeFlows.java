// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.util.Collection;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface StateTimeFlows {
  Collection<Tensor> flows(StateTime stateTime);
}
