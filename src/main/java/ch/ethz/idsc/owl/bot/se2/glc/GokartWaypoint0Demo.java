// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.SimpleGlcPlannerCallback;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.ren.Se2WaypointRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.planar.ConeRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegions;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;

/** demo to simulate dubendorf hangar
 * 
 * a virtual obstacle is added in the center to prevent the gokart from corner cutting */
public class GokartWaypoint0Demo extends GokartDemo {
  private static final Tensor VIRTUAL = Tensors.fromString("{{38, 39}, {42, 47}, {51, 52}, {46, 43}}");

  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    final StateTime initial = new StateTime(Tensors.vector(33.6, 41.5, 0.6), RealScalar.ZERO);
    GokartEntity gokartEntity = new GokartEntity(initial) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, RealScalar.of(Math.PI / 10));
      }
    };
    // ---
    HangarMap hangarMap = new HangarMap("20180423obstacles", gokartEntity);
    // ---
    Tensor waypoints = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
    Region<Tensor> polygonRegion = PolygonRegions.numeric(VIRTUAL);
    Region<Tensor> union = RegionUnion.wrap(Arrays.asList(hangarMap.region, polygonRegion));
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(union);
    // ---
    owlyAnimationFrame.add(gokartEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(hangarMap.imageRegion));
    owlyAnimationFrame.addBackground(RegionRenders.create(polygonRegion));
    owlyAnimationFrame.geometricComponent.setModel2Pixel(HangarMap.MODEL2PIXEL);
    // ---
    RenderInterface renderInterface = new Se2WaypointRender(waypoints, ARROWHEAD, new Color(64, 192, 64, 64));
    owlyAnimationFrame.addBackground(renderInterface);
    GlcPlannerCallback glcPlannerCallback = new SimpleGlcPlannerCallback(gokartEntity);
    GlcWaypointFollowing wpf = new GlcWaypointFollowing(waypoints, RealScalar.of(2), //
        gokartEntity, plannerConstraint, glcPlannerCallback);
    wpf.setHorizonDistance(RealScalar.of(7));
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
    new GokartWaypoint0Demo().start().jFrame.setVisible(true);
  }
}