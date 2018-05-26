// code by jph
package ch.ethz.idsc.owl.math.state;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Tensor;

/** container class that bundles information to follow a trajectory */
public class TrajectorySample implements Serializable {
  /** @param stateTime
   * @return first entry of a trajectory that does not specify flow */
  public static TrajectorySample head(StateTime stateTime) {
    return new TrajectorySample(stateTime, null);
  }

  // ---
  private final StateTime stateTime;
  private final Flow flow;

  /** @param stateTime
   * @param flow may be null */
  public TrajectorySample(StateTime stateTime, Flow flow) {
    this.stateTime = stateTime;
    this.flow = flow;
  }

  public StateTime stateTime() {
    return stateTime;
  }

  /** typically the first state time in a trajectory
   * may not have a flow associated
   * (since there may not be history for the sample)
   * 
   * We return type {@link Optional} to make the application
   * layer aware of the possibility that flow may not be present.
   * 
   * @return Optional.ofNullable(flow) */
  public Optional<Flow> getFlow() {
    return Optional.ofNullable(flow);
  }

  public Optional<Tensor> getControl() {
    return Optional.ofNullable(Objects.isNull(flow) ? null : flow.getU());
  }

  public String toInfoString() {
    String ustring = Objects.isNull(flow) ? "null" : flow.getU().toString();
    return stateTime.toInfoString() + "  u=" + ustring;
  }
}
