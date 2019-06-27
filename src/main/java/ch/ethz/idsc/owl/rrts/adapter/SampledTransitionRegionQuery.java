// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.StateTimeCollector;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class SampledTransitionRegionQuery implements TransitionRegionQuery, StateTimeCollector, Serializable {
  private final Region<Tensor> region;
  private final Scalar dt;

  public SampledTransitionRegionQuery(Region<Tensor> region, Scalar dt) {
    this.region = region;
    this.dt = dt;
  }

  @Override
  public boolean isDisjoint(Transition transition) {
    return transition.sampled(dt).stream() //
        .noneMatch(region::isMember);
  }

  @Override
  public Collection<StateTime> getMembers() {
    // if (trajectoryRegionQuery instanceof StateTimeCollector)
    // return ((StateTimeCollector) trajectoryRegionQuery).getMembers();
    return Collections.emptyList();
  }
}
