package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ApEntityTest extends TestCase {
  Integrator integrator = RungeKutta4Integrator.INSTANCE;
  EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
      ApStateSpaceModel.INSTANCE, integrator, //
      new StateTime(Tensors.vector(0, 0, 0, 0), RealScalar.ZERO));
  // Setting up Trajectory Controls
  TrajectoryControl trajectoryControl = new ApTrajectoryControl();
  ApEntity apEntity = new ApEntity(episodeIntegrator, trajectoryControl);

  public void testSimple() {
    assertTrue(apEntity instanceof TrajectoryEntity);
  }

  public void testCreateTrajectoryPlanner() {
    // TrajectoryPlanner TrajectoryPlanner = apEntity.createTrajectoryPlanner(new ApPlannerConstraint(), ApEntity.GOAL);
    // assertTrue(standardTrajectoryPlanner instanceof StandardTrajectoryPlanner);
  }

  public void testDistance() {
    Tensor x = Tensors.vector(1, 1, 1, 1);
    Tensor y = Tensors.vector(3, 3, 3, 3);
    Scalar expected = RealScalar.of(4);
    assertTrue(expected.equals(apEntity.distance(x, y)));
  }
}
