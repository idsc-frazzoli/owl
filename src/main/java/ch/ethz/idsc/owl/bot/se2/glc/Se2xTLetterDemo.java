// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Arrays;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.ethz.idsc.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegion;
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.bot.util.SimpleTranslationFamily;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.map.BijectionFamily;
import ch.ethz.idsc.owl.math.map.Se2Family;
import ch.ethz.idsc.owl.math.planar.CogPoints;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.sim.CameraEmulator;
import ch.ethz.idsc.owl.sim.LidarEmulator;
import ch.ethz.idsc.subare.core.td.SarsaType;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Se2xTLetterDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    CarxTEntity carxTEntity = new CarxTEntity(new StateTime(Tensors.vector(6.75, 5.4, 1 + Math.PI), RealScalar.ZERO));
    owlyAnimationFrame.set(carxTEntity);
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
    carxTEntity.extraCosts.add(r2ImageRegionWrap.costFunction());
    // ---
    BijectionFamily noise1 = new SimpleTranslationFamily(s -> Tensors.vector( //
        Math.sin(s.number().doubleValue() * .12) * 3.0 + 3.6, 4.0));
    Region<StateTime> region1 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.4, 0.5), noise1, () -> carxTEntity.getStateTimeNow().time());
    // ---
    BijectionFamily rigid3 = new Se2Family(s -> Tensors.vector(8.0, 5.8, s.number().doubleValue() * 0.5));
    Tensor polygon = CogPoints.of(3, RealScalar.of(1.0), RealScalar.of(0.3));
    Region<StateTime> cog0 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid3, () -> carxTEntity.getStateTimeNow().time());
    // ---
    Se2PointsVsRegion se2PointsVsRegion = Se2PointsVsRegions.line(Tensors.vector(0.2, 0.1, 0, -0.1), imageRegion);
    TrajectoryRegionQuery trq = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList( //
            new TimeInvariantRegion(se2PointsVsRegion), // <- expects se2 states
            region1, cog0 //
        )));
    // Se2PointsVsRegion se2PointsVsRegion = Se2PointsVsRegions.line(Tensors.vector(0.2, 0.1, 0, -0.1), RegionUnion.wrap(Arrays.asList( //
    // new TimeInvariantRegion(imageRegion), // <- expects se2 states
    // region1, cog0 //
    // )));
    // TrajectoryRegionQuery trq = new SimpleTrajectoryRegionQuery( //
    // );
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trq);
    carxTEntity.plannerConstraint = plannerConstraint;
    // abstractEntity.raytraceQuery = SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    owlyAnimationFrame.setPlannerConstraint(plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    owlyAnimationFrame.addBackground((RenderInterface) region1);
    // owlyAnimationFrame.addBackground((RenderInterface) region2);
    owlyAnimationFrame.addBackground((RenderInterface) cog0);
    // ---
    final TrajectoryRegionQuery ray = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList( //
            new TimeInvariantRegion(imageRegion), //
            region1,
            // region2,
            cog0 //
        )));
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), carxTEntity::getStateTimeNow, ray);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LidarEmulator.RAYDEMO, carxTEntity::getStateTimeNow, ray);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      CarPolicyEntity twdPolicyEntity = new CarPolicyEntity( //
          Tensors.vector(5.600, 8.667, -1.571), SarsaType.QLEARNING, ray);
      owlyAnimationFrame.add(twdPolicyEntity);
    }
    // ---
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Se2xTLetterDemo().start().jFrame.setVisible(true);
  }
}
