// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import ch.ethz.idsc.owl.bot.rn.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.rn.R2ImageRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ani.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.sim.CameraEmulator;
import ch.ethz.idsc.owl.sim.LidarEmulator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

public class TwdImageDemo implements DemoInterface {
  @Override
  public void start() {
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._0F5C_2182;
    ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
    TrajectoryRegionQuery obstacleQuery = SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    TwdEntity twdEntity = TwdEntity.createJ2B2(new StateTime(Tensors.vector(7, 5, 0), RealScalar.ZERO));
    twdEntity.extraCosts.add(r2ImageRegionWrap.costFunction());
    owlyAnimationFrame.set(twdEntity);
    owlyAnimationFrame.setObstacleQuery(obstacleQuery);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), twdEntity::getStateTimeNow, obstacleQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LidarEmulator.DEFAULT, twdEntity::getStateTimeNow, obstacleQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new TwdImageDemo().start();
  }
}
