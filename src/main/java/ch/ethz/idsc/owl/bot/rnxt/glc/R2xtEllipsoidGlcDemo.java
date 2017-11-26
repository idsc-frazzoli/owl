// code by jl
package ch.ethz.idsc.owl.bot.rnxt.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.rn.R2Flows;
import ch.ethz.idsc.owl.bot.rn.R2Parameters;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.par.Parameters;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.OwlyFrame;
import ch.ethz.idsc.owl.gui.ani.OwlyGui;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

enum R2xtEllipsoidGlcDemo {
  ;
  public static void main(String[] args) {
    RationalScalar resolution = (RationalScalar) RealScalar.of(8);
    Tensor partitionScale = Tensors.vector(25, 25, 64);
    Scalar timeScale = RealScalar.of(6);
    Scalar depthScale = RealScalar.of(100);
    Scalar dtMax = RationalScalar.of(1, 6);
    int maxIter = 1000000;
    Scalar lipschitz = RealScalar.ONE;
    Parameters parameters = new R2Parameters(resolution, timeScale, depthScale, partitionScale, dtMax, maxIter, lipschitz);
    System.out.println("1/DomainSize: " + parameters.getEta());
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        EulerIntegrator.INSTANCE, parameters.getdtMax(), parameters.getTrajectorySize());
    R2Flows r2Config = new R2Flows(RealScalar.ONE);
    Collection<Flow> controls = r2Config.getFlows(parameters.getResolutionInt());
    Tensor goal = Tensors.vector(5, 5);
    EllipsoidRegion ellipsoidRegion = new EllipsoidRegion(goal, Tensors.vector(0.2, 0.2));
    GoalInterface goalInterface = new RnHeuristicEllipsoidGoalManager(ellipsoidRegion);
    // with HeuristicGoalManager only expand 10% of nodes
    // GoalRegion at x:5, y= 5 and all time
    TrajectoryRegionQuery obstacleQuery = SimpleTrajectoryRegionQuery.timeDependent( //
        new EllipsoidRegion(goal.copy().append(RealScalar.ZERO), Tensors.vector(3, 3, 10)));
    // ObstacleEllipsoid around goal, which disappears after 10s
    StateTime root = new StateTime(Tensors.vector(0, 0), RealScalar.ZERO);
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        parameters.getEta(), stateIntegrator, controls, obstacleQuery, goalInterface);
    trajectoryPlanner.represent = StateTimeTensorFunction.withTime();
    trajectoryPlanner.insertRoot(root);
    int iters = Expand.maxSteps(trajectoryPlanner, maxIter);
    System.out.println(iters);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    OwlyFrame owlyFrame = OwlyGui.glc(trajectoryPlanner);
    owlyFrame.addBackground(RegionRenders.create(ellipsoidRegion));
    owlyFrame.configCoordinateOffset(250, 500);
  }
}
