// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.RenderElements;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** simple animation of a landing airplane */
/* package */ enum ApDemo {
  ;
  public static void main(String[] args) throws Exception {
    final Tensor INITIAL = Tensors.vector(800, 0, 0, 80);
    SphericalRegion sphericalRegion = new SphericalRegion(ApTrajectoryPlanner.GOAL.extract(2, 4), ApTrajectoryPlanner.RADIUS_VECTOR.Get(2));
    StateTimeRaster stateTimeRaster = ApTrajectoryPlanner.stateTimeRaster();
    StandardTrajectoryPlanner standardTrajectoryPlanner = ApTrajectoryPlanner.ApStandardTrajectoryPlanner();
    // ---
    OwlyFrame owlyFrame = OwlyGui.start();
    // owlyFrame.configCoordinateOffset(33, 416);
    // owlyFrame.addBackground(RegionRenders.create(region));
    owlyFrame.addBackground(RegionRenders.create(sphericalRegion));
    owlyFrame.addBackground(RenderElements.create(stateTimeRaster));
    // owlyFrame.addBackground(RenderElements.create(plannerConstraint));
    // owlyFrame.addBackground(new DomainRender(trajectoryPlanner.getDomainMap(), eta));
    // ---
    standardTrajectoryPlanner.insertRoot(new StateTime(INITIAL, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(standardTrajectoryPlanner);
    glcExpand.findAny(1000);
    Optional<GlcNode> optional = standardTrajectoryPlanner.getBest();
    // ---
    System.out.println("Initial starting point: " + INITIAL);
    System.out.println("Final desired point: " + ApTrajectoryPlanner.GOAL);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    // ---
    if (optional.isPresent()) {
      System.out.println(1);
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
  }
}
