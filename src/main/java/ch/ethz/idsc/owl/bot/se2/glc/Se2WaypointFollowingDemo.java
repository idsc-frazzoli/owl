// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.GlcWaypointFollowing;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.ArrowHeadRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.map.Bijection;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

public class Se2WaypointFollowingDemo extends Se2CarDemo {
  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    CarEntity se2Entity = CarEntity.createDefault(new StateTime(Tensors.vector(33.6, 41.5, 0.6), RealScalar.ZERO));
    // ---
    Scalar scale = DoubleScalar.of(7.5);
    Tensor tensor = ImageRegions.grayscale(ResourceData.of("/map/dubendorf/hangar/20180423obstacles.png"));
    BufferedImage bufferedImage = ImageFormat.of(tensor);
    int size = bufferedImage.getWidth();
    tensor = ImageEdges.extrusion(tensor, 3);
    Tensor range = Tensors.vector(size, size).divide(scale);
    ImageRegion region = new ImageRegion(tensor, range, false);
    // ---
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(SimpleTrajectoryRegionQuery.timeInvariant(region));
    se2Entity.plannerConstraint = plannerConstraint;
    owlyAnimationFrame.set(se2Entity);
    owlyAnimationFrame.setPlannerConstraint(plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    // owlyAnimationFrame.configCoordinateOffset(100, 800);
    // define waypoints
    Tensor waypoints = Tensors.of( //
        Tensors.vector(0.0, 3.0, 0), //
        Tensors.vector(2.0, 3.0, 0), //
        Tensors.vector(4.0, 3.0, 0), //
        Tensors.vector(6.0, 3.0, 0), //
        Tensors.vector(8.0, 3.0, 0), //
        Tensors.vector(9.5, 2.59, -0.52), //
        Tensors.vector(10.6, 1.5, -1.05), //
        Tensors.vector(11.0, 0.0, -1.57), // 2.
        Tensors.vector(10.6, -1.5, -2.09), //
        Tensors.vector(9.5, -2.59, -2.62), //
        Tensors.vector(8.0, -3.0, -3.14), //
        Tensors.vector(6.0, -3.0, -3.14), //
        Tensors.vector(4.0, -3.0, -3.14), //
        Tensors.vector(2.0, -3.0, -3.14), //
        Tensors.vector(0.0, -3.0, -3.14), //
        Tensors.vector(-1.5, -2.59, -3.66), //
        Tensors.vector(-2.6, -1.50, -4.19), //
        Tensors.vector(-3.0, 0.00, -4.71), //
        Tensors.vector(-2.6, 1.50, -5.24), //
        Tensors.vector(-1.5, 2.59, -5.76)).unmodifiable(); //
    // ---
    Scalar tx = RealScalar.of(40);
    Scalar ty = RealScalar.of(41);
    Scalar angle = DoubleScalar.of(3.141 / 4.0);
    Tensor xyscale = Tensors.vector(1.7, 1.5, 1);
    Bijection bijection = new Se2Bijection(Tensors.of(tx, ty, angle));
    Tensor waypointsT = Tensors.empty();
    for (Tensor t : waypoints) {
      waypointsT.append(bijection.forward().apply(t.pmul(xyscale)).append(t.get(2).add(angle)));
    }
    // draw waypoints
    RenderInterface renderInterface = new ArrowHeadRender(waypointsT, new Color(64, 192, 64, 64));
    owlyAnimationFrame.addBackground(renderInterface);
    GlcWaypointFollowing wpf = new GlcWaypointFollowing(waypointsT, se2Entity, plannerConstraint, owlyAnimationFrame.trajectoryPlannerCallback);
    wpf.setDistanceThreshold(RealScalar.of(1));
    wpf.startNonBlocking();
    //
    owlyAnimationFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        System.out.println("window was closed. terminating...");
        wpf.flagShutdown();
      }
    });
  }

  public static void main(String[] args) {
    new Se2WaypointFollowingDemo().start().jFrame.setVisible(true);
  }
}