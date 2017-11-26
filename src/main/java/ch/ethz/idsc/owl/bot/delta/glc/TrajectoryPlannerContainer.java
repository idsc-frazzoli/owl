// code by jl
package ch.ethz.idsc.owl.bot.delta.glc;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.par.Parameters;
import ch.ethz.idsc.owl.math.StateSpaceModel;

/** A Container, which contains a {@link TrajectoryPlanner} and its linked objects:
 * Currently:
 * {@link Parameters}
 * {@link StateSpaceModel} */
/* package */ class TrajectoryPlannerContainer {
  private final TrajectoryPlanner trajectoryPlanner;
  private final Parameters parameters;
  private final StateSpaceModel stateSpaceModel;

  public TrajectoryPlannerContainer( //
      TrajectoryPlanner trajectoryPlanner, //
      Parameters parameters, //
      StateSpaceModel stateSpaceModel) {
    this.trajectoryPlanner = trajectoryPlanner;
    this.parameters = parameters;
    this.stateSpaceModel = stateSpaceModel;
  }

  public TrajectoryPlanner getTrajectoryPlanner() {
    return trajectoryPlanner;
  }

  public Parameters getParameters() {
    return parameters;
  }

  public StateSpaceModel getStateSpaceModel() {
    return stateSpaceModel;
  }
}
