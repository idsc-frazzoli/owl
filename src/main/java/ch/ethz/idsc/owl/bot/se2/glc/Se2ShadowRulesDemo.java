// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.glc.adapter.MultiConstraintAdapter;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.mapping.ShadowMapDirected;
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

public class Se2ShadowRulesDemo extends Se2ShadowBaseDemo {
  private static final Color PED_COLOR_ILLEGAL = new Color(83, 33, 248, 200);
  private static final float CAR_VELOCITY = 8.0f;
  private static final Color CAR_COLOR_LEGAL = new Color(169, 59, 239, 200);
  // private static final float CAR_RADIUS = 0.3f;
  // ---
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-180), Degree.of(180), 288), Subdivide.of(0, 15, 120));

  @Override
  protected void configure(OwlyAnimationFrame owlyAnimationFrame) {
    StateTime stateTime = new StateTime(Tensors.vector(25, 5, 0), RealScalar.ZERO);
    GokartEntity gokartEntity = new GokartEntity(stateTime) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, Degree.of(30));
      }
    };
    // ---
    Tensor imageCar = STREET_SCENARIO_DATA.imageCar_extrude(12);
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
    owlyAnimationFrame.addBackground(lidarEmulator);
    Tensor imgT = ResourceData.of("/graphics/car.png");
    BufferedImage img = ImageFormat.of(imgT);
    owlyAnimationFrame.addBackground(new EntityImageRender(() -> gokartEntity.getStateTimeNow(), img, Tensors.vector(3.5, 2)));
    //  ---
    // ShadowMaps
    ShadowMapSpherical smPedLegal = //
        new ShadowMapSpherical(lidarEmulator, imageRegionPed, PED_VELOCITY, PED_RADIUS);
    smPedLegal.setColor(PED_COLOR_LEGAL);
    // smPedLegal.useGPU(); // requires CUDA
    owlyAnimationFrame.addBackground(smPedLegal);
    ShadowMapSimulator simPedLegal = new ShadowMapSimulator(smPedLegal, gokartEntity::getStateTimeNow);
    simPedLegal.startNonBlocking(10);
    //
    ShadowMapSpherical smPedIllegal = //
        new ShadowMapSpherical(lidarEmulator, imageRegionLid, PED_VELOCITY, PED_RADIUS);
    smPedIllegal.setColor(PED_COLOR_ILLEGAL);
    // smPedIllegal.useGPU(); // requires CUDA
    ShadowMapSimulator simPedIllegal = new ShadowMapSimulator(smPedIllegal, gokartEntity::getStateTimeNow);
    // owlyAnimationFrame.addBackground(smPedIllegal);
    // simPedIllegal.startNonBlocking(10);
    //
    ShadowMapDirected smCarLegal = //
        new ShadowMapDirected(lidarEmulator, imageRegionCar, STREET_SCENARIO_DATA.imageLanesString, CAR_VELOCITY);
    smCarLegal.setColor(CAR_COLOR_LEGAL);
    owlyAnimationFrame.addBackground(smCarLegal);
    ShadowMapSimulator simCarLegal = new ShadowMapSimulator(smCarLegal, gokartEntity::getStateTimeNow);
    simCarLegal.startNonBlocking(10);
    //
    RenderInterface renderInterface = new MouseShapeRender( //
        SimpleTrajectoryRegionQuery.timeInvariant(line(imageRegionCar)), //
        GokartEntity.SHAPE, () -> gokartEntity.getStateTimeNow().time());
    owlyAnimationFrame.addBackground(renderInterface);
    //
    PlannerConstraint plannerConstraint = MultiConstraintAdapter.of(constraintCollection);
    MouseGoal.simple(owlyAnimationFrame, gokartEntity, plannerConstraint);
    owlyAnimationFrame.add(gokartEntity);
    owlyAnimationFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        simPedLegal.flagShutdown();
        simPedIllegal.flagShutdown();
        simCarLegal.flagShutdown();
      }
    });
  }

  public static void main(String[] args) {
    new Se2ShadowRulesDemo().start().jFrame.setVisible(true);
  }
}
