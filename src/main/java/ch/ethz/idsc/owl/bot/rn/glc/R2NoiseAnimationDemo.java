// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.r2.R2NoiseRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** demo visualizes the detected obstacles */
public class R2NoiseAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(0.2, 0.2), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl();
    R2Entity r2Entity = new R2Entity(episodeIntegrator, trajectoryControl);
    owlyAnimationFrame.set(r2Entity);
    Region<Tensor> region = new R2NoiseRegion(RealScalar.of(0.2));
    TrajectoryRegionQuery trajectoryRegionQuery = CatchyTrajectoryRegionQuery.timeInvariant(region);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    MouseGoal.simple(owlyAnimationFrame, r2Entity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(trajectoryRegionQuery));
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2NoiseAnimationDemo().start().jFrame.setVisible(true);
  }
}
