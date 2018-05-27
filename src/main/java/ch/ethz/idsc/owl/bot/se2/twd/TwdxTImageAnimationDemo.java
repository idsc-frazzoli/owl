// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.r2.R2xTImageStateTimeRegion;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.map.RigidFamily;
import ch.ethz.idsc.owl.math.map.Se2Family;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.sim.CameraEmulator;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** the obstacle region in the demo is the outside of a rotating letter 'a' */
public class TwdxTImageAnimationDemo extends AbstractTwdDemo {
  static final LidarRaytracer LIDAR_RAYTRACER = new LidarRaytracer(Subdivide.of(-1.2, 1.2, 32), Subdivide.of(0, 4, 25));
  // ---
  private final TwdxTEntity twdxTEntity;
  private final Region<StateTime> region;
  private final TrajectoryRegionQuery trajectoryRegionQuery;

  public TwdxTImageAnimationDemo() {
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(RealScalar.of(1.2), RealScalar.of(.5));
    twdxTEntity = new TwdxTEntity(twdConfig, new StateTime(Tensors.vector(-1, -1, 1.0), RealScalar.ZERO));
    // ---
    RigidFamily rigidFamily = Se2Family.rotationAround( //
        Tensors.vectorDouble(1.5, 2), time -> time.multiply(RealScalar.of(0.1)));
    ImageRegion imageRegion = R2ImageRegions.inside_circ();
    region = new R2xTImageStateTimeRegion( //
        imageRegion, rigidFamily, () -> twdxTEntity.getStateTimeNow().time());
    // ---
    trajectoryRegionQuery = new SimpleTrajectoryRegionQuery(region);
  }

  @Override // from AbstractTwdDemo
  TwdEntity configure(OwlyAnimationFrame owlyAnimationFrame) {
    owlyAnimationFrame.add(twdxTEntity);
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), twdxTEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    MouseGoal.simple(owlyAnimationFrame, twdxTEntity, plannerConstraint);
    owlyAnimationFrame.addBackground((RenderInterface) region);
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, () -> twdxTEntity.getStateTimeNow(), trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    owlyAnimationFrame.configCoordinateOffset(200, 400);
    return twdxTEntity;
  }

  @Override // from AbstractTwdDemo
  Region<StateTime> getRegion() {
    return trajectoryRegionQuery;
  }

  public static void main(String[] args) {
    new TwdxTImageAnimationDemo().start().jFrame.setVisible(true);
  }
}
