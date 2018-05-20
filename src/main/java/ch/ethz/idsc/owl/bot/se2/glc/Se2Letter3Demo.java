// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.SimpleGlcPlannerCallback;
import ch.ethz.idsc.owl.glc.std.SimpleGoalConsumer;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.planar.ConeRegion;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.sim.CameraEmulator;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;

public class Se2Letter3Demo extends Se2CarDemo {
  static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-90), Degree.of(90), 32), Subdivide.of(0, 5, 30));

  @Override // from Se2CarDemo
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    StateTime stateTime = new StateTime(Tensors.vector(6, 6, 1), RealScalar.ZERO);
    CarEntity carEntity = new CarEntity( //
        stateTime, //
        new PurePursuitControl(CarEntity.LOOKAHEAD, CarEntity.MAX_TURNING_RATE), //
        CarEntity.PARTITIONSCALE, CarEntity.CARFLOWS, CarEntity.SHAPE) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, RealScalar.of(Math.PI / 6));
      }
    };
    carEntity.extraCosts.add(r2ImageRegionWrap.costFunction());
    // se2Entity.extraCosts.add(r2ImageRegionWrap.gradientCostFunction());
    ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
    PlannerConstraint plannerConstraint = createConstraint(imageRegion);
    carEntity.plannerConstraint = plannerConstraint;
    TrajectoryRegionQuery ray = SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    owlyAnimationFrame.add(carEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    GlcPlannerCallback glcPlannerCallback = new SimpleGlcPlannerCallback(carEntity);
    MouseGoal.supply(owlyAnimationFrame.geometricComponent, //
        new SimpleGoalConsumer(carEntity, plannerConstraint, glcPlannerCallback));
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), carEntity::getStateTimeNow, ray);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, carEntity::getStateTimeNow, ray);
      owlyAnimationFrame.addBackground(renderInterface);
    }
  }

  public static void main(String[] args) {
    new Se2Letter3Demo().start().jFrame.setVisible(true);
  }
}
