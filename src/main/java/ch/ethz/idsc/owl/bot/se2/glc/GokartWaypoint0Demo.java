// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.SimpleGlcPlannerCallback;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ren.WaypointRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegions;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.curve.BSpline2CurveSubdivision;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;

/** demo to simulate dubendorf hangar
 * 
 * a virtual obstacle is added in the center to prevent the gokart from corner cutting */
public class GokartWaypoint0Demo extends GokartDemo {
  private static final Tensor VIRTUAL = Tensors.fromString("{{38, 39}, {42, 47}, {51, 52}, {46, 43}}");

  @Override
  protected void configure(OwlyAnimationFrame owlyAnimationFrame) {
    final StateTime initial = new StateTime(Tensors.vector(33.6, 41.5, 0.6), RealScalar.ZERO);
    GokartEntity gokartEntity = new GokartEntity(initial) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, Degree.of(18));
      }
    };
    // ---
    HelperHangarMap hangarMap = new HelperHangarMap("/dubilab/obstacles/20180423.png", gokartEntity);
    // ---
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180425.csv");
    waypoints = new BSpline2CurveSubdivision(Se2Geodesic.INSTANCE).cyclic(waypoints);
    Region<Tensor> polygonRegion = PolygonRegions.numeric(VIRTUAL);
    Region<Tensor> union = RegionUnion.wrap(Arrays.asList(hangarMap.region, polygonRegion));
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(union);
    // ---
    owlyAnimationFrame.add(gokartEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(hangarMap.imageRegion));
    owlyAnimationFrame.addBackground(RegionRenders.create(polygonRegion));
    owlyAnimationFrame.geometricComponent.setModel2Pixel(HelperHangarMap.MODEL2PIXEL);
    // ---
    owlyAnimationFrame.addBackground(new WaypointRender(ARROWHEAD, COLOR_WAYPOINT).setWaypoints(waypoints));
    GlcPlannerCallback glcPlannerCallback = new SimpleGlcPlannerCallback(gokartEntity);
    GlcWaypointFollowing glcWaypointFollowing = new GlcWaypointFollowing( //
        waypoints, RealScalar.of(2), gokartEntity, plannerConstraint, //
        Arrays.asList(gokartEntity, glcPlannerCallback));
    glcWaypointFollowing.setHorizonDistance(RealScalar.of(7));
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
    new GokartWaypoint0Demo().start().jFrame.setVisible(true);
  }
}