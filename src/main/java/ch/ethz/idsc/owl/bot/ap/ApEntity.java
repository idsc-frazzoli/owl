// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.FallbackControl;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ class ApEntity extends TrajectoryEntity {
  /* Setting up parameters for the ApComboRegion
   * Note: GOAL and RADIUS_VECTOR are 3D, since x is omitted in ApComboRegion */
  final static Tensor GOAL = Tensors.vector(5, 60, 0); // goal = {zCenter,vCenter, gammaCenter}
  final static Tensor RADIUS_VECTOR = Tensors.of(RealScalar.of(5), RealScalar.of(200), Degree.of(50)); // radius_vector = {zRadius,vRadius, GammaRadius}
  final static Scalar MAX_AOA = ApStateSpaceModel.MAX_AOA;
  final static int THRUST_PARTIONING = 3;
  final static Tensor THRUSTS = Subdivide.of( //
      ApStateSpaceModel.MAX_THRUST.zero(), //
      ApStateSpaceModel.MAX_THRUST, //
      THRUST_PARTIONING);
  final static int FLOWRES = 2;
  final static FlowsInterface AP_FLOWS = ApFlows.of(MAX_AOA, THRUSTS);
  static final Integrator INTEGRATOR = RungeKutta4Integrator.INSTANCE;
  final static Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(1), RealScalar.of(1), RealScalar.of(1), Degree.of(1)).unmodifiable();
  private static final Scalar DELAY_HINT = RealScalar.ONE;

  public ApEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
    add(new FallbackControl(Array.zeros(1)));
  }

  static protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(PARTITIONSCALE, StateTimeTensorFunction.state(ApWrap.INSTANCE::represent));
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    return Norm._2.ofVector(ApWrap.INSTANCE.difference(x, y));
  }

  @Override // from TrajectoryEntity
  public Scalar delayHint() {
    return DELAY_HINT;
  }

  @Override // from TrajectoryEntity
  public TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        INTEGRATOR, RationalScalar.of(1, 5), 3);
    Collection<Flow> controls = AP_FLOWS.getFlows(FLOWRES);
    ApComboRegion apComboRegion = ApComboRegion.createApRegion(goal, RADIUS_VECTOR);
    ApMinTimeGoalManager apMinTimeGoalManager = new ApMinTimeGoalManager(apComboRegion, ApStateSpaceModel.Z_DOT_FLIGHT_MAX);
    GoalInterface goalInterface = apMinTimeGoalManager.getGoalInterface();
    return new StandardTrajectoryPlanner(stateTimeRaster(), stateIntegrator, controls, plannerConstraint, goalInterface);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(trajectoryWrap)) {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(trajectoryWrap.trajectory());
      trajectoryRender.render(geometricLayer, graphics);
    }
    { // indicate current position
      Tensor state = getStateTimeNow().state();
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(64, 128, 64, 192));
      graphics.fill(new Ellipse2D.Double(point.getX() - 2, point.getY() - 2, 7, 7));
    }
    { // indicate position 1[s] into the future
      Tensor state = getEstimatedLocationAt(delayHint());
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(255, 128, 128 - 64, 128 + 64));
      graphics.fill(new Rectangle2D.Double(point.getX() - 2, point.getY() - 2, 5, 5));
    }
    // ---
    if (Objects.nonNull(trajectoryWrap)) {
      StateTime stateTime = getStateTimeNow();
      Scalar now = stateTime.time();
      if (trajectoryWrap.isDefined(now)) {
        TrajectorySample trajectorySample = trajectoryWrap.getSample(now);
        Path2D path2d = geometricLayer.toPath2D(Tensors.of(stateTime.state(), trajectorySample.stateTime().state()));
        graphics.setColor(Color.PINK);
        graphics.draw(path2d);
      }
    }
  }
}
