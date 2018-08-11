// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.HyperplaneRegion;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** position and velocity control in 2D with friction */
enum Rice2dDemo {
  ;
  static final StateIntegrator STATE_INTEGRATOR = FixedStateIntegrator.create( //
      MidpointIntegrator.INSTANCE, RationalScalar.HALF, 5);
  static final EllipsoidRegion ELLIPSOID_REGION = //
      new EllipsoidRegion(Tensors.vector(3, 3, -1, 0), Tensors.vector(0.5, 0.5, 0.4, 0.4));

  public static TrajectoryPlanner createInstance() {
    Tensor eta = Tensors.vector(3, 3, 6, 6);
    Collection<Flow> controls = Rice2Controls.create2d(RealScalar.of(-.5), 1).getFlows(15);
    GoalInterface goalInterface = new Rice2GoalManager(ELLIPSOID_REGION);
    PlannerConstraint plannerConstraint = //
        new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(RegionUnion.wrap(Arrays.asList( //
            new HyperplaneRegion(Tensors.vector(1, +0, 0, 0), RealScalar.ZERO), //
            new HyperplaneRegion(Tensors.vector(0, +1, 0, 0), RealScalar.ZERO), //
            new HyperplaneRegion(Tensors.vector(0, -1, 0, 0), RealScalar.of(3.2)), //
            new HyperplaneRegion(Tensors.vector(0, +0, 0, 1), RealScalar.ZERO) //
        ))));
    // ---
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, STATE_INTEGRATOR, controls, plannerConstraint, goalInterface);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0.1, 0.1, 0, 0), RealScalar.ZERO));
    return trajectoryPlanner;
  }

  // Hint: ensure that goal region contains at least 1 domain etc.
  public static void main(String[] args) {
    TrajectoryPlanner trajectoryPlanner = createInstance();
    Stopwatch stopwatch = Stopwatch.started();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    // 550 1.6898229210000002 without parallel integration of trajectories
    // 555 1.149214356 with parallel integration of trajectories
    System.out.println(glcExpand.getExpandCount() + " " + stopwatch.display_seconds());
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    OwlyFrame owlyFrame = OwlyGui.glc(trajectoryPlanner);
    if (optional.isPresent()) {
      GlcNode glcNode = optional.get();
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(glcNode);
      StateTimeTrajectories.print(trajectory);
      List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(STATE_INTEGRATOR, glcNode);
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(samples);
      owlyFrame.addBackground(trajectoryRender);
    }
    glcExpand.untilOptimal(1000);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      GlcNode glcNode = optional.get();
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(glcNode);
      StateTimeTrajectories.print(trajectory);
      List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(STATE_INTEGRATOR, glcNode);
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(samples);
      owlyFrame.addBackground(trajectoryRender);
    }
    owlyFrame.addBackground(RegionRenders.create(ELLIPSOID_REGION));
  }
}
