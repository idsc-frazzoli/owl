// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.io.Serializable;
import java.util.Collection;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

public class InvariantFlows implements StateTimeFlows, Serializable {
  private final Collection<Tensor> collection;

  public InvariantFlows(Collection<Tensor> collection) {
    this.collection = collection;
  }

  @Override
  public Collection<Tensor> flows(StateTime stateTime) {
    return collection;
  }
}
