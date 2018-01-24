// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.rn.RnPointcloudRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** the obstacle region in the demo is the outside of a rotating letter 'a' */
public class R2xTMapAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(4.5, 5), RealScalar.ZERO));
    TrajectoryEntity abstractEntity = new R2xTEntity(episodeIntegrator, RealScalar.of(1.5));
    owlyAnimationFrame.set(abstractEntity);
    // ---
    ImageRegion imageRegion = null;
    imageRegion = ImageRegions.loadFromRepository( //
        "/map/dubendorf/hangar/20180122.png", Tensors.vector(10, 10), false);
    Region<Tensor> region = RnPointcloudRegions.from(imageRegion, RealScalar.of(0.15));
    // ---
    owlyAnimationFrame.setObstacleQuery(SimpleTrajectoryRegionQuery.timeInvariant(region));
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    owlyAnimationFrame.configCoordinateOffset(100, 800);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2xTMapAnimationDemo().start().jFrame.setVisible(true);
  }
}
