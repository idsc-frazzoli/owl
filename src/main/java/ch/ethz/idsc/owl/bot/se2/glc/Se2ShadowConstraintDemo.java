// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.MultiConstraintAdapter;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.planar.ConeRegion;
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

public class Se2ShadowConstraintDemo extends Se2CarDemo {
  // private static final float PED_VELOCITY = 0.3f;
  // private static final float PED_RADIUS = 0.1f;
  // private static final Color PED_COLOR = new Color(23, 12, 200);
  // private static final float MAX_A = 0.6f; // [m/s²]
  // private static final float REACTION_TIME = 0.5f;
  private static final FlowsInterface CARFLOWS = Se2CarFlows.forward(RealScalar.ONE, Degree.of(70));
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-180), Degree.of(180), 128), Subdivide.of(0, 5, 60));

  @Override // from Se2CarDemo
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    StateTime stateTime = new StateTime(Tensors.vector(3.8, 1.0, 3.14 / 2), RealScalar.ZERO);
    CarEntity carEntity = new CarEntity( //
        stateTime, //
        new PurePursuitControl(CarEntity.LOOKAHEAD, Degree.of(75)), //
        CarEntity.PARTITIONSCALE, CARFLOWS, CarEntity.SHAPE) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, RealScalar.of(Math.PI / 6));
      }
    };
    Collection<PlannerConstraint> constraintCollection = new ArrayList<>();
    // Image Region
    ImageRegion imageRegion = //
        ImageRegions.loadFromRepository("/map/scenarios/street_2.png", Tensors.vector(9, 9), false);
    PlannerConstraint regionConstraint = createConstraint(imageRegion);
    constraintCollection.add(regionConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    // Lidar
    TrajectoryRegionQuery ray = SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    LidarEmulator lidarEmulator = new LidarEmulator( //
        LIDAR_RAYTRACER, carEntity::getStateTimeNow, ray);
    owlyAnimationFrame.addBackground(lidarEmulator);
    // Shadow Map
    // ShadowMapSimulator shadowMapPed = //
    // new ShadowMapSimulator(lidarEmulator, imageRegion, carEntity::getStateTimeNow, PED_VELOCITY, PED_RADIUS);
    // shadowMapPed.setColor(PED_COLOR);
    // owlyAnimationFrame.addBackground(shadowMapPed);
    // shadowMapPed.startNonBlocking(10);
    // SimpleShadowConstraintJavaCV shadowConstraintPed = //
    // new SimpleShadowConstraintJavaCV(shadowMapPed, MAX_A, REACTION_TIME);
    // constraintCollection.add(shadowConstraintPed);
    // ---
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(imageRegion)), //
          CarEntity.SHAPE, () -> carEntity.getStateTimeNow().time());
      owlyAnimationFrame.addBackground(renderInterface);
    }
    PlannerConstraint constraints = MultiConstraintAdapter.of(constraintCollection);
    MouseGoal.simple(owlyAnimationFrame, carEntity, constraints);
    owlyAnimationFrame.add(carEntity);
    owlyAnimationFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        System.out.println("window was closed. terminating...");
        // shadowMapPed.flagShutdown();
      }
    });
  }

  public static void main(String[] args) {
    new Se2ShadowConstraintDemo().start().jFrame.setVisible(true);
  }
}
