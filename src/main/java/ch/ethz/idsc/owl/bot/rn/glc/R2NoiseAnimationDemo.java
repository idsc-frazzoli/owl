// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.r2.R2NoiseRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.ani.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class R2NoiseAnimationDemo implements DemoInterface {
  @Override
  public void start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.set(new R2Entity(Tensors.vector(0.2, 0.2)));
    Region<Tensor> region = new R2NoiseRegion(RealScalar.of(0.2));
    TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(region);
    owlyAnimationFrame.setObstacleQuery(trajectoryRegionQuery);
    owlyAnimationFrame.addBackground(RegionRenders.create(trajectoryRegionQuery));
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new R2NoiseAnimationDemo().start();
  }
}
