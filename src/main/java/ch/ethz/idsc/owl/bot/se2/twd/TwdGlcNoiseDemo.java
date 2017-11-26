// code by jl
package ch.ethz.idsc.owl.bot.se2.twd;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.rn.R2NoiseRegion;
import ch.ethz.idsc.owl.bot.se2.Se2LateralAcceleration;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.par.Parameters;
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
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

enum TwdGlcNoiseDemo {
  ;
  public static void main(String[] args) {
    RationalScalar resolution = (RationalScalar) RealScalar.of(6);
    Scalar timeScale = RealScalar.of(32);
    Scalar depthScale = RealScalar.of(20);
    Tensor partitionScale = Tensors.vector(5, 5, 2 * Math.PI / 360 * 10);
    Scalar dtMax = RationalScalar.of(1, 10);
    int maxIter = 2000;
    Parameters parameters = new TwdParameters( //
        resolution, timeScale, depthScale, partitionScale, dtMax, maxIter, RealScalar.ONE); // TODO check lipschitz
    parameters.printResolution();
    System.out.println("DomainSize: 1/Eta: " + parameters.getEta().map(n -> RealScalar.ONE.divide(n)));
    Region<Tensor> region = new R2NoiseRegion(RealScalar.of(0.1));
    StateIntegrator stateIntegrator = //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, parameters.getdtMax(), parameters.getTrajectorySize());
    // Controls
    TwdDuckieFlows twdControls = new TwdDuckieFlows(RealScalar.ONE, RealScalar.ONE);
    Collection<Flow> controls = twdControls.getFlows(parameters.getResolutionInt());
    // GoalRegion
    Tensor goalCenter = Tensors.vector(5.5, 0, -1 * Math.PI);
    Tensor radiusVector = Tensors.vector(0.2, 0.2, 50 * Math.PI / 180);
    GoalInterface goalInterface = MultiCostGoalAdapter.of( //
        Se2MinTimeGoalManager.create(goalCenter, radiusVector, controls), //
        Arrays.asList(Se2LateralAcceleration.COSTFUNCTION));
    // ObstacleRegion
    TrajectoryRegionQuery obstacleQuery = SimpleTrajectoryRegionQuery.timeInvariant(region);
    // Planner init
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        partitionScale, stateIntegrator, controls, obstacleQuery, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(3), RealScalar.ZERO));
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(200, 300);
    owlyFrame.jFrame.setBounds(100, 100, 620, 475);
    owlyFrame.setGlc(trajectoryPlanner);
    // Planning
    while (!trajectoryPlanner.getBest().isPresent() && owlyFrame.jFrame.isVisible()) {
      GlcExpand.maxSteps(trajectoryPlanner, 100, parameters.getDepthLimit());
      owlyFrame.setGlc(trajectoryPlanner);
    }
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
  }
}
