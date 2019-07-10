// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.List;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.HeuristicAssert;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.io.Timing;
import junit.framework.TestCase;

public class Rice2dDemoTest extends TestCase {
  public void testExpand() throws InterruptedException {
    TrajectoryPlanner trajectoryPlanner = Rice2dDemo.createInstance();
    Timing timing = Timing.started();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000); // 153 0.368319228
    assertTrue(timing.seconds() < 1.5);
    assertTrue(glcExpand.getExpandCount() < 500);
    OwlyFrame owlyFrame = OwlyGui.glc(trajectoryPlanner);
    GlcNode glcNode = trajectoryPlanner.getBest().get();
    GlcNodes.getPathFromRootTo(glcNode);
    List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(Rice2dDemo.STATE_INTEGRATOR, glcNode);
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.trajectory(samples);
    owlyFrame.addBackground(trajectoryRender);
    owlyFrame.addBackground(RegionRenders.create(Rice2dDemo.ELLIPSOID_REGION));
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
    Thread.sleep(120);
    owlyFrame.jFrame.setVisible(false);
  }

  public void testGlcExpand() throws InterruptedException {
    TrajectoryPlanner trajectoryPlanner = Rice2dDemo.createInstance();
    Timing timing = Timing.started();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.untilOptimal(1000); // 220 0.283809941
    assertTrue(timing.seconds() < 1.5);
    assertTrue(glcExpand.getExpandCount() < 500);
    OwlyFrame owlyFrame = OwlyGui.glc(trajectoryPlanner);
    GlcNode glcNode = trajectoryPlanner.getBest().get();
    GlcNodes.getPathFromRootTo(glcNode);
    List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(Rice2dDemo.STATE_INTEGRATOR, glcNode);
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.trajectory(samples);
    owlyFrame.addBackground(trajectoryRender);
    owlyFrame.addBackground(RegionRenders.create(Rice2dDemo.ELLIPSOID_REGION));
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
    Thread.sleep(120);
    owlyFrame.jFrame.setVisible(false);
  }
}
