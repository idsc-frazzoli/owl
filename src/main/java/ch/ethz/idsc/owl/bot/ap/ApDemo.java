// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;

/** simple animation of a landing airplane */
/* package */ enum ApDemo {
  ;
  final static Scalar INITIAL_X = RealScalar.of(0);
  final static Scalar INITIAL_Z = RealScalar.of(80);
  final static Scalar INITIAL_VEL = RealScalar.of(60);
  final static Scalar INITIAL_GAMMA = Degree.of(-1);
  final static Tensor INITIAL = Tensors.of(INITIAL_X, INITIAL_Z, INITIAL_VEL, INITIAL_GAMMA);

  public static void main(String[] args) throws Exception {
    // StateTimeRaster stateTimeRaster = ApTrajectoryPlanner.stateTimeRaster();
    StandardTrajectoryPlanner standardTrajectoryPlanner = ApTrajectoryPlanner.apStandardTrajectoryPlanner();
    // ---
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(300, 300);
    owlyFrame.geometricComponent.setModel2Pixel(Tensors.fromString("{{1,0,10},{0,-1,500},{0,0,1}}"));
    // owlyFrame.addBackground(RegionRenders.create(region));
    // owlyFrame.addBackground(RegionRenders.create(sphericalRegion));
    // owlyFrame.addBackground(RenderElements.create(stateTimeRaster));
    // owlyFrame.addBackground(RenderElements.create(plannerConstraint));
    // owlyFrame.addBackground(new DomainRender(trajectoryPlanner.getDomainMap(), eta));
    // ---
    System.out.println("Initial starting point: " + INITIAL);
    System.out.println("Final desired point: " + ApTrajectoryPlanner.GOAL);
    // ---
    standardTrajectoryPlanner.insertRoot(new StateTime(INITIAL, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(standardTrajectoryPlanner);
    glcExpand.findAny(15000);
    Optional<GlcNode> optional = standardTrajectoryPlanner.getBest();
    // ---
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    // ---
    if (optional.isPresent()) {
      System.out.println(1);
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    owlyFrame.setGlc(standardTrajectoryPlanner);
    // OwlyFrame owlyFrame2 = OwlyGui.glc(standardTrajectoryPlanner);
  }
}
