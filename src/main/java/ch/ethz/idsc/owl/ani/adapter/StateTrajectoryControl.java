// code by jph
package ch.ethz.idsc.owl.ani.adapter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.ArgMin;

/** trajectory control for a time-invariant state-space */
public abstract class StateTrajectoryControl implements TrajectoryControl, Serializable {
  // ---
  private List<TrajectorySample> trajectory = null;
  private int trajectory_skip = 0;

  @Override // from TrajectoryListener
  public final synchronized void trajectory(List<TrajectorySample> trajectory) {
    this.trajectory = trajectory;
    trajectory_skip = 0;
  }

  @Override // from EntityControl
  public final synchronized Optional<Tensor> control(StateTime tail, Scalar now) {
    // implementation does not require that current position is perfectly located on trajectory
    if (Objects.nonNull(trajectory)) {
      final int argmin = indexOfPassedTrajectorySample(tail, trajectory.subList(trajectory_skip, trajectory.size()));
      if (argmin == ArgMin.EMPTY)
        throw new RuntimeException();
      int index = trajectory_skip + argmin;
      trajectory_skip = index;
      ++index; // <- next node has flow control
      if (index < trajectory.size()) {
        Optional<Tensor> optional = customControl(tail, trajectory.subList(index, trajectory.size()));
        if (optional.isPresent())
          return optional;
        Tensor flow = trajectory.get(index).getFlow().get();
        return Optional.of(flow);
      }
      trajectory = resetAction(trajectory);
    }
    return Optional.empty();
  }

  @Override
  public final synchronized List<TrajectorySample> getFutureTrajectoryUntil(StateTime tail, Scalar delay) {
    if (Objects.isNull(trajectory)) // agent does not have a trajectory
      return Collections.singletonList(TrajectorySample.head(tail)); // delay is not added
    int index = trajectory_skip + indexOfPassedTrajectorySample(tail, trajectory.subList(trajectory_skip, trajectory.size()));
    // <- no update of trajectory_skip here
    Scalar threshold = trajectory.get(index).stateTime().time().add(delay);
    return trajectory.stream().skip(index) //
        .filter(Trajectories.untilTime(threshold)) //
        .collect(Collectors.toList());
  }

  /** function determines closest point of trajectory to current state
   * 
   * @param x from trajectory
   * @param y present state of entity
   * @return distance mapped by a monotonous function, for instance Norm2Squared instead of Norm._2 */
  protected abstract Scalar pseudoDistance(Tensor x, Tensor y);

  /** @param tail
   * @param trailAhead
   * @return */
  protected abstract Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead);

  /** @param trajectory
   * @return */
  private static List<TrajectorySample> resetAction(List<TrajectorySample> trajectory) {
    // TODO JPH api not clear
    // System.err.println("out of trajectory");
    return null;
  }

  /** the return index does not refer to node in the trajectory closest to the entity
   * but rather the index of the node that was already traversed.
   * this ensures that the entity can query the correct flow that leads to the upcoming node
   * 
   * @param trajectory
   * @return index of node that has been traversed most recently by entity */
  private int indexOfPassedTrajectorySample(StateTime tail, List<TrajectorySample> trajectory) {
    final Tensor y = tail.state();
    Tensor dist = Tensor.of(trajectory.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(state -> pseudoDistance(state, y)));
    int argmin = ArgMin.of(dist);
    // the below 'correction' does not help in tracking
    // instead one could try blending flows depending on distance
    // if (0 < argmin && argmin < dist.length() - 1)
    // if (Scalars.lessThan(dist.Get(argmin - 1), dist.Get(argmin + 1)))
    // --argmin;
    return argmin;
  }

  @Override // from EntityControl
  public final ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }
}
