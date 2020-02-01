// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.io.Serializable;
import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;

public class InvariantFlows implements StateTimeFlows, Serializable {
  private final Collection<Flow> collection;

  public InvariantFlows(Collection<Flow> collection) {
    this.collection = collection;
  }

  @Override
  public Collection<Flow> flows(StateTime stateTime) {
    return collection;
  }
}
