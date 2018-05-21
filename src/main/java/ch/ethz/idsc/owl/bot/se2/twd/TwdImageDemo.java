// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.sim.CameraEmulator;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;

public class TwdImageDemo implements DemoInterface {
  static final LidarRaytracer LIDAR_RAYTRACER = new LidarRaytracer(Subdivide.of(-1, 1, 26), Subdivide.of(0, 4, 30));

  @Override
  public OwlyAnimationFrame start() {
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._0F5C_2182;
    ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    TwdEntity twdEntity = TwdEntity.createJ2B2(new StateTime(Tensors.vector(7, 5, 0), RealScalar.ZERO));
    twdEntity.extraCosts.add(r2ImageRegionWrap.costFunction());
    owlyAnimationFrame.add(twdEntity);
    TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    MouseGoal.simple(owlyAnimationFrame, twdEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), twdEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, twdEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new TwdImageDemo().start().jFrame.setVisible(true);
  }
}
