// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import ch.ethz.idsc.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.SimpleGlcPlannerCallback;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.WaypointRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegions;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.sophus.hs.r2.SimpleR2TranslationFamily;
import ch.ethz.idsc.sophus.math.BijectionFamily;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;

/** demo to simulate dubendorf hangar */
public class GokartxTWaypointFollowingDemo extends GokartDemo {
  private static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, -7.5, 640 }, { 0, 0, 1 } });
  private static final Tensor VIRTUAL = Tensors.fromString("{{38, 39}, {42, 47}, {51, 52}, {46, 43}}");

  @Override
  protected void configure(OwlyAnimationFrame owlyAnimationFrame) {
    // {50.800, 55.733, -0.314}
    final StateTime initial = new StateTime(Tensors.vector(35.733, 38.267, 1.885), RealScalar.of(0.0));
    GokartxTEntity gokartEntity = new GokartxTEntity(initial) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, Degree.of(18));
      }
    };
    // ---
    Tensor ext = Tensors.vector(0.7, 0.7).unmodifiable(); // 0.7 is the half-width of the gokart
    BijectionFamily oscillation = new SimpleR2TranslationFamily(s -> Tensors.vector( //
        Math.sin(s.number().doubleValue() * -.4) * 6.0 + 44, //
        Math.cos(s.number().doubleValue() * -.4) * 6.0 + 44.0));
    Tensor dim1 = Tensors.vector(2., 2.);
    Region<StateTime> region1 = new R2xTEllipsoidStateTimeRegion( //
        dim1, oscillation, () -> gokartEntity.getStateTimeNow().time());
    Region<StateTime> region1d = new R2xTEllipsoidStateTimeRegion( //
        dim1.subtract(ext), oscillation, () -> gokartEntity.getStateTimeNow().time());
    // ---
    BijectionFamily oscillation2 = new SimpleR2TranslationFamily(s -> Tensors.vector( //
        Math.sin((s.number().doubleValue() - 1) * -.3) * 5.0 + 48, //
        Math.cos((s.number().doubleValue() - 1) * -.3) * 5.0 + 50.0));
    Tensor dim = Tensors.vector(2.5, 2.5);
    Region<StateTime> region2 = new R2xTEllipsoidStateTimeRegion( //
        dim, oscillation2, () -> gokartEntity.getStateTimeNow().time());
    Region<StateTime> region2d = new R2xTEllipsoidStateTimeRegion( //
        dim.subtract(ext), oscillation2, () -> gokartEntity.getStateTimeNow().time());
    // ---
    HelperHangarMap hangarMap = new HelperHangarMap("/dubilab/obstacles/20180423.png", gokartEntity);
    // ---
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180425.csv");
    Region<Tensor> polygonRegion = PolygonRegions.numeric(VIRTUAL);
    Region<Tensor> union = RegionUnion.wrap(Arrays.asList(hangarMap.region, polygonRegion));
    TrajectoryRegionQuery trajectoryRegionQuery = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList( //
            new TimeInvariantRegion(union), // <- expects se2 states
            region1, region2 //
        )));
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    // ---
    owlyAnimationFrame.add(gokartEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(hangarMap.imageRegion));
    owlyAnimationFrame.addBackground(RegionRenders.create(polygonRegion));
    owlyAnimationFrame.addBackground((RenderInterface) region1d);
    owlyAnimationFrame.addBackground((RenderInterface) region2d);
    owlyAnimationFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    // ---
    owlyAnimationFrame.addBackground(new WaypointRender(ARROWHEAD, COLOR_WAYPOINT).setWaypoints(waypoints));
    SimpleGlcPlannerCallback glcPlannerCallback = new SimpleGlcPlannerCallback(gokartEntity);
    glcPlannerCallback.showCost();
    GlcWaypointFollowing glcWaypointFollowing = new GlcWaypointFollowing( //
        waypoints, RealScalar.of(2), gokartEntity, plannerConstraint, //
        Arrays.asList(gokartEntity, glcPlannerCallback));
    glcWaypointFollowing.setHorizonDistance(RealScalar.of(5));
    glcWaypointFollowing.startNonBlocking();
    // ---
    owlyAnimationFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        glcWaypointFollowing.flagShutdown();
      }
    });
  }

  public static void main(String[] args) {
    new GokartxTWaypointFollowingDemo().start().jFrame.setVisible(true);
  }
}