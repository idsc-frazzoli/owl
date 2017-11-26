// code by jl
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.CarFlows;
import ch.ethz.idsc.owl.bot.se2.CarStandardFlows;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.par.Parameters;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.OwlyGui;
import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.HyperplaneRegion;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** (x,y,theta) */
enum Se2GlcDemo {
  ;
  public static void main(String[] args) throws Exception {
    RationalScalar resolution = (RationalScalar) RealScalar.of(9);
    Scalar timeScale = RealScalar.of(10);
    Scalar depthScale = RealScalar.of(5);
    Tensor partitionScale = Tensors.vector(3, 3, 50 / Math.PI);
    Scalar dtMax = RationalScalar.of(1, 6);
    int maxIter = 2000;
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    // --
    Parameters parameters = new Se2Parameters( //
        resolution, timeScale, depthScale, partitionScale, dtMax, maxIter, stateSpaceModel.getLipschitz());
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        Se2CarIntegrator.INSTANCE, //
        parameters.getdtMax(), //
        parameters.getTrajectorySize());
    // ---
    System.out.println("scale=" + parameters.getEta());
    parameters.printResolution();
    CarFlows carFlows = new CarStandardFlows(RealScalar.ONE, Degree.of(45));
    Collection<Flow> controls = carFlows.getFlows(parameters.getResolutionInt());
    GoalInterface goalInterface = Se2MinTimeGoalManager.create( //
        Tensors.vector(0, 1, Math.PI), //
        Tensors.vector(0.1, 0.1, 10 / 180 * Math.PI), //
        controls);
    TrajectoryRegionQuery obstacleQuery = SimpleTrajectoryRegionQuery.timeInvariant( //
        RegionUnion.wrap(Arrays.asList( //
            new HyperplaneRegion(Tensors.vector(0, -1, 0), RealScalar.of(1.5)), //
            new HyperplaneRegion(Tensors.vector(0, +1, 0), RealScalar.of(1.5)) //
        )));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        parameters.getEta(), stateIntegrator, controls, obstacleQuery, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(1, 0, -0.5 * Math.PI), RealScalar.ZERO));
    int iters = GlcExpand.maxDepth(trajectoryPlanner, parameters.getDepthLimit());
    // int iters = Expand.maxTime(trajectoryPlanner, RealScalar.of(3));
    System.out.println("After " + iters + " iterations");
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    OwlyGui.glc(trajectoryPlanner);
  }
}
