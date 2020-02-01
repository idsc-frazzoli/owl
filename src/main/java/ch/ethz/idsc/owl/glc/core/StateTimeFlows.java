package ch.ethz.idsc.owl.glc.core;

import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;

@FunctionalInterface
public interface StateTimeFlows {
  Collection<Flow> flows(StateTime stateTime);
}
