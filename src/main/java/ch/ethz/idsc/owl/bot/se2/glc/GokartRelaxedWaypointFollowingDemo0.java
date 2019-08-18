// code by astoll
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.EntityGlcPlannerCallback;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ren.WaypointRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;

public class GokartRelaxedWaypointFollowingDemo0 extends GokartDemo {
  private static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, -7.5, 640 }, { 0, 0, 1 } });

  @Override
  protected void configure(OwlyAnimationFrame owlyAnimationFrame) {
    final StateTime initial = new StateTime(Tensors.vector(35.733, 38.267, 1.885), RealScalar.ZERO);
    // slacks
    Tensor slacks = Tensors.vector(0.5, 0);
    // set up relaxed gokart entity
    GokartRelaxedEntity gokartEntity = GokartRelaxedEntity.createRelaxedGokartEntity(initial, slacks);
    // set up hangar map and way points
    HelperHangarMap hangarMap = new HelperHangarMap("/dubilab/obstacles/20180704.png", gokartEntity);
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180610.csv");
    // add set up second cost function, e.g. penalty for coming to close to obstacles
    R2ImageRegionWrap r2ImageRegionWrap = new R2ImageRegionWrap(hangarMap.bufferedImage, hangarMap.imageRegion.range(), 8);
    gokartEntity.setAdditionalCostFunction(r2ImageRegionWrap.costFunction());
    // --
    TrajectoryRegionQuery trajectoryRegionQuery = //
        SimpleTrajectoryRegionQuery.timeInvariant(hangarMap.region);
    // set up planner constraints, e.g. region outside track
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    // ---
    owlyAnimationFrame.add(gokartEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(hangarMap.imageRegion));
    owlyAnimationFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    owlyAnimationFrame.addBackground(new WaypointRender(ARROWHEAD, COLOR_WAYPOINT).setWaypoints(waypoints));
    // --
    GlcPlannerCallback glcPlannerCallback = EntityGlcPlannerCallback.verbose(gokartEntity);
    // --
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
    new GokartRelaxedWaypointFollowingDemo0().start().jFrame.setVisible(true);
  }
}
