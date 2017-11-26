// code by jph
package ch.ethz.idsc.owl.bot.ip;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owl.math.region.FreeBoundedIntervalRegion;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

/** inverted pendulum */
/* package */ class IpDemo {
  public static void main(String[] args) {
    Tensor eta = Tensors.vector(10, 10, 10, 10);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, RationalScalar.of(1, 12), 5);
    StateSpaceModel stateSpaceModel = new IpStateSpaceModel( //
        RealScalar.of(0.3), // M
        RealScalar.of(0.2), // m
        RealScalar.of(0.5), // l
        RealScalar.of(1)); // g;
    Collection<Flow> controls = IpControls.createControls(stateSpaceModel, 2, 10);
    IpGoalManager ipGoalManager = new IpGoalManager( //
        Tensors.vector(2, 0, 0, 0), //
        Tensors.vector(0.1, 0.1, 1, 1));
    TrajectoryRegionQuery obstacleQuery =
        // new EmptyTrajectoryRegionQuery();
        SimpleTrajectoryRegionQuery.timeInvariant( //
            RegionUnion.wrap(Arrays.asList( //
                new FreeBoundedIntervalRegion(0, RealScalar.of(-1), RealScalar.of(+3)), // ,
                new FreeBoundedIntervalRegion(2, RealScalar.of(-2), RealScalar.of(+2)) // ,
            )));
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta, stateIntegrator, controls, obstacleQuery, ipGoalManager);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(4), RealScalar.ZERO));
    // new ExpandGlcFrame(trajectoryPlanner);
    int iters = Expand.maxSteps(trajectoryPlanner, 3000);
    System.out.println(iters);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    OwlyGui.glc(trajectoryPlanner);
  }
}
