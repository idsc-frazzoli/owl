// code by jph
package ch.ethz.idsc.owl.math.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Sign;

/** trajectory integration with fixed step size over given time period */
public class FixedStateIntegrator implements StateIntegrator, Serializable {
  /** @param integrator
   * @param timeStep non-negative period of one step
   * @param trajectorySize number of steps
   * @return
   * @throws Exception if given timeStep is negative */
  public static FixedStateIntegrator create(Integrator integrator, Scalar timeStep, int trajectorySize) {
    return new FixedStateIntegrator( //
        integrator, //
        Sign.requirePositiveOrZero(timeStep), //
        trajectorySize);
  }

  // ---
  private final Integrator integrator;
  private final Scalar timeStep;
  private final int trajectorySize;

  private FixedStateIntegrator(Integrator integrator, Scalar timeStep, int trajectorySize) {
    this.integrator = integrator;
    this.timeStep = timeStep;
    this.trajectorySize = trajectorySize;
  }

  @Override // from StateIntegrator
  public List<StateTime> trajectory(StateTime stateTime, Flow flow) {
    final List<StateTime> trajectory = new ArrayList<>();
    StateTime prev = stateTime;
    for (int count = 0; count < trajectorySize; ++count) {
      StateTime next = new StateTime( //
          integrator.step(flow, prev.state(), timeStep), //
          prev.time().add(timeStep));
      trajectory.add(next);
      prev = next;
    }
    return trajectory;
  }

  /** @return time advancement of trajectory which is the product of the fine grained
   * time step and the trajectory size */
  public Scalar getTimeStepTrajectory() {
    return timeStep.multiply(RealScalar.of(trajectorySize));
  }
}
