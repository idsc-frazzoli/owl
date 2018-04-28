// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.rn.RnPointcloudRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** demo shows the use of a cost image that is added to the distance cost
 * which gives an incentive to stay clear of obstacles */
public class R2NdTreeAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    ImageRegion imageRegion = ImageRegions.loadFromRepository( //
        "/io/track0_100.png", Tensors.vector(10, 10), false);
    Region<Tensor> region = RnPointcloudRegions.from(imageRegion, RealScalar.of(0.3));
    // ---
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(0, 0), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl();
    R2Entity r2Entity = new R2Entity(episodeIntegrator, trajectoryControl);
    owlyAnimationFrame.set(r2Entity);
    owlyAnimationFrame.setPlannerConstraint(new TrajectoryObstacleConstraint(SimpleTrajectoryRegionQuery.timeInvariant(region)));
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    // owlyAnimationFrame.addBackground(RegionRenders.create(region));
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2NdTreeAnimationDemo().start().jFrame.setVisible(true);
  }
}
