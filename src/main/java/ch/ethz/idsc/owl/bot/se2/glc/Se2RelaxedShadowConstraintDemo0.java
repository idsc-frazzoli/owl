// code by astoll
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.glc.adapter.ConstraintViolationCost;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.mapping.ShadowMapSimulator;
import ch.ethz.idsc.owl.mapping.ShadowMapSpherical;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;

public class Se2RelaxedShadowConstraintDemo0 extends Se2ShadowBaseDemo {
  private static final float CAR_MAX_ACC = 1.51f;
  private static final float CAR_REACTION_TIME = 0.0f;
  private static final float CAR_RADIUS = 1.0f; // [m]
  static final Tensor ARROWHEAD = Arrowhead.of(0.6);
  static final Color COLOR_WAYPOINT = new Color(64, 192, 64, 64);
  // ---
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-180), Degree.of(180), 72), Subdivide.of(0, 40, 120));

  @Override
  protected void configure(OwlyAnimationFrame owlyAnimationFrame) {
    // initial state time
    StateTime initial = new StateTime(Tensors.vector(39.0, 11, 1.571), RealScalar.ZERO);
    // slacks
    Tensor slacks = Tensors.vector(1.5, 0);
    // set up slack vector
    CarRelaxedEntity carRelaxedEntity = CarRelaxedEntity.createDefault(initial, slacks);
    // car region
    Tensor imageCar = STREET_SCENARIO_DATA.imageCar_extrude(6);
    ImageRegion imageRegionCar = new ImageRegion(imageCar, RANGE, false);
    // planner constraints given by car region
    PlannerConstraint plannerConstraint = createConstraint(imageRegionCar);
    //
    ImageRender imageRender = ImageRender.range(STREET_SCENARIO_DATA.render, RANGE);
    owlyAnimationFrame.addBackground(imageRender);
    // Lidar
    LidarEmulator lidarEmulator = new LidarEmulator( //
        LIDAR_RAYTRACER, carRelaxedEntity::getStateTimeNow, trajectoryRegionQuery);
    LidarEmulator lidarEmulatorCon = new LidarEmulator( //
        LIDAR_RAYTRACER, () -> new StateTime(Tensors.vector(0, 0, 0), RealScalar.ZERO), trajectoryRegionQuery);
    owlyAnimationFrame.addBackground(lidarEmulator);
    // --
    Tensor imgT = ResourceData.of("/graphics/car.png");
    BufferedImage img = ImageFormat.of(imgT);
    owlyAnimationFrame.addBackground(new EntityImageRender(() -> carRelaxedEntity.getStateTimeNow(), img, Tensors.vector(3.5, 2)));
    //  ---
    // ShadowMaps
    ShadowMapSpherical smPedLegal = //
        new ShadowMapSpherical(lidarEmulator, imageRegionPed, PED_VELOCITY, PED_RADIUS);
    smPedLegal.setColor(PED_COLOR_LEGAL);
    owlyAnimationFrame.addBackground(smPedLegal);
    ShadowMapSimulator simPedLegal = new ShadowMapSimulator(smPedLegal, carRelaxedEntity::getStateTimeNow);
    simPedLegal.startNonBlocking(10);
    // --
    ShadowMapSpherical smPedLegalCon = //
        new ShadowMapSpherical(lidarEmulatorCon, imageRegionPed, PED_VELOCITY, PED_RADIUS);
    SimpleShadowConstraintCV shadowConstraintPed = //
        new SimpleShadowConstraintCV(smPedLegalCon, imageRegionCar, CAR_RADIUS, CAR_MAX_ACC, CAR_REACTION_TIME, false);
    // set up second cost function
    CostFunction pedLegalCost = //
        ConstraintViolationCost.of(shadowConstraintPed, RealScalar.ONE);
    carRelaxedEntity.setAdditionalCostFunction(pedLegalCost);
    //
    // Tensor waypoints = Tensors.fromString("{{39, 20, 1.8}, {39, 15, 1.8}}");
    // ---
    // owlyAnimationFrame.add(carRelaxedEntity);
    // owlyAnimationFrame.addBackground(RegionRenders.create(imageRegionCar));
    // owlyAnimationFrame.addBackground(new WaypointRender(ARROWHEAD, COLOR_WAYPOINT).setWaypoints(waypoints));
    // --
    // SimpleGlcPlannerCallback glcPlannerCallback = new SimpleGlcPlannerCallback(carRelaxedEntity);
    // glcPlannerCallback.showCost();
    // // --
    // GlcWaypointFollowing glcWaypointFollowing = new GlcWaypointFollowing( //
    // waypoints, RealScalar.of(2), carRelaxedEntity, plannerConstraint, //
    // Arrays.asList(carRelaxedEntity, glcPlannerCallback));
    // glcWaypointFollowing.setHorizonDistance(RealScalar.of(5));
    // glcWaypointFollowing.startNonBlocking();
    // // ---
    // owlyAnimationFrame.jFrame.addWindowListener(new WindowAdapter() {
    // @Override
    // public void windowClosed(WindowEvent windowEvent) {
    // glcWaypointFollowing.flagShutdown();
    // }
    // });
    // --
    RenderInterface renderInterface = new MouseShapeRender( //
        SimpleTrajectoryRegionQuery.timeInvariant(line(imageRegionCar)), //
        GokartEntity.SHAPE, () -> carRelaxedEntity.getStateTimeNow().time());
    owlyAnimationFrame.addBackground(renderInterface);
    //
    MouseGoal.simple(owlyAnimationFrame, carRelaxedEntity, plannerConstraint);
    owlyAnimationFrame.add(carRelaxedEntity);
    owlyAnimationFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        simPedLegal.flagShutdown();
      }
    });
  }

  public static void main(String[] args) {
    new Se2RelaxedShadowConstraintDemo0().start().jFrame.setVisible(true);
  }
}
