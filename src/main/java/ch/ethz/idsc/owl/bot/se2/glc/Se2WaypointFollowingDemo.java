// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.GlcWaypointFollowing;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.Se2WaypointRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;

/** demo to simulate dubendorf hangar */
public class Se2WaypointFollowingDemo extends Se2CarDemo {
  private static final Tensor ARROWHEAD = Tensors.matrixDouble( //
      new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).multiply(RealScalar.of(3));
  private static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, -7.5, 640 }, { 0, 0, 1 } });
  private static final Tensor VIRTUAL = Tensors.fromString("{{38, 39}, {42, 47}, {51, 52}, {46, 43}}");

  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    final StateTime initial = new StateTime(Tensors.vector(33.6, 41.5, 0.6), RealScalar.ZERO);
    CarEntity se2Entity = new GokartEntity(initial);
    // ---
    final Scalar scale = DoubleScalar.of(7.5); // meter_to_pixel
    Tensor tensor = ImageRegions.grayscale(ResourceData.of("/map/dubendorf/hangar/20180423obstacles.png"));
    tensor = ImageEdges.extrusion(tensor, 6); // == 0.73 * 7.5 == 5.475
    Tensor range = Tensors.vector(Dimensions.of(tensor)).divide(scale);
    ImageRegion region = new ImageRegion(tensor, range, false);
    // ---
    Tensor waypointsT = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
    // R2ImageRegionWrap waypointsRegionWrap = //
    // R2ImageRegions.fromWaypoints(waypointsT, 6.0f, //
    // new Dimension(range.Get(0).number().intValue(), range.Get(1).number().intValue()), range);
    // se2Entity.extraCosts.add(waypointsRegionWrap.costFunction());
    // ---
    Region<Tensor> polygonRegion = PolygonRegion.of(VIRTUAL);
    Region<Tensor> union = RegionUnion.wrap(Arrays.asList(region, polygonRegion
    // waypointsRegionWrap.imageRegion()
    ));
    PlannerConstraint plannerConstraint = //
        new TrajectoryObstacleConstraint(SimpleTrajectoryRegionQuery.timeInvariant(union));
    se2Entity.plannerConstraint = plannerConstraint;
    // ---
    owlyAnimationFrame.set(se2Entity);
    owlyAnimationFrame.setPlannerConstraint(plannerConstraint);
    // owlyAnimationFrame.addBackground(RegionRenders.create(waypointsRegionWrap.imageRegion()));
    owlyAnimationFrame.addBackground(RegionRenders.create(region)); // TODO rendering of both regions / union
    owlyAnimationFrame.addBackground(RegionRenders.create(polygonRegion));
    owlyAnimationFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    // ---
    RenderInterface renderInterface = new Se2WaypointRender(waypointsT, ARROWHEAD, new Color(64, 192, 64, 64));
    owlyAnimationFrame.addBackground(renderInterface);
    GlcWaypointFollowing wpf = new GlcWaypointFollowing(waypointsT, RealScalar.of(2), //
        se2Entity, plannerConstraint, owlyAnimationFrame.trajectoryPlannerCallback);
    wpf.setHorizonDistance(RealScalar.of(5));
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