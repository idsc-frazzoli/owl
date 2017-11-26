// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.rn.RnMinDistSphericalGoalManager;
import ch.ethz.idsc.owl.bot.rn.RnPointclouds;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
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
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

enum R2PointsDemo {
  ;
  public static void main(String[] args) {
    Tensor partitionScale = Tensors.vector(5, 5);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.of(1, 8), 4);
    R2Flows r2Config = new R2Flows(RealScalar.ONE);
    Collection<Flow> controls = r2Config.getFlows(20);
    SphericalRegion sphericalRegion = new SphericalRegion(Tensors.vector(5, 5), DoubleScalar.of(0.2));
    GoalInterface goalInterface = new RnMinDistSphericalGoalManager(sphericalRegion);
    Region<Tensor> region = RnPointclouds.createRandomRegion(10, Tensors.vector(0, 0), Tensors.vector(4, 4), RealScalar.of(0.6));
    TrajectoryRegionQuery obstacleQuery = SimpleTrajectoryRegionQuery.timeInvariant(region);
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        partitionScale, stateIntegrator, controls, obstacleQuery, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(-0.5, -0.5), RealScalar.ZERO));
    int iters = Expand.maxSteps(trajectoryPlanner, 1000);
    System.out.println(iters);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    OwlyFrame owlyFrame = OwlyGui.glc(trajectoryPlanner);
    owlyFrame.addBackground(RegionRenders.create(region));
    owlyFrame.addBackground(RegionRenders.create(sphericalRegion));
  }
}
