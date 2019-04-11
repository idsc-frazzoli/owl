// code by jph 
// adapted by mcp
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.se2.pid.PIDGains;
import ch.ethz.idsc.owl.bot.se2.pid.PIDTrajectoryControl;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.qty.Quantity;

public class Se2Letter4Demo extends Se2CarDemo {
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-90), Degree.of(90), 32), Subdivide.of(0, 5, 30));
  private static final PIDGains PID_GAINS = new PIDGains(Quantity.of(30, "m^-1"), RealScalar.of(10));

  @Override // from Se2CarDemo
  protected void configure(OwlyAnimationFrame owlyAnimationFrame) {
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._0F5C_2182;
    StateTime stateTime = new StateTime(Tensors.vector(7, 5, 1), RealScalar.ZERO);
    CarEntity carEntity = new CarEntity( //
        stateTime, //
        new PIDTrajectoryControl(CarEntity.MAX_TURNING_RATE, PID_GAINS), //
        CarEntity.PARTITIONSCALE, CarEntity.CARFLOWS, CarEntity.SHAPE) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, Degree.of(30));
      }
    };
    carEntity.extraCosts.add(r2ImageRegionWrap.costFunction());
    // se2Entity.extraCosts.add(r2ImageRegionWrap.gradientCostFunction());
    ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
    PlannerConstraint plannerConstraint = createConstraint(imageRegion);
    TrajectoryRegionQuery trajectoryRegionQuery = //
        SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    owlyAnimationFrame.add(carEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    MouseGoal.simple(owlyAnimationFrame, carEntity, plannerConstraint);
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, carEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(imageRegion)), //
          CarEntity.SHAPE, () -> carEntity.getStateTimeNow().time());
      owlyAnimationFrame.addBackground(renderInterface);
    }
  }

  public static void main(String[] args) {
    new Se2Letter4Demo().start().jFrame.setVisible(true);
  }
}
