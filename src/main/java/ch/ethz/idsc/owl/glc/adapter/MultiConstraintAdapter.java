// code by ynager
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;

/** combines multiple PlannerConstraints
 * 
 * @see GoalAdapter */
public class MultiConstraintAdapter implements PlannerConstraint {
  /** @param constraintCollection
   * @return */
  public static PlannerConstraint of(Collection<PlannerConstraint> constraintCollection) {
    return new MultiConstraintAdapter(constraintCollection);
  }

  private final Collection<PlannerConstraint> constraintCollection;

  private MultiConstraintAdapter(Collection<PlannerConstraint> constraintCollection) {
    this.constraintCollection = constraintCollection;
  }

  @Override
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return constraintCollection.stream() //
        .allMatch(constraint -> constraint.isSatisfied(glcNode, trajectory, flow));
  }
}
