// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.bot.util.SimpleTranslationFamily;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.SimpleGlcPlannerCallback;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.ren.Se2WaypointRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.map.BijectionFamily;
import ch.ethz.idsc.owl.math.planar.ConeRegion;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.StandardTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;

/** demo to simulate dubendorf hangar */
// TODO this demo requires
public class GokartxTWaypointFollowingDemo extends Se2CarDemo {
  private static final Tensor ARROWHEAD = Tensors.matrixDouble( //
      new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).multiply(RealScalar.of(2));
  private static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, -7.5, 640 }, { 0, 0, 1 } });
  private static final Tensor VIRTUAL = Tensors.fromString("{{38, 39}, {42, 47}, {51, 52}, {46, 43}}");

  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    // {50.800, 55.733, -0.314}
    final StateTime initial = new StateTime(Tensors.vector(35.733, 38.267, 1.885), RealScalar.of(0.0));
    GokartxTEntity gokartEntity = new GokartxTEntity(initial) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, RealScalar.of(Math.PI / 10));
      }
    };
    // ---
    Tensor ext = Tensors.vector(0.7, 0.7).unmodifiable(); // TODO magic const
    BijectionFamily oscillation = new SimpleTranslationFamily(s -> Tensors.vector( //
        Math.sin(s.number().doubleValue() * -.4) * 6.0 + 44, //
        Math.cos(s.number().doubleValue() * -.4) * 6.0 + 44.0));
    Tensor dim1 = Tensors.vector(2., 2.);
    Region<StateTime> region1 = new R2xTEllipsoidStateTimeRegion( //
        dim1, oscillation, () -> gokartEntity.getStateTimeNow().time());
    Region<StateTime> region1d = new R2xTEllipsoidStateTimeRegion( //
        dim1.subtract(ext), oscillation, () -> gokartEntity.getStateTimeNow().time());
    // ---
    BijectionFamily oscillation2 = new SimpleTranslationFamily(s -> Tensors.vector( //
        Math.sin((s.number().doubleValue() - 1) * -.3) * 5.0 + 48, //
        Math.cos((s.number().doubleValue() - 1) * -.3) * 5.0 + 50.0));
    Tensor dim = Tensors.vector(2.5, 2.5);
    Region<StateTime> region2 = new R2xTEllipsoidStateTimeRegion( //
        dim, oscillation2, () -> gokartEntity.getStateTimeNow().time());
    Region<StateTime> region2d = new R2xTEllipsoidStateTimeRegion( //
        dim.subtract(ext), oscillation2, () -> gokartEntity.getStateTimeNow().time());
    // ---
    final Scalar scale = DoubleScalar.of(7.5); // meter_to_pixel
    Tensor tensor = ImageRegions.grayscale(ResourceData.of("/map/dubendorf/hangar/20180423obstacles.png"));
    tensor = ImageEdges.extrusion(tensor, 6); // == 0.73 * 7.5 == 5.475
    Tensor range = Tensors.vector(Dimensions.of(tensor)).divide(scale);
    ImageRegion imageRegion = new ImageRegion(tensor, range, false);
    Region<Tensor> region = Se2PointsVsRegions.line(gokartEntity.coords_X(), imageRegion);
    // ---
    Tensor waypoints = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
    Region<Tensor> polygonRegion = PolygonRegion.of(VIRTUAL);
    Region<Tensor> union = RegionUnion.wrap(Arrays.asList(region, polygonRegion));
    TrajectoryRegionQuery trajectoryRegionQuery = new StandardTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList( //
            new TimeInvariantRegion(union), // <- expects se2 states
            region1, region2 //
        )));
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    gokartEntity.plannerConstraint = plannerConstraint;
    // ---
    owlyAnimationFrame.set(gokartEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    owlyAnimationFrame.addBackground(RegionRenders.create(polygonRegion));
    owlyAnimationFrame.addBackground((RenderInterface) region1d);
    owlyAnimationFrame.addBackground((RenderInterface) region2d);
    owlyAnimationFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    // ---
    RenderInterface renderInterface = new Se2WaypointRender(waypoints, ARROWHEAD, new Color(64, 192, 64, 64));
    owlyAnimationFrame.addBackground(renderInterface);
    GlcPlannerCallback glcPlannerCallback = new SimpleGlcPlannerCallback(gokartEntity);
    GlcWaypointFollowing wpf = new GlcWaypointFollowing(waypoints, RealScalar.of(2), //
        gokartEntity, plannerConstraint, glcPlannerCallback);
    wpf.setHorizonDistance(RealScalar.of(5));
    wpf.startNonBlocking();
    // ---
    owlyAnimationFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        System.out.println("window was closed. terminating...");
        wpf.flagShutdown();
      }
    });
  }

  public static void main(String[] args) {
    new GokartxTWaypointFollowingDemo().start().jFrame.setVisible(true);
  }
}