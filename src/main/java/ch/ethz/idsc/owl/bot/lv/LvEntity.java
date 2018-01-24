// code by jph
package ch.ethz.idsc.owl.bot.lv;

import java.util.Collection;

import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.AbstractCircularEntity;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class LvEntity extends AbstractCircularEntity {
  private static final Integrator INTEGRATOR = RungeKutta45Integrator.INSTANCE;
  // ---
  private final Collection<Flow> controls;

  /** @param state initial position of entity */
  public LvEntity(TrajectoryControl trajectoryControl, Collection<Flow> controls) {
    super(trajectoryControl);
    this.controls = controls;
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.ONE;
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(TrajectoryRegionQuery obstacleQuery, Tensor goal) {
    Tensor partitionScale = Tensors.vector(8, 8);
    StateIntegrator stateIntegrator = //
        FixedStateIntegrator.create(INTEGRATOR, RationalScalar.of(1, 12), 4);
    GoalInterface goalInterface = LvGoalInterface.create(goal.extract(0, 2), Tensors.vector(0.2, 0.2));
    return new StandardTrajectoryPlanner( //
        partitionScale, stateIntegrator, controls, obstacleQuery, goalInterface);
  }
}
