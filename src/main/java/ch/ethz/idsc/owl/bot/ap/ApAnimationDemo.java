// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
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
import ch.ethz.idsc.tensor.qty.Degree;

/* package */ class ApAnimationDemo implements DemoInterface {
  final static Scalar INITIAL_X = RealScalar.of(0);
  final static Scalar INITIAL_Z = RealScalar.of(80);
  final static Scalar INITIAL_VEL = RealScalar.of(60);
  final static Scalar INITIAL_GAMMA = Degree.of(-1);
  final static Tensor INITIAL = Tensors.of(INITIAL_X, INITIAL_Z, INITIAL_VEL, INITIAL_GAMMA);

  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    // Setting up episode integrator
    Integrator integrator = RungeKutta4Integrator.INSTANCE;
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        ApStateSpaceModel.INSTANCE, integrator, //
        new StateTime(INITIAL, RealScalar.ZERO));
    // Setting up Trajectory Controls
    TrajectoryControl trajectoryControl = new ApTrajectoryControl();
    // Setting up Trajectory Controls
    TrajectoryEntity trajectoryEntity = new ApEntity(episodeIntegrator, trajectoryControl);
    owlyAnimationFrame.add(trajectoryEntity);
    owlyAnimationFrame.addBackground(GridRender.INSTANCE);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new ApAnimationDemo().start().jFrame.setVisible(true);
  }
}
