// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.OwlyGui;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.Region;
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

/** position and velocity control in 1D with friction
 * 
 * References:
 * "Mobility and Autonomous Reconfiguration of Marsokhod" */
enum Rice1dDemo {
  ;
  public static TrajectoryPlanner simple() {
    Tensor eta = Tensors.vector(8, 8);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, RationalScalar.of(1, 8), 5);
    Collection<Flow> controls = Rice2Controls.create1d(RealScalar.of(-0.5), 15); //
    GoalInterface goalInterface = Rice1GoalManager.create(Tensors.vector(6, -.7), Tensors.vector(0.4, 0.3));
    Region<Tensor> region1 = new EllipsoidRegion(Tensors.vector(+3, +1), Tensors.vector(1.75, 0.75));
    Region<Tensor> region2 = new EllipsoidRegion(Tensors.vector(-2, +0), Tensors.vector(1, 1));
    TrajectoryRegionQuery obstacleQuery = //
        SimpleTrajectoryRegionQuery.timeInvariant( //
            RegionUnion.wrap(Arrays.asList( //
                region1, // speed limit along the way
                region2 // block to the left
            )));
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta, stateIntegrator, controls, obstacleQuery, goalInterface);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    int iters = Expand.maxSteps(trajectoryPlanner, 1000);
    if (900 < iters)
      System.out.println("close to upper bound: " + iters);
    return trajectoryPlanner;
  }

  public static void main(String[] args) {
    TrajectoryPlanner trajectoryPlanner = simple();
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    OwlyGui.glc(trajectoryPlanner);
  }
}
