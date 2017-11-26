// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Arrays;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.Tensors;

public class Se2xTPolicyDemo implements DemoInterface {
  @Override
  public void start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._2181;
    ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
    // ---
    TrajectoryRegionQuery trq = SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    // abstractEntity.raytraceQuery = SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    owlyAnimationFrame.setObstacleQuery(trq);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    // ---
    final TrajectoryRegionQuery ray = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList( //
            new TimeInvariantRegion(imageRegion))));
    {
      CarPolicyEntity twdPolicyEntity = new CarPolicyEntity(Tensors.vector(1, 2.8, 1.57), ray);
      owlyAnimationFrame.add(twdPolicyEntity);
    }
    // ---
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new Se2xTPolicyDemo().start();
  }
}
