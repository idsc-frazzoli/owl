// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;

/** simple animation of small boat driving upstream, or downstream in a river delta */
/* package */ class DeltaExample {
  private static final StateIntegrator STATE_INTEGRATOR = FixedStateIntegrator.create( //
      RungeKutta45Integrator.INSTANCE, RationalScalar.of(1, 10), 4);
  private static final Tensor RANGE = Tensors.vector(9, 6.5);
  private static final Tensor OBSTACLE_IMAGE = ResourceData.of("/io/delta_free.png"); //
  static final Region<Tensor> REGION = new ImageRegion(OBSTACLE_IMAGE, RANGE, true);
  private static final PlannerConstraint PLANNER_CONSTRAINT = //
      new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(REGION));
  private static final Scalar MAX_INPUT = RealScalar.ONE;
  static final SphericalRegion SPHERICAL_REGION = //
      new SphericalRegion(Tensors.vector(2.1, 0.3), RealScalar.of(0.3));
  private static final StateTimeRaster STATE_TIME_RASTER = EtaRaster.state(Tensors.vector(8, 8));
  // ---
  private final ImageGradientInterpolation imageGradientInterpolation;
  private final StateSpaceModel stateSpaceModel;
  final TrajectoryPlanner trajectoryPlanner;

  public DeltaExample(Scalar amp) {
    imageGradientInterpolation = //
        ImageGradientInterpolation.linear(ResourceData.of("/io/delta_uxy.png"), RANGE, amp);
    stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    Scalar maxNormGradient = imageGradientInterpolation.maxNormGradient();
    Scalar maxMove = maxNormGradient.add(MAX_INPUT);
    Collection<Flow> controls = new DeltaFlows(stateSpaceModel, MAX_INPUT).getFlows(25);
    GoalInterface goalInterface = new DeltaMinTimeGoalManager(SPHERICAL_REGION, maxMove);
    trajectoryPlanner = new StandardTrajectoryPlanner( //
        STATE_TIME_RASTER, STATE_INTEGRATOR, controls, PLANNER_CONSTRAINT, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(8.8, 0.5), RealScalar.ZERO));
  }

  public RenderInterface vf(double scale) {
    return DeltaHelper.vectorFieldRender(stateSpaceModel, RANGE, REGION, RealScalar.of(scale));
  }
}
