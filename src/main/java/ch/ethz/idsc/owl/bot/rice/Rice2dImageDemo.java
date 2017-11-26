// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.rn.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.rn.R2ImageRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.ani.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.EmptyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

public class Rice2dImageDemo implements DemoInterface {
  @Override
  public void start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    Scalar mu = RealScalar.of(-0.5);
    Collection<Flow> controls = Rice2Controls.create2d(mu, 1, 15);
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
    TrajectoryRegionQuery obstacleQuery = SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    owlyAnimationFrame.set(new Rice2dEntity(mu, Tensors.vector(7, 6, 0, 0), controls));
    owlyAnimationFrame.setObstacleQuery(EmptyTrajectoryRegionQuery.INSTANCE);
    owlyAnimationFrame.setObstacleQuery(obstacleQuery);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new Rice2dImageDemo().start();
  }
}
