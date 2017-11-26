// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.CarFlows;
import ch.ethz.idsc.owl.bot.se2.CarStandardFlows;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.bot.se2.Se2WrapGoalManagerExt;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.adapter.IdentityWrap;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** Se2WrapDemoExt uses a different goal manager than Se2WrapDemo
 * 
 * (x,y,theta) */
enum Se2WrapDemoExt {
  ;
  public static void main(String[] args) {
    Tensor eta = Tensors.vector(3, 3, 50 / Math.PI);
    Tensor radiusVector = Tensors.of(DoubleScalar.of(0.2), DoubleScalar.of(0.2), Degree.of(15));
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        Se2CarIntegrator.INSTANCE, //
        RationalScalar.of(1, 6), 5);
    System.out.println("scale=" + eta);
    CarFlows carFlows = new CarStandardFlows(RealScalar.ONE, Degree.of(45));
    Collection<Flow> controls = carFlows.getFlows(6);
    final CoordinateWrap identity = IdentityWrap.INSTANCE;
    CoordinateWrap coordinateWrap;
    coordinateWrap = new Se2Wrap(Tensors.vector(1, 1, 1));
    coordinateWrap = identity;
    Se2MinTimeGoalManager se2DefaultGoalManager = new Se2MinTimeGoalManager( //
        Tensors.vector(-.5, 0, 0), radiusVector, controls);
    Se2WrapGoalManagerExt se2WrapGoalManager = new Se2WrapGoalManagerExt( //
        coordinateWrap, se2DefaultGoalManager);
    TrajectoryRegionQuery obstacleQuery = Se2WrapDemo.obstacleQuery();
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta, stateIntegrator, controls, obstacleQuery, se2WrapGoalManager.getGoalInterface());
    trajectoryPlanner.represent = StateTimeTensorFunction.state(coordinateWrap::represent);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0.1, 0, 0), RealScalar.ZERO));
    int iters = Expand.maxSteps(trajectoryPlanner, 4000);
    System.out.println(iters);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    OwlyGui.glc(trajectoryPlanner);
  }
}
