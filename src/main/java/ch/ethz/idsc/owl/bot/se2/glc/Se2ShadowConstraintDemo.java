//code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.se2.Se2LateralAcceleration;
import ch.ethz.idsc.owl.glc.adapter.MultiConstraintAdapter;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.EntityImageRender;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.mapping.ShadowMapSimulator;
import ch.ethz.idsc.owl.mapping.ShadowMapSpherical;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;

public class Se2ShadowConstraintDemo extends Se2ShadowBaseDemo {
  private static final float CAR_MAX_ACC = 1.51f;
  private static final float CAR_REACTION_TIME = 0.0f;
  private static final float CAR_RAD = 1.0f; // [m]
  // ---
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-180), Degree.of(180), 72), Subdivide.of(0, 40, 120));

  @Override
  protected void configure(OwlyAnimationFrame owlyAnimationFrame) {
    StateTime stateTime = new StateTime(Tensors.vector(40.0, 11, 1.571), RealScalar.ZERO);
    GokartEntity gokartEntity = new GokartEntity(stateTime) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, Degree.of(30));
      }
    };
    Tensor imageCar = STREET_SCENARIO_DATA.imageCar_extrude(6);
    ImageRegion imageRegionCar = new ImageRegion(imageCar, RANGE, false);
    //
    Collection<PlannerConstraint> constraintCollection = new ArrayList<>();
    PlannerConstraint regionConstraint = createConstraint(imageRegionCar);
    constraintCollection.add(regionConstraint);
    //
    ImageRender imageRender = ImageRender.range(STREET_SCENARIO_DATA.render, RANGE);
    owlyAnimationFrame.addBackground(imageRender);
    // Lidar
    LidarEmulator lidarEmulator = new LidarEmulator( //
        LIDAR_RAYTRACER, gokartEntity::getStateTimeNow, trajectoryRegionQuery);
    LidarEmulator lidarEmulatorCon = new LidarEmulator( //
        LIDAR_RAYTRACER, () -> new StateTime(Tensors.vector(0, 0, 0), RealScalar.ZERO), trajectoryRegionQuery);
    owlyAnimationFrame.addBackground(lidarEmulator);
    Tensor imgT = ResourceData.of("/graphics/car.png");
    BufferedImage img = ImageFormat.of(imgT);
    owlyAnimationFrame.addBackground(new EntityImageRender(() -> gokartEntity.getStateTimeNow(), img, Tensors.vector(3.5, 2)));
    //  ---
    // ShadowMaps
    ShadowMapSpherical smPedLegal = //
        new ShadowMapSpherical(lidarEmulator, imageRegionPed, PED_VELOCITY, PED_RADIUS);
    smPedLegal.setColor(PED_COLOR_LEGAL);
    owlyAnimationFrame.addBackground(smPedLegal);
    ShadowMapSimulator simPedLegal = new ShadowMapSimulator(smPedLegal, gokartEntity::getStateTimeNow);
    simPedLegal.startNonBlocking(10);
    //
    ShadowMapSpherical smPedLegalCon = //
        new ShadowMapSpherical(lidarEmulatorCon, imageRegionPed, PED_VELOCITY, PED_RADIUS);
    SimpleShadowConstraintCV shadowConstraintPed = //
        new SimpleShadowConstraintCV(smPedLegalCon, imageRegionCar, CAR_RAD, CAR_MAX_ACC, CAR_REACTION_TIME, false);
    constraintCollection.add(shadowConstraintPed);
    //
    RenderInterface renderInterface = new MouseShapeRender( //
        SimpleTrajectoryRegionQuery.timeInvariant(line(imageRegionCar)), //
        GokartEntity.SHAPE, () -> gokartEntity.getStateTimeNow().time());
    owlyAnimationFrame.addBackground(renderInterface);
    //
    gokartEntity.extraCosts.add(Se2LateralAcceleration.INSTANCE);
    PlannerConstraint plannerConstraint = MultiConstraintAdapter.of(constraintCollection);
    MouseGoal.simple(owlyAnimationFrame, gokartEntity, plannerConstraint);
    owlyAnimationFrame.add(gokartEntity);
    owlyAnimationFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        simPedLegal.flagShutdown();
      }
    });
  }

  public static void main(String[] args) {
    new Se2ShadowConstraintDemo().start().jFrame.setVisible(true);
  }
}
