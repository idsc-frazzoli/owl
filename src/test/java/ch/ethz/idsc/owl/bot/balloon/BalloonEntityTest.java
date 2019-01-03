// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class BalloonEntityTest extends TestCase {
  private static final BalloonStateSpaceModel BALLOON_STATE_SPACE_MODEL = BalloonStateSpaceModels.defaultWithoutUnits();
  private static final StateTime START = new StateTime(Tensors.vector(0, 10, 0, 0.5), RealScalar.ZERO);
  private static final EpisodeIntegrator EPISODE_INTEGRATOR = new SimpleEpisodeIntegrator( //
      BALLOON_STATE_SPACE_MODEL, EulerIntegrator.INSTANCE, START);

  static BalloonEntity createEntity() {
    return new BalloonEntity(EPISODE_INTEGRATOR, new EuclideanTrajectoryControl(), BALLOON_STATE_SPACE_MODEL);
  }

  public void testDistanceSimple() {
    Tensor x = Tensors.vector(-1, 2, 1, 0);
    Tensor y = Tensors.vector(3, 2, 4, 0);
    Scalar expected = RealScalar.of(25);
    BalloonEntity balloonEntity = createEntity();
    assertEquals(expected, balloonEntity.distance(x, y));
  }

  public void testDistanceWithUnits() {
    Tensor x = Tensors.fromString("{2[m], 2[m]}");
    Tensor y = Tensors.fromString("{4[m], 2[m]}");
    Scalar expected = Quantity.of(4, "m^2");
    BalloonEntity balloonEntity = createEntity();
    assertEquals(expected, balloonEntity.distance(x, y));
  }

  public void testDelayHint() {
    BalloonEntity balloonEntity = createEntity();
    assertEquals(balloonEntity.delayHint(), RealScalar.of(2));
  }

  public void testRender() {
    // TODO ANDRE
  }

  public void testCreateTrajectoryPlanner() {
    Tensor goal = Tensors.vector(100, 30);
    Scalar vertSpeedMax = RealScalar.of(4);
    PlannerConstraint plannerConstraint = new BalloonPlannerConstraint(vertSpeedMax);
    BalloonEntity balloonEntity = createEntity();
    TrajectoryPlanner trajectoryPlanner = balloonEntity.createTrajectoryPlanner(plannerConstraint, goal);
    assertTrue(trajectoryPlanner instanceof StandardTrajectoryPlanner);
    trajectoryPlanner.insertRoot(START);
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(10000);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    if (optional.isPresent()) {
      System.out.println(1);
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    assertTrue(optional.isPresent());
  }

  public void testStateTimeRaster() {
    BalloonEntity balloonEntity = createEntity();
    StateTimeRaster stateTimeRaster = balloonEntity.stateTimeRaster();
    assertTrue(stateTimeRaster instanceof EtaRaster);
  }
}
