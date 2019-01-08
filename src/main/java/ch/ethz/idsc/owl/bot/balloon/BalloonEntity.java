// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.ani.api.AbstractCircularEntity;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.red.Norm2Squared;

/* package */ class BalloonEntity extends AbstractCircularEntity {
  private static final Tensor PARTITIONSCALE = Tensors.vector(2, 2, 1, 1).unmodifiable();
  protected static final FixedStateIntegrator FIXED_STATE_INTEGRATOR = //
      FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.of(1, 5), 3);
  static final int FLOWRES = 3;
  // TODO ANDRE Look up realistic values and adapt accordingly + allocate to BalloonStateSpaceModels
  static final Scalar U_MAX = RealScalar.of(10);
  // TODO ANDRE adapt when heuristic is changed
  final static Scalar SPEED_MAX = RealScalar.of(10);
  /** preserve 1[s] of the former trajectory */
  private static final Scalar DELAY_HINT = RealScalar.of(2);
  private static final Scalar GOAL_RADIUS = RealScalar.of(3);
  // ---
  /***************************************************/
  private final StateSpaceModel stateSpaceModel;
  private BufferedImage bufferedImage;

  /***************************************************/
  public BalloonEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl, StateSpaceModel stateSpaceModel) {
    super(episodeIntegrator, trajectoryControl);
    add(new BalloonFallbackControl());
    this.stateSpaceModel = stateSpaceModel;
    bufferedImage = ResourceData.bufferedImage("/graphics/hotairballoon.png");
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    // TODO ANDRE maybe change to norm2
    return Norm2Squared.between(x, y);
  }

  @Override // from TrajectoryEntity
  public Scalar delayHint() {
    return DELAY_HINT;
  }

  @Override // from TrajectoryEntity
  public final TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    Collection<Flow> controls = BalloonFlows.of(U_MAX, stateSpaceModel).getFlows(FLOWRES);
    BalloonMinTimeGoalManager balloonMinTimeGoalManager = //
        new BalloonMinTimeGoalManager(goal.extract(0, 2), GOAL_RADIUS, SPEED_MAX);
    GoalInterface goalInterface = balloonMinTimeGoalManager.getGoalInterface();
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXED_STATE_INTEGRATOR, controls, plannerConstraint, goalInterface);
  }

  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.state(PARTITIONSCALE);
  }

  @Override // from AbstractCircularEntity
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO ANDRE create meaningful render function
    // RegionRenders.draw(geometricLayer, graphics, goalRegion);
    if (Objects.nonNull(trajectoryWrap)) {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(trajectoryWrap.trajectory());
      trajectoryRender.setColor(Color.GREEN);
      trajectoryRender.render(geometricLayer, graphics);
    }
    // ---
    { // indicate current position
      Tensor state = getStateTimeNow().state();
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(64, 128, 64, 192));
      graphics.fill(new Ellipse2D.Double(point.getX() - 2, point.getY() - 2, 7, 7));
      // graphics.drawImage(bufferedImage, (int) point.getX(), (int) point.getY(), null);
      ImageRender imageRender = ImageRender.of(bufferedImage, Tensors.vector(1, 1));
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(state));
      imageRender.render(geometricLayer, graphics);
      geometricLayer.popMatrix();
    }
  }
}
