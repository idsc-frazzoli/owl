// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

public class BalloonAnimationDemo implements DemoInterface {
  @Override // from DemoInterface
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.geometricComponent.setModel2Pixel(Tensors.fromString("{{7.5,0,100},{0,-7.5,800},{0,0,1}}"));
    PlannerConstraint plannerConstraint = new BalloonPlannerConstraint(BalloonEntity.SPEED_MAX);
    BalloonStateSpaceModel balloonStateSpaceModel = BalloonStateSpaceModels.defaultWithoutUnits();
    StateTime stateTime = new StateTime(Tensors.vector(0, 30, 0, 0), RealScalar.ZERO);
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        balloonStateSpaceModel, EulerIntegrator.INSTANCE, stateTime);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    BalloonEntity balloonEntity = new BalloonEntity(episodeIntegrator, trajectoryControl, balloonStateSpaceModel);
    MouseGoal.simple(owlyAnimationFrame, balloonEntity, plannerConstraint);
    owlyAnimationFrame.add(balloonEntity);
    owlyAnimationFrame.addBackground(AxesRender.INSTANCE);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) throws Exception {
    new BalloonAnimationDemo().start().jFrame.setVisible(true);
    // FIXME ANDRE can't fly down
  }
}
