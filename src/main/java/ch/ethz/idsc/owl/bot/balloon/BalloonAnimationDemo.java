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
    PlannerConstraint plannerConstraint = new BalloonPlannerConstraint();
    StateTime stateTime = new StateTime(Tensors.vector(10, 3.5), RealScalar.ZERO);
    BalloonStateSpaceModel balloonStateSpaceModel = BalloonStateSpaceModels.defaultWithUnits();
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
