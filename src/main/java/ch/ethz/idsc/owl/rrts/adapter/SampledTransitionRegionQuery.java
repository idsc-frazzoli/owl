// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.StateTimeCollector;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class SampledTransitionRegionQuery implements TransitionRegionQuery, StateTimeCollector {
  private final TrajectoryRegionQuery trajectoryRegionQuery;
  private final Scalar dt;

  public SampledTransitionRegionQuery(TrajectoryRegionQuery trajectoryRegionQuery, Scalar dt) {
    this.trajectoryRegionQuery = trajectoryRegionQuery;
    this.dt = dt;
  }

  @Override
  public boolean isDisjoint(Transition transition) {
    List<StateTime> list = transition.sampled(RealScalar.ZERO, RealScalar.ZERO, dt);
    return !trajectoryRegionQuery.firstMember(list).isPresent();
  }

  @Override
  public Collection<StateTime> getMembers() {
    if (trajectoryRegionQuery instanceof StateTimeCollector)
      return ((StateTimeCollector) trajectoryRegionQuery).getMembers();
    return Collections.emptyList();
  }
}
