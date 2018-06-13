// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.awt.Graphics2D;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.AbstractCircularEntity;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Chop;

/** class controls delta using {@link StandardTrajectoryPlanner} */
/* package */ class DeltaEntity extends AbstractCircularEntity {
  protected static final Tensor PARTITION_SCALE = Tensors.vector(5, 5).unmodifiable();
  protected static final FixedStateIntegrator FIXED_STATE_INTEGRATOR = FixedStateIntegrator.create( //
      RungeKutta45Integrator.INSTANCE, RationalScalar.of(1, 5), 4);
  /** preserve 1[s] of the former trajectory */
  private static final Scalar DELAY_HINT = RealScalar.of(2);
  // ---
  /** the constants define the control */
  private static final Scalar U_NORM = RealScalar.of(0.6);
  /** resolution of radial controls */
  private static final int U_SIZE = 15;
  private static final Scalar GOAL_RADIUS = RealScalar.of(.3);
  /***************************************************/
  private final ImageGradientInterpolation imageGradientInterpolation;
  private RegionWithDistance<Tensor> goalRegion = null;

  public DeltaEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl, ImageGradientInterpolation imageGradientInterpolation) {
    super(episodeIntegrator, trajectoryControl);
    add(new DeltaCoastingControl(imageGradientInterpolation, U_NORM));
    this.imageGradientInterpolation = imageGradientInterpolation;
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    return Norm2Squared.between(x, y); // non-negative
  }

  @Override
  public Scalar delayHint() {
    return DELAY_HINT;
  }

  /** @param goal
   * @return */
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new SphericalRegion(goal.extract(0, 2), GOAL_RADIUS);
  }

  @Override
  public final TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    Collection<Flow> controls = new DeltaFlows(stateSpaceModel, U_NORM).getFlows(U_SIZE);
    Scalar u_norm = DeltaControls.maxSpeed(controls);
    GlobalAssert.that(Chop._10.close(u_norm, U_NORM));
    Scalar maxMove = stateSpaceModel.getLipschitz().add(u_norm);
    goalRegion = getGoalRegionWithDistance(goal);
    GoalInterface goalInterface = new DeltaMinTimeGoalManager(goalRegion, maxMove);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXED_STATE_INTEGRATOR, controls, plannerConstraint, goalInterface);
  }

  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.state(PARTITION_SCALE);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RegionRenders.draw(geometricLayer, graphics, goalRegion);
    // ---
    super.render(geometricLayer, graphics);
  }
}
