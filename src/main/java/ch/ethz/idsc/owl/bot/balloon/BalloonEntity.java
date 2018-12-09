// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collection;

import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.AbstractCircularEntity;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
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

/*package*/ class BalloonEntity extends AbstractCircularEntity {
  final static Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(1), RealScalar.of(1), RealScalar.of(1), RealScalar.of(1)).unmodifiable();
  protected static final FixedStateIntegrator FIXED_STATE_INTEGRATOR = FixedStateIntegrator.create( //
      RungeKutta4Integrator.INSTANCE, RationalScalar.of(1, 5), 3);
  final static int FLOWRES = 2;
  // TODO Look up realistic values and adapt accordingly + allocate to BalloonStateSpaceModels
  final static Scalar U_MAX = RealScalar.of(20);
  // TODO adapt when heuristic is changed
  final static Scalar SPEED_MAX = RealScalar.of(20);
  /** preserve 1[s] of the former trajectory */
  private static final Scalar DELAY_HINT = RealScalar.of(2);
  private static final Scalar GOAL_RADIUS = RealScalar.of(.3);
  // ---
  /***************************************************/
  private final StateSpaceModel stateSpaceModel;

  /***************************************************/
  public BalloonEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl, StateSpaceModel stateSpaceModel) {
    super(episodeIntegrator, trajectoryControl);
    add(new BalloonFallbackControl());
    this.stateSpaceModel = stateSpaceModel;
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    // TODO maybe change to norm2
    return Norm2Squared.between(x, y);
  }

  @Override // from TrajectoryEntity
  public Scalar delayHint() {
    return DELAY_HINT;
  }

  /** @param goal vector being two dimensional {x,y}
   * @return new spherical region with center {x_goal, z_goal} and radius GOAL_RADIUS */
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new SphericalRegion(goal.extract(0, 2), GOAL_RADIUS);
  }

  @Override // from TrajectoryEntity
  public final TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    Collection<Flow> controls = BalloonFlows.of(U_MAX, stateSpaceModel).getFlows(FLOWRES);
    RegionWithDistance<Tensor> goalRegion = getGoalRegionWithDistance(goal);
    BalloonMinTimeGoalManager balloonMinTimeGoalManager = new BalloonMinTimeGoalManager(goalRegion, SPEED_MAX);
    GoalInterface goalInterface = balloonMinTimeGoalManager.getGoalInterface();
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXED_STATE_INTEGRATOR, controls, plannerConstraint, goalInterface);
  }

  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.state(PARTITIONSCALE);
  }

  @Override // from AbstractCircularEntity
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO create meaningful render function
    // RegionRenders.draw(geometricLayer, graphics, goalRegion);
    // ---
    { // indicate current position
      Tensor state = getStateTimeNow().state();
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(64, 128, 64, 192));
      graphics.fill(new Ellipse2D.Double(point.getX() - 2, point.getY() - 2, 7, 7));
    }
  }
}
