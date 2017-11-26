// code by jph
package ch.ethz.idsc.owl.bot.psu;

import java.util.Collection;

import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.AbstractCircularEntity;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.state.EmptyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class PsuEntity extends AbstractCircularEntity {
  public static final Tensor FALLBACK_CONTROL = Tensors.vectorDouble(0).unmodifiable();
  // ---
  private static final Integrator INTEGRATOR = RungeKutta45Integrator.INSTANCE;
  /** preserve 1[s] of the former trajectory */
  private static final Scalar DELAY_HINT = RealScalar.ONE;

  public PsuEntity() {
    super(new SimpleEpisodeIntegrator( //
        PsuStateSpaceModel.INSTANCE, //
        INTEGRATOR, //
        new StateTime(Tensors.vector(0, 0), RealScalar.ZERO)));
  }

  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    return PsuWrap.INSTANCE.distance(x, y);
  }

  @Override
  protected Tensor fallbackControl() {
    return FALLBACK_CONTROL;
  }

  @Override
  public Scalar delayHint() {
    return DELAY_HINT;
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(TrajectoryRegionQuery obstacleQuery, Tensor goal) {
    Tensor eta = Tensors.vector(6, 8);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        INTEGRATOR, RationalScalar.of(1, 4), 5);
    Collection<Flow> controls = PsuControls.createControls(0.2, 6);
    PsuWrap psuWrap = PsuWrap.INSTANCE;
    GoalInterface goalInterface = PsuGoalManager.of( //
        psuWrap, psuWrap.represent(goal.extract(0, 2)), RealScalar.of(0.2));
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta, stateIntegrator, controls, EmptyTrajectoryRegionQuery.INSTANCE, goalInterface);
    trajectoryPlanner.represent = StateTimeTensorFunction.state(psuWrap::represent);
    return trajectoryPlanner;
  }
}
