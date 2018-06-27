// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.adapter.MultiConstraintAdapter;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.mapping.ShadowMapDirected;
import ch.ethz.idsc.owl.mapping.ShadowMapSimulator;
import ch.ethz.idsc.owl.mapping.ShadowMapSpherical;
import ch.ethz.idsc.owl.math.planar.ConeRegion;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;

public class Se2ShadowRulesDemo extends Se2CarDemo {
  private static final float PED_VELOCITY = 0.2f;
  private static final float PED_RADIUS = 0.05f;
  private static final Color PED_COLOR = new Color(38, 239, 248, 200);
  private static final float CAR_VELOCITY = 0.6f;
  private static final float CAR_RADIUS = 0.3f;
  private static final Color CAR_COLOR = new Color(200, 80, 20, 150);
  private static final Tensor RANGE = Tensors.vector(10.4, 8);
  // ---
  private static final FlowsInterface CARFLOWS = Se2CarFlows.standard(RealScalar.ONE, Degree.of(70));
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-180), Degree.of(180), 72), Subdivide.of(0, 5, 60));

  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    StateTime stateTime = new StateTime(Tensors.vector(5.0, 1.0, 0), RealScalar.ZERO);
    CarEntity carEntity = new CarEntity( //
        stateTime, //
        new PurePursuitControl(CarEntity.LOOKAHEAD, Degree.of(75)), //
        CarEntity.PARTITIONSCALE, CARFLOWS, CarEntity.SHAPE) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, RealScalar.of(Math.PI / 6));
      }
    };
    // ---
    Tensor image = ResourceData.of("/map/scenarios/S1_ped_obs_legal.BMP");
    BufferedImage bufferedImage = ImageFormat.of(image);
    // bufferedImage = ImageAlpha.scale(bufferedImage, 0.5f);
    //
    int dim1 = bufferedImage.getWidth();
    int dim0 = bufferedImage.getHeight();
    Tensor scale = Tensors.vector(dim1, dim0).pmul(RANGE.map(Scalar::reciprocal));
    //
    Tensor imageCar = ResourceData.of("/map/scenarios/S1_car_obs.BMP");
    Tensor imagePed = ResourceData.of("/map/scenarios/S1_ped_obs_legal.BMP");
    Tensor imageLid = ResourceData.of("/map/scenarios/S1_ped_obs_illegal.BMP");
    ImageRegion imageRegionCar = new ImageRegion(imageCar, RANGE, false);
    ImageRegion imageRegionPed = new ImageRegion(imagePed, RANGE, false);
    ImageRegion imageRegionLid = new ImageRegion(imageLid, RANGE, false);
    TrajectoryRegionQuery lidarRay = SimpleTrajectoryRegionQuery.timeInvariant(imageRegionLid);
    //
    Collection<PlannerConstraint> constraintCollection = new ArrayList<>();
    PlannerConstraint regionConstraint = createConstraint(imageRegionCar);
    constraintCollection.add(regionConstraint);
    //
    ImageRender imgRender = new ImageRender(bufferedImage, scale);
    owlyAnimationFrame.addBackground(imgRender);
    // Lidar
    LidarEmulator lidarEmulator = new LidarEmulator( //
        LIDAR_RAYTRACER, carEntity::getStateTimeNow, lidarRay);
    owlyAnimationFrame.addBackground(lidarEmulator);
    //  ---
    // ShadowMaps
    ShadowMapSpherical shadowMapPed = //
        new ShadowMapSpherical(lidarEmulator, imageRegionPed, PED_VELOCITY, PED_RADIUS);
    shadowMapPed.setColor(PED_COLOR);
    owlyAnimationFrame.addBackground(shadowMapPed);
    ShadowMapSimulator shadowSimPed = new ShadowMapSimulator(shadowMapPed, carEntity::getStateTimeNow);
    shadowSimPed.startNonBlocking(10);
    //
    ShadowMapDirected shadowMapCar = //
        new ShadowMapDirected(lidarEmulator, imageRegionCar, CAR_VELOCITY);
    shadowMapCar.setColor(CAR_COLOR);
    owlyAnimationFrame.addBackground(shadowMapCar);
    ShadowMapSimulator shadowSimCar = new ShadowMapSimulator(shadowMapCar, carEntity::getStateTimeNow);
    shadowSimCar.startNonBlocking(10);
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(imageRegionCar)), //
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
        shadowSimPed.flagShutdown();
        shadowSimCar.flagShutdown();
      }
    });
  }

  public static void main(String[] args) {
    new Se2ShadowRulesDemo().start().jFrame.setVisible(true);
  }
}
