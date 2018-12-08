// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

public class BalloonAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    // Quantity.of(5, "m*s^-1")
    PlannerConstraint plannerConstraint = new BalloonPlannerConstraint(RealScalar.of(1));
    StateTime stateTime = new StateTime(Tensors.vector(5, 0, 10, 0.5), RealScalar.ZERO);
    BalloonStateSpaceModel balloonStateSpaceModel = BalloonStateSpaceModels.defaultWithoutUnits();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        balloonStateSpaceModel, EulerIntegrator.INSTANCE, stateTime);
    TrajectoryControl trajectoryControl = EuclideanTrajectoryControl.INSTANCE;
    BalloonEntity balloonEntity = new BalloonEntity(episodeIntegrator, trajectoryControl, balloonStateSpaceModel);
    MouseGoal.simple(owlyAnimationFrame, balloonEntity, plannerConstraint);
    owlyAnimationFrame.add(balloonEntity);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) throws Exception {
    new BalloonAnimationDemo().start().jFrame.setVisible(true);
  }
}
