// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
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
  BalloonStateSpaceModel balloonStateSpaceModel = BalloonStateSpaceModels.defaultWithoutUnits();
  StateTime stateTime = new StateTime(Tensors.vector(5, 0, 10, 0.5), RealScalar.ZERO);
  EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
      balloonStateSpaceModel, EulerIntegrator.INSTANCE, stateTime);
  BalloonEntity balloonEntity = new BalloonEntity(episodeIntegrator, EuclideanTrajectoryControl.INSTANCE, balloonStateSpaceModel);

  public void testDistanceSimple() {
    Tensor x = Tensors.vector(-1, 2, 1, 0);
    Tensor y = Tensors.vector(3, 2, 4, 0);
    Scalar expected = RealScalar.of(25);
    assertEquals(expected, balloonEntity.distance(x, y));
  }

  public void testDistanceWithUnits() {
    // FIXME only working for same type of units,
    Tensor x = Tensors.fromString("{2[m],2[m]}");
    Tensor y = Tensors.fromString("{4[m],2[m]}");
    Scalar expected = Quantity.of(4, "m^2");
    assertEquals(expected, balloonEntity.distance(x, y));
  }

  public void testDelayHint() {
    assertEquals(RealScalar.of(2), balloonEntity.delayHint());
  }

  public void testGetGoalRegionWithDistance() {
    Tensor x = Tensors.vector(1, 4);
    SphericalRegion sphericalRegionExpected = new SphericalRegion(Tensors.vector(1, 2), RealScalar.of(.3));
    SphericalRegion toBeTested = (SphericalRegion) balloonEntity.getGoalRegionWithDistance(Tensors.vector(1, 2, 1, 3));
    // FIXME equals method for sphericalRegion?
    // assertEquals(sphericalRegionExpected, toBeTested);
    assertEquals(sphericalRegionExpected.signedDistance(x), toBeTested.signedDistance(x));
  }

  public void testRender() {
    // TODO
  }

  public void testCreateTrajectoryPlanner() {
    Tensor goal = Tensors.vector(10, 30);
    Scalar vertSpeedMax = RealScalar.of(4);
    PlannerConstraint plannerConstraint = new BalloonPlannerConstraint(vertSpeedMax);
    TrajectoryPlanner trajectoryPlanner = balloonEntity.createTrajectoryPlanner(plannerConstraint, goal);
    assertTrue(trajectoryPlanner instanceof TrajectoryPlanner);
    trajectoryPlanner.insertRoot(stateTime);
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(10);
  }

  public void testStateTimeRaster() {
    StateTimeRaster stateTimeRaster = balloonEntity.stateTimeRaster();
    assertTrue(stateTimeRaster instanceof StateTimeRaster);
  }
}
