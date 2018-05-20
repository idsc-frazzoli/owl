// code by ynager
package ch.ethz.idsc.owl.glc.std;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

/** Implements a @PlannerConstraint to define and check flow constraints in certain regions.
 * non-empty intersection of the trajectory with the StateTime region and the flow region
 * represents a constraint violation. */
public class FlowRegionConstraint implements PlannerConstraint, Serializable {
  public static PlannerConstraint create(Region<Tensor> flowregion, Region<StateTime> region) {
    return new FlowRegionConstraint(flowregion, region);
  }
  // ---

  private final Region<Tensor> flowRegion;
  private final Region<StateTime> region;

  /** @param Flow region defining the flow constraint
   * @param StateTime region where given flow constraint should be enforced */
  private FlowRegionConstraint(Region<Tensor> flowregion, Region<StateTime> region) {
    this.flowRegion = flowregion;
    this.region = region;
  }

  @Override // from PlannerConstraint
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return !firstMember(trajectory, flow).isPresent();
  }

  private Optional<StateTime> firstMember(List<StateTime> trajectory, Flow flow) {
    Tensor u = flow.getU();
    if (flowRegion.isMember(u))
      return trajectory.stream().filter(region::isMember).findFirst();
    return Optional.empty();
  }
}
