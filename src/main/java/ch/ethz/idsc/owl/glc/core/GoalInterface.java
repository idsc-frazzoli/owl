// code by jph
package ch.ethz.idsc.owl.glc.core;

import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;

/** the cost to goal and the goal region have to be compatible in order
 * for the planner to work properly.
 * 
 * therefore the two concepts of distance and region query
 * are assembled into one {@link GoalInterface} */
public interface GoalInterface extends CostFunction, TrajectoryRegionQuery {
  // ---
}
