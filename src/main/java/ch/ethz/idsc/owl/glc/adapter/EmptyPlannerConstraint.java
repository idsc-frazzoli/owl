// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

/** adapter for planning without constraint, for instance no obstacles
 * the planning then only depends on the cost function and heuristic */
public enum EmptyPlannerConstraint implements PlannerConstraint {
  INSTANCE;

  @Override // from PlannerConstraint
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return true;
  }
}
