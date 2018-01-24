// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.r2.R2xTNoiseStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

// LONGTERM the visualization of the demo is poor
public class R2xTNoiseAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(0.2, 0.2), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl(episodeIntegrator);
    owlyAnimationFrame.set(new R2xTEntity(trajectoryControl, RealScalar.of(0.4)));
    Region<StateTime> region = new R2xTNoiseStateTimeRegion(RealScalar.of(0.5));
    owlyAnimationFrame.setObstacleQuery(new SimpleTrajectoryRegionQuery(region));
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2xTNoiseAnimationDemo().start().jFrame.setVisible(true);
  }
}
