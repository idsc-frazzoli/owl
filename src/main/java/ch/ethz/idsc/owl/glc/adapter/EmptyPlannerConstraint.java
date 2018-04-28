// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;

public enum EmptyPlannerConstraint implements PlannerConstraint {
  INSTANCE;
  // ---
  @Override
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return true;
  }
}
