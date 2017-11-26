// code by jph
package ch.ethz.idsc.owl.bot.rnd;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.rn.R2NoiseRegion;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.OwlyFrame;
import ch.ethz.idsc.owl.gui.ani.OwlyGui;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

enum R2dNoiseDemo {
  ;
  // TODO demo needs a revision
  public static void main(String[] args) {
    Tensor partitionScale = Tensors.vector(6, 6, 6, 6);
    final Scalar threshold = RealScalar.of(0.2);
    Region<Tensor> region = RndOrRegion.common(new R2NoiseRegion(threshold));
    StateIntegrator stateIntegrator = //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.of(1, 8), 3);
    Collection<Flow> controls = R2dControls.createRadial(5);
    final Tensor center = Tensors.vector(3, 0);
    final Scalar radius = DoubleScalar.of(0.2);
    GoalInterface goalInterface = //
        new RndMinDistSphericalGoalManager(center, radius);
    TrajectoryRegionQuery obstacleQuery = SimpleTrajectoryRegionQuery.timeInvariant(region);
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        partitionScale, stateIntegrator, controls, obstacleQuery, goalInterface);
    // Trajectories.s
    StateTime root_x = new StateTime(Tensors.vector(0, 0, 0, 0), RealScalar.ZERO);
    boolean root_free = !obstacleQuery.isMember(root_x);
    GlobalAssert.that(root_free);
    trajectoryPlanner.insertRoot(root_x);
    Stopwatch stopwatch = Stopwatch.started();
    int iters = Expand.maxSteps(trajectoryPlanner, 1000);
    System.out.println(iters + " " + stopwatch.display_seconds());
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    OwlyFrame owlyFrame = OwlyGui.glc(trajectoryPlanner);
    owlyFrame.configCoordinateOffset(100, 300);
  }
}
