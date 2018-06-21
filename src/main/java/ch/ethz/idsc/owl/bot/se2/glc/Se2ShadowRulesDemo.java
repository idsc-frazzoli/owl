// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.data.img.ImageAlpha;
import ch.ethz.idsc.owl.data.img.ImageTensors;
import ch.ethz.idsc.owl.glc.adapter.MultiConstraintAdapter;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.mapping.ShadowMapSimulator;
import ch.ethz.idsc.owl.math.planar.ConeRegion;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionIntersection;
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
  private static final Color PED_COLOR = new Color(23, 200, 20, 100);
  private static final float CAR_VELOCITY = 0.6f;
  private static final float CAR_RADIUS = 0.3f;
  private static final Color CAR_COLOR = new Color(200, 80, 20, 150);
  private static final Tensor RANGE = Tensors.vector(5, 10);
  // ---
  private static final FlowsInterface CARFLOWS = Se2CarFlows.forward(RealScalar.ONE, Degree.of(70));
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-180), Degree.of(180), 128), Subdivide.of(0, 5, 60));

  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    StateTime stateTime = new StateTime(Tensors.vector(2.9, 1.0, 3.14 / 2), RealScalar.ZERO);
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
    Tensor image = ResourceData.of("/map/scenarios/multiarea.png");
    BufferedImage bufferedImage = ImageFormat.of(image);
    bufferedImage = ImageAlpha.scale(bufferedImage, 0.3f);
    //
    int dim1 = bufferedImage.getWidth();
    int dim0 = bufferedImage.getHeight();
    Tensor scale = Tensors.vector(dim1, dim0).pmul(RANGE.map(Scalar::reciprocal));
    //
    Tensor imageCar = ImageTensors.reduceInverted(image, 1);
    Tensor imagePed = ImageTensors.reduceInverted(image, 2);
    ImageRegion imageRegionCar = new ImageRegion(imageCar, RANGE, false);
    ImageRegion imageRegionPed = new ImageRegion(imagePed, RANGE, false);
    Region<Tensor> intersectionRegion = RegionIntersection.wrap(Arrays.asList(imageRegionCar, imageRegionPed));
    TrajectoryRegionQuery rayComp = SimpleTrajectoryRegionQuery.timeInvariant(intersectionRegion);
    //
    Collection<PlannerConstraint> constraintCollection = new ArrayList<>();
    PlannerConstraint regionConstraint = createConstraint(imageRegionCar);
    constraintCollection.add(regionConstraint);
    //
    ImageRender imgRender = new ImageRender(bufferedImage, scale);
    owlyAnimationFrame.addBackground(imgRender);
    // Lidar
    LidarEmulator lidarEmulator = new LidarEmulator( //
        LIDAR_RAYTRACER, carEntity::getStateTimeNow, rayComp);
    owlyAnimationFrame.addBackground(lidarEmulator);
    // Shadowmap
    ShadowMapSimulator shadowMapPed = //
        new ShadowMapSimulator(lidarEmulator, imageRegionPed, carEntity::getStateTimeNow, PED_VELOCITY, PED_RADIUS);
    shadowMapPed.setColor(PED_COLOR);
    owlyAnimationFrame.addBackground(shadowMapPed);
    shadowMapPed.startNonBlocking(10);
    //
    ShadowMapSimulator shadowMapCar = //
        new ShadowMapSimulator(lidarEmulator, imageRegionCar, carEntity::getStateTimeNow, CAR_VELOCITY, CAR_RADIUS);
    shadowMapCar.setColor(CAR_COLOR);
    owlyAnimationFrame.addBackground(shadowMapCar);
    shadowMapCar.startNonBlocking(10);
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
        shadowMapPed.flagShutdown();
        shadowMapCar.flagShutdown();
      }
    });
  }

  public static void main(String[] args) {
    new Se2ShadowRulesDemo().start().jFrame.setVisible(true);
  }
}
